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
package org.sonatype.ossindex.maven.extension;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.base.Splitter;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.CumulativeScopeArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ???
 *
 * @since ???
 */
@Named
@Singleton
public class AuditLifecycleParticipant
    extends AbstractMavenLifecycleParticipant
{
  private static final Logger log = LoggerFactory.getLogger(AuditLifecycleParticipant.class);

  //@Inject
  //private DependencyGraphBuilder dependencyGraphBuilder;

  private String scope;

  private boolean transitive = true;

  @Override
  public void afterProjectsRead(final MavenSession session) throws MavenExecutionException {
    try {
      Set<Artifact> dependencies = resolveDependencies(session);
      log.info("Dependencies: {}", dependencies.size());
      for (Artifact artifact : dependencies) {
        log.info("Dependency: {}", artifact);
      }
    }
    catch (Exception e) {
      throw new MavenExecutionException("Error", e);
    }
  }

  @Override
  public void afterSessionEnd(final MavenSession session) throws MavenExecutionException {
    System.out.println("afterSessionEnd: " + session);
  }

  private Set<Artifact> resolveDependencies(final MavenSession session) throws DependencyGraphBuilderException {
    Set<Artifact> result = new HashSet<>();

    ProjectBuildingRequest request = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
    request.setProject(session.getCurrentProject());

    ArtifactFilter artifactFilter = null;
    if (scope != null) {
      List<String> scopes = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(scope);
      log.info("Scopes: {}", scopes);
      artifactFilter = new CumulativeScopeArtifactFilter(scopes);
    }

    //DependencyNode root = dependencyGraphBuilder.buildDependencyGraph(request, artifactFilter);
    //collectArtifacts(result, root);

    return result;
  }

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
