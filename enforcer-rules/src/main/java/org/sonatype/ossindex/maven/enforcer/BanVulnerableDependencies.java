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
package org.sonatype.ossindex.maven.enforcer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.sonatype.ossindex.maven.common.ComponentReportAssistant;
import org.sonatype.ossindex.maven.common.ComponentReportRequest;
import org.sonatype.ossindex.maven.common.ComponentReportResult;
import org.sonatype.ossindex.maven.common.MavenCoordinates;
import org.sonatype.ossindex.service.client.AuthConfiguration;
import org.sonatype.ossindex.service.client.OssindexClientConfiguration;
import org.sonatype.ossindex.service.client.ProxyConfiguration;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.CumulativeScopeArtifactFilter;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Proxy;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;

/**
 * Enforcer rule to ban vulnerable dependencies.
 *
 * @since 3.0.0
 */
public class BanVulnerableDependencies
    extends EnforcerRuleSupport
{
  private OssindexClientConfiguration clientConfiguration = new OssindexClientConfiguration();

  private String authId;

  private String scope;

  private boolean transitive = true;

  private Set<MavenCoordinates> excludeCoordinates = new HashSet<>();

  private float cvssScoreThreshold = 0;

  private Set<String> excludeVulnerabilityIds = new HashSet<>();

  /**
   * <a href="https://ossindex.sonatype.org/">Sonatype OSS Index</a> client configuration.
   */
  @SuppressWarnings("unused")
  public void setClientConfiguration(final OssindexClientConfiguration clientConfiguration) {
    this.clientConfiguration = clientConfiguration;
  }

  /**
   * Set client authentication from Maven settings server configuration.
   */
  public void setAuthId(final String authId) {
    this.authId = authId;
  }

  /**
   * Limit scope of dependency resolution.
   */
  @SuppressWarnings("unused")
  public void setScope(final String scope) {
    this.scope = scope;
  }

  /**
   * Include transitive dependencies.
   */
  @SuppressWarnings("unused")
  public void setTransitive(final boolean transitive) {
    this.transitive = transitive;
  }

  /**
   * Set of coordinates to exclude from vulnerability matching.
   */
  @SuppressWarnings("unused")
  public void setExcludeCoordinates(final Set<MavenCoordinates> excludeCoordinates) {
    this.excludeCoordinates = excludeCoordinates;
  }

  /**
   * CVSS-score threshold.
   *
   * Vulnerabilities with lower scores will be excluded.
   */
  @SuppressWarnings("unused")
  public void setCvssScoreThreshold(final float cvssScoreThreshold) {
    this.cvssScoreThreshold = cvssScoreThreshold;
  }

  /**
   * Set of <a href="https://ossindex.sonatype.org/">Sonatype OSS Index</a>
   * vulnerability identifiers to exclude from matching.
   */
  @SuppressWarnings("unused")
  public void setExcludeVulnerabilityIds(final Set<String> excludeVulnerabilityIds) {
    this.excludeVulnerabilityIds = excludeVulnerabilityIds;
  }

  @Override
  public void execute(@Nonnull final EnforcerRuleHelper helper) throws EnforcerRuleException {
    new Task(helper).run();
  }

  /**
   * Encapsulates state for rule evaluation.
   */
  private class Task
  {
    private final Log log;

    private final MavenSession session;

    private final MavenProject project;

    private final Settings settings;

    private final DependencyGraphBuilder graphBuilder;

    private final ComponentReportAssistant reportAssistant;

    public Task(final EnforcerRuleHelper helper) {
      this.log = helper.getLog();
      this.session = lookup(helper, "${session}", MavenSession.class);
      this.project = lookup(helper, "${project}", MavenProject.class);
      this.settings = lookup(helper, "${settings}", Settings.class);
      this.graphBuilder = lookup(helper, DependencyGraphBuilder.class);
      this.reportAssistant = lookup(helper, ComponentReportAssistant.class);
    }

    public void run() throws EnforcerRuleException {
      // skip if maven is in offline mode
      if (session.isOffline()) {
        log.warn(skipReason("offline"));
        return;
      }

      // skip if packaging is pom
      if ("pom".equals(project.getPackaging())) {
        log.debug(skipReason("POM module"));
        return;
      }

      // determine dependencies
      Set<Artifact> dependencies;
      try {
        dependencies = resolveDependencies();
      }
      catch (DependencyGraphBuilderException e) {
        throw new RuntimeException("Failed to resolve dependencies", e);
      }

      // skip if project has no dependencies
      if (dependencies.isEmpty()) {
        log.debug(skipReason("zero dependencies"));
        return;
      }

      // adapt client authentication
      maybeApplyAuth(authId, clientConfiguration);

      // adapt maven http-proxy settings to client configuration
      maybeApplyProxy(clientConfiguration);

      ComponentReportRequest reportRequest = new ComponentReportRequest();
      reportRequest.setClientConfiguration(clientConfiguration);
      reportRequest.setExcludeCoordinates(excludeCoordinates);
      reportRequest.setExcludeVulnerabilityIds(excludeVulnerabilityIds);
      reportRequest.setCvssScoreThreshold(cvssScoreThreshold);
      reportRequest.setComponents(dependencies);

      ComponentReportResult reportResult = reportAssistant.request(reportRequest);

      if (reportResult.hasVulnerable()) {
        throw new EnforcerRuleException(reportResult.explain());
      }
    }

    /**
     * Generate skip reason message; includes rule simple-name for clarity.
     */
    private String skipReason(final String reason) {
      return String.format("Skipping %s; %s", BanVulnerableDependencies.class.getSimpleName(), reason);
    }

    //
    // Client auth from settings; some duplication due to maven-version mismatch
    //

    @SuppressWarnings("Duplicates")
    private void maybeApplyAuth(@Nullable final String serverId, final OssindexClientConfiguration clientConfiguration) {
      if (serverId != null) {
        Server server = settings.getServer(serverId);
        if (server == null) {
          log.warn("Missing configuration for server-id: " + serverId);
        }
        else {
          clientConfiguration.setAuthConfiguration(new AuthConfiguration(server.getUsername(), server.getPassword()));
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
    private Set<Artifact> resolveDependencies() throws DependencyGraphBuilderException {
      Set<Artifact> result = new HashSet<>();

      ArtifactFilter artifactFilter = null;
      if (scope != null) {
        List<String> scopes = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(scope);
        artifactFilter = new CumulativeScopeArtifactFilter(scopes);
      }

      DependencyNode node = graphBuilder.buildDependencyGraph(project, artifactFilter);
      collectArtifacts(result, node);

      return result;
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
  }
}
