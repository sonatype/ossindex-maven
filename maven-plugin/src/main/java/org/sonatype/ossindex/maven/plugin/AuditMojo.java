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

import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;

import static org.apache.maven.plugins.annotations.ResolutionScope.TEST;

/**
 * Vulnerability audit of project dependencies via
 * <a href="https://ossindex.sonatype.org/">Sonatype OSS Index</a>.
 *
 * @since 3.0.0
 */
@Mojo(name = "audit", requiresDependencyResolution = TEST)
public class AuditMojo
    extends AuditMojoSupport
{
  @Override
  protected boolean isSkipped() {
    if ("pom".equals(project.getPackaging())) {
      getLog().info("Skipping; POM module");
      return true;
    }
    return super.isSkipped();
  }

  @Override
  protected Set<Artifact> resolveDependencies(final MavenSession session) throws DependencyGraphBuilderException {
    return resolveDependencies(session, project);
  }
}
