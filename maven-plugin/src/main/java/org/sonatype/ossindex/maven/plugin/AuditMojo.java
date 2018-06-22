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

import java.util.List;

import javax.annotation.Nullable;

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
import org.apache.maven.shared.dependency.graph.DependencyNode;

import static org.apache.maven.plugins.annotations.ResolutionScope.RUNTIME;

/**
 * ???
 *
 * @since ???
 */
@Mojo(name = "audit", requiresDependencyResolution = RUNTIME)
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

  @Component
  private DependencyGraphBuilder dependencyGraphBuilder;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      doExecute();
    }
    catch (Exception e) {
      throw new MojoExecutionException("Error", e);
    }
  }

  private void doExecute() throws Exception {
    if (skip) {
      getLog().info("Skipping");
      return;
    }

    ProjectBuildingRequest request = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
    request.setProject(project);

    ArtifactFilter artifactFilter = null;
    if (scope != null) {
      List<String> scopes = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(scope);
      getLog().info("Scopes: " + scopes);
      artifactFilter = new CumulativeScopeArtifactFilter(scopes);
    }

    DependencyNode root = dependencyGraphBuilder.buildDependencyGraph(request, artifactFilter);
    List<DependencyNode> children = root.getChildren();

    getLog().info("Dependencies: " + children.size());
    for (DependencyNode child : children) {
      Artifact artifact = child.getArtifact();
      getLog().info("Dependency: " + artifact);
    }

    // TODO: analyze and report
  }
}
