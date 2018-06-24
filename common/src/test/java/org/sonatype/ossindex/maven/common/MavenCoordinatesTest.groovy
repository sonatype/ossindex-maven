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
package org.sonatype.ossindex.maven.common

import org.sonatype.goodies.testsupport.TestSupport

import org.junit.Test

/**
 * Tests for {@link MavenCoordinates}.
 */
class MavenCoordinatesTest
    extends TestSupport
{
  @Test
  void 'parse GAV'() {
    def coordinates = MavenCoordinates.parse('foo:bar:1')
    assert coordinates.groupId == 'foo'
    assert coordinates.artifactId == 'bar'
    assert coordinates.version == '1'
  }

  @Test
  void 'parse GAV with type'() {
    def coordinates = MavenCoordinates.parse('foo:bar:zip:1')
    assert coordinates.groupId == 'foo'
    assert coordinates.artifactId == 'bar'
    assert coordinates.version == '1'
    assert coordinates.type == 'zip'
  }

  @Test
  void 'parse GAV with type and classifier'() {
    def coordinates = MavenCoordinates.parse('foo:bar:zip:baz:1')
    assert coordinates.groupId == 'foo'
    assert coordinates.artifactId == 'bar'
    assert coordinates.version == '1'
    assert coordinates.type == 'zip'
    assert coordinates.classifier == 'baz'
  }

  @Test
  void 'parse list'() {
    def list = MavenCoordinates.parseList('foo:bar:1,foo:bar:2')
    assert list.size() == 2
    list.get(0).with {
      assert groupId == 'foo'
      assert artifactId == 'bar'
      assert version == '1'
    }
    list.get(1).with {
      assert groupId == 'foo'
      assert artifactId == 'bar'
      assert version == '2'
    }
  }
}
