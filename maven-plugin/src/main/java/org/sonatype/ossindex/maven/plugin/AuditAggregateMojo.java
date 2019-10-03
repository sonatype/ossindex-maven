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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;

import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;

/**
 * Vulnerability audit of aggregate project dependencies via
 * <a href="https://ossindex.sonatype.org/">Sonatype OSS Index</a>.
 *
 * @since ???
 */
@Mojo(name = "audit-aggregate", requiresDependencyResolution = TEST, aggregator = true)
public class AuditAggregateMojo
    extends AuditMojoSupport
{
  @Parameter(defaultValue = "${reactorProjects}", required = true, readonly = true)
  private List<MavenProject> reactorProjects;

  @Override
  protected Set<Artifact> resolveDependencies(final MavenSession session) throws DependencyGraphBuilderException {
    Set<Artifact> dependencies = new HashSet<>();

    for (MavenProject project : reactorProjects) {
      Set<Artifact> resolved = resolveDependencies(session, project);
      dependencies.addAll(resolved);
    }

    return dependencies;
  }
}
