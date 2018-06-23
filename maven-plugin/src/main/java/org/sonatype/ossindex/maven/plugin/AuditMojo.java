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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.sonatype.ossindex.maven.common.ComponentReportAssistant;
import org.sonatype.ossindex.maven.common.ComponentReportRequest;
import org.sonatype.ossindex.maven.common.ComponentReportResult;
import org.sonatype.ossindex.service.client.OssindexClientConfiguration;

import com.google.common.base.Splitter;
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
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;

import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;

/**
 * Component-report {@code audit} goal.
 *
 * @since ???
 */
@Mojo(name = "audit", requiresDependencyResolution = TEST)
public class AuditMojo
    extends AbstractMojo
{
  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  @Parameter(defaultValue = "${session}", readonly = true)
  private MavenSession session;

  @Parameter(property = "ossindex.skip", defaultValue = "false")
  private boolean skip = false;

  @Nullable
  @Parameter(property = "ossindex.scope")
  private String scope;

  @Parameter(property = "ossindex.transitive", defaultValue = "true")
  private boolean transitive = true;

  // TODO: bridge exclusion configuration

  @Component
  private DependencyGraphBuilder dependencyGraphBuilder;

  @Parameter
  private OssindexClientConfiguration clientConfiguration = new OssindexClientConfiguration();

  @Component
  private ComponentReportAssistant reportAssistant;

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
      getLog().debug("Skipping; POM module");
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
      getLog().info("Skipping; no dependencies found");
      return;
    }

    ComponentReportRequest reportRequest = new ComponentReportRequest();
    reportRequest.setClientConfiguration(clientConfiguration);
    reportRequest.setComponents(dependencies);

    ComponentReportResult reportResult = reportAssistant.request(reportRequest);

    if (reportResult.hasVulnerable()) {
      throw new MojoFailureException(reportResult.explain());
    }
  }

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
}
