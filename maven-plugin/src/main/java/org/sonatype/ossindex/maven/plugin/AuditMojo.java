/*
 * Copyright (c) 2018-present Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.sonatype.ossindex.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.sonatype.ossindex.maven.common.ComponentReportAssistant;
import org.sonatype.ossindex.maven.common.ComponentReportRequest;
import org.sonatype.ossindex.maven.common.ComponentReportResult;
import org.sonatype.ossindex.maven.common.MavenCoordinates;
import org.sonatype.ossindex.maven.common.Version;
import org.sonatype.ossindex.maven.plugin.export.Exporter;
import org.sonatype.ossindex.service.client.OssindexClientConfiguration;
import org.sonatype.ossindex.service.client.transport.AuthConfiguration;
import org.sonatype.ossindex.service.client.transport.ProxyConfiguration;
import org.sonatype.ossindex.service.client.transport.UserAgentBuilder.Product;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.CumulativeScopeArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;

import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;

/**
 * Vulnerability audit of project dependencies via <a href="https://ossindex.sonatype.org/">Sonatype OSS Index</a>.
 *
 * @since 3.0.0
 */
@Mojo(name = "audit", requiresDependencyResolution = TEST)
public class AuditMojo
    extends AbstractMojo
{
  @Component
  private DependencyGraphBuilder dependencyGraphBuilder;

  @Component
  private ComponentReportAssistant reportAssistant;

  @Component
  private List<Exporter> exporters;

  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  @Parameter(defaultValue = "${session}", readonly = true)
  private MavenSession session;

  @Parameter(defaultValue = "${settings}", readonly = true)
  private Settings settings;

  @Parameter(defaultValue = "${maven.version}", readonly = true)
  private String mavenVersion;

  /**
   * Skip execution.
   */
  @Parameter(property = "ossindex.skip", defaultValue = "false")
  private boolean skip = false;

  /**
   * Limit scope of dependency resolution.
   */
  @Nullable
  @Parameter(property = "ossindex.scope")
  private String scope;

  /**
   * Include transitive dependencies.
   */
  @Parameter(property = "ossindex.transitive", defaultValue = "true")
  private boolean transitive = true;

  /**
   * When vulnerable components are found; fail the build.
   */
  @Parameter(property = "ossindex.fail", defaultValue = "true")
  private boolean fail = true;

  /**
   * <a href="https://ossindex.sonatype.org/">Sonatype OSS Index</a>
   * <a href="https://sonatype.github.io/ossindex-public/maven/apidocs/org/sonatype/ossindex/service/client/OssindexClientConfiguration.html">client configuration</a>.
   */
  @Parameter
  private OssindexClientConfiguration clientConfiguration = new OssindexClientConfiguration();

  // FIXME: we need more adaption to ^^^ to bridge configuration when run via CLI instead of execution

  /**
   * Override <a href="https://ossindex.sonatype.org/">Sonatype OSS Index</a> service base-URL.
   */
  @Nullable
  @Parameter(property = "ossindex.baseUrl")
  private URI baseUrl;

  /**
   * Set client authentication from Maven settings server configuration.
   */
  @Nullable
  @Parameter(property = "ossindex.authId")
  private String authId;

  /**
   * Set of coordinates to exclude from vulnerability matching.
   */
  @Parameter
  private Set<MavenCoordinates> excludeCoordinates = new HashSet<>();

  /**
   * Set {@link #excludeCoordinates} from a comma-separated list.
   */
  @Nullable
  @Parameter(property = "ossindex.excludeCoordinates")
  private String excludeCoordinatesCsv;

  /**
   * CVSS-score threshold.
   *
   * Vulnerabilities with lower scores will be excluded.
   */
  @Parameter(property = "ossindex.cvssScoreThreshold", defaultValue = "0")
  private float cvssScoreThreshold = 0;

  /**
   * Set of <a href="https://ossindex.sonatype.org/">Sonatype OSS Index</a>
   * vulnerability identifiers to exclude from matching.
   */
  @Parameter
  private Set<String> excludeVulnerabilityIds = new HashSet<>();

  /**
   * Set {@link #excludeVulnerabilityIds} from a comma-separated list.
   */
  @Nullable
  @Parameter(property = "ossindex.excludeVulnerabilityIds")
  private String excludeVulnerabilityIdsCsv;

  /**
   * Export component-report to file.
   *
   * Supports {@code .json}, {@code .xml} and {@code .txt} extensions.
   */
  @Nullable
  @Parameter(property = "ossindex.reportFile")
  private File reportFile;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (skip) {
      getLog().info("Skipping; configured");
      return;
    }
    if (session.isOffline()) {
      getLog().info("Skipping; offline");
      return;
    }
    if ("pom".equals(project.getPackaging())) {
      getLog().info("Skipping; POM module");
      return;
    }

    // determine dependencies
    Set<Artifact> dependencies;
    try {
      dependencies = resolveDependencies(session);
    }
    catch (DependencyGraphBuilderException e) {
      throw new MojoExecutionException("Failed to resolve dependencies", e);
    }

    // skip if project has no dependencies
    if (dependencies.isEmpty()) {
      getLog().info("Skipping; zero dependencies");
      return;
    }

    // adapt client configuration
    if (baseUrl != null) {
      clientConfiguration.setBaseUrl(baseUrl);
    }

    // adapt client authentication
    maybeApplyAuth(authId, clientConfiguration);

    // adapt string-list configuration forms
    if (excludeCoordinatesCsv != null) {
      excludeCoordinates.addAll(MavenCoordinates.parseList(excludeCoordinatesCsv));
    }
    if (excludeVulnerabilityIdsCsv != null) {
      List<String> ids = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(excludeVulnerabilityIdsCsv);
      excludeVulnerabilityIds.addAll(ids);
    }

    // adapt maven http-proxy settings to client configuration
    maybeApplyProxy(clientConfiguration);

    ComponentReportRequest reportRequest = new ComponentReportRequest();
    reportRequest.setProducts(ImmutableList.of(
        new Product("Maven", mavenVersion),
        new Product("Maven-Plugin", Version.get().getVersion())
    ));
    reportRequest.setClientConfiguration(clientConfiguration);
    reportRequest.setCvssScoreThreshold(cvssScoreThreshold);
    reportRequest.setExcludeCoordinates(excludeCoordinates);
    reportRequest.setExcludeVulnerabilityIds(excludeVulnerabilityIds);
    reportRequest.setComponents(dependencies);

    ComponentReportResult reportResult = reportAssistant.request(reportRequest);

    // maybe export report
    try {
      if (reportFile != null) {
        export(reportResult, reportFile);
      }
    }
    catch (IOException e) {
      throw new MojoExecutionException("Failed to export report: " + reportFile, e);
    }

    // maybe fail or warn
    if (reportResult.hasVulnerable()) {
      if (fail) {
        throw new MojoFailureException(reportResult.explain());
      }
      else {
        getLog().warn(reportResult.explain());
      }
    }
  }

  //
  // Client auth from settings; some duplication due to maven-version mismatch
  //

  @SuppressWarnings("Duplicates")
  private void maybeApplyAuth(@Nullable final String serverId, final OssindexClientConfiguration clientConfiguration) {
    if (serverId != null) {
      Server server = settings.getServer(serverId);
      if (server == null) {
        getLog().warn("Missing configuration for server-id: " + serverId);
      }
      else {
        String username = Strings.emptyToNull(server.getUsername());
        String password = Strings.emptyToNull(server.getPassword());
        if (username != null && password != null) {
          clientConfiguration.setAuthConfiguration(new AuthConfiguration(server.getUsername(), server.getPassword()));
        }
        else {
          getLog().warn("Configuration for server-id: " + serverId + "; missing username and/or password");
        }
      }
    }
  }

  //
  // HTTP-proxy support; some duplication due to maven-version mismatch
  //

  /**
   * Detect proxy configuration from Maven settings.
   */
  @SuppressWarnings("Duplicates")
  @Nullable
  private ProxyConfiguration detectProxy() {
    Proxy proxy = settings.getActiveProxy();
    if (proxy != null) {
      ProxyConfiguration config = new ProxyConfiguration();
      config.setProtocol(proxy.getProtocol());
      config.setHost(proxy.getHost());
      config.setPort(proxy.getPort());
      config.setNonProxyHosts(proxy.getNonProxyHosts());

      // maybe add authentication if username & password are present
      String username = Strings.emptyToNull(proxy.getUsername());
      String password = Strings.emptyToNull(proxy.getPassword());
      if (username != null && password != null) {
        config.setAuthConfiguration(new AuthConfiguration(username, password));
      }
      return config;
    }
    return null;
  }

  /**
   * If a proxy configuration was detected then configure client, unless client has this configured already.
   */
  private void maybeApplyProxy(final OssindexClientConfiguration clientConfiguration) {
    if (clientConfiguration.getProxyConfiguration() == null) {
      ProxyConfiguration proxy = detectProxy();
      if (proxy != null) {
        clientConfiguration.setProxyConfiguration(proxy);
      }
    }
  }

  //
  // Dependency resolution; some duplication due to maven-version mismatch
  //

  /**
   * Resolve dependencies to inspect for vulnerabilities.
   */
  private Set<Artifact> resolveDependencies(final MavenSession session) throws DependencyGraphBuilderException {
    Set<Artifact> dependencies = new HashSet<>();

    ProjectBuildingRequest request = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
    request.setProject(project);

    ArtifactFilter artifactFilter = null;
    if (scope != null) {
      List<String> scopes = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(scope);
      artifactFilter = new CumulativeScopeArtifactFilter(scopes);
    }

    DependencyNode root = dependencyGraphBuilder.buildDependencyGraph(request, artifactFilter);
    collectArtifacts(dependencies, root);

    return dependencies;
  }

  /**
   * Collect artifacts from dependency.
   *
   * Optionally including transitive dependencies if {@link #transitive} is {@code true}.
   */
  @SuppressWarnings("Duplicates")
  private void collectArtifacts(final Set<Artifact> artifacts, final DependencyNode node) {
    if (node.getChildren() != null) {
      for (DependencyNode child : node.getChildren()) {
        artifacts.add(child.getArtifact());

        if (transitive) {
          collectArtifacts(artifacts, child);
        }
      }
    }
  }

  //
  // Export
  //

  /**
   * Export given report results to file.
   */
  private void export(final ComponentReportResult result, final File file) throws IOException {
    getLog().info("Exporting results to: " + file);

    Exporter exporter = selectExporter(file);
    if (exporter == null) {
      getLog().warn("Unsupported export file: " + file);
      return;
    }

    getLog().debug("Selected exporter: " + exporter);
    exporter.export(result, file);
  }

  /**
   * Select the exporter to use for given file.
   */
  @Nullable
  private Exporter selectExporter(final File file) {
    for (Exporter exporter : exporters) {
      if (exporter.accept(file)) {
        return exporter;
      }
    }
    return null;
  }
}
