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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;

/**
 * ???
 *
 * @since ???
 */
//@Named
//@Singleton
public class MavenLifecycleParticipantImpl
    extends AbstractMavenLifecycleParticipant
{
  @Inject
  private DependencyGraphBuilder dependencyGraphBuilder;

  @Override
  public void afterSessionStart(final MavenSession session) throws MavenExecutionException {
    System.out.println("afterSessionStart: " + session);
  }

  @Override
  public void afterProjectsRead(final MavenSession session) throws MavenExecutionException {
    System.out.println("afterProjectsRead: " + session);
    System.out.println("dependencyGraphBuilder: " + dependencyGraphBuilder);
  }

  @Override
  public void afterSessionEnd(final MavenSession session) throws MavenExecutionException {
    System.out.println("afterSessionEnd: " + session);
  }
}
