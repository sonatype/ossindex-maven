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
package org.sonatype.ossindex.maven.plugin.export

import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.DefaultArtifact
import org.apache.maven.artifact.handler.ArtifactHandler

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Test {@link Artifact} factory.
 */
class TestArtifactFactory
{
  private static final ArtifactHandler artifactHandler

  static {
    // setup artifact-handler to return null for classifier
    artifactHandler = mock(ArtifactHandler.class)
    when(artifactHandler.getClassifier()).thenReturn(null)
  }

  static Artifact create(final String groupId, final String artifactId, final String version) {
    // default impl when classifier is null, consults artifact-handler
    return new DefaultArtifact(groupId, artifactId, version, null, 'jar', null, artifactHandler)
  }
}
