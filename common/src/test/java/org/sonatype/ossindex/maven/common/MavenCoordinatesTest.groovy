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
    MavenCoordinates.parse('foo:bar:1').with {
      assert groupId == 'foo'
      assert artifactId == 'bar'
      assert version == '1'
    }
  }

  @Test
  void 'parse GAV with type'() {
    MavenCoordinates.parse('foo:bar:zip:1').with {
      assert groupId == 'foo'
      assert artifactId == 'bar'
      assert version == '1'
      assert type == 'zip'
    }
  }

  @Test
  void 'parse GAV with type and classifier'() {
    MavenCoordinates.parse('foo:bar:zip:baz:1').with {
      assert groupId == 'foo'
      assert artifactId == 'bar'
      assert version == '1'
      assert type == 'zip'
      assert classifier == 'baz'
    }
  }

  @Test
  void 'parse list'() {
    def list = MavenCoordinates.parseList('foo:bar:1, foo:bar:2   ,,foo:bar:3')
    assert list.size() == 3
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
    list.get(2).with {
      assert groupId == 'foo'
      assert artifactId == 'bar'
      assert version == '3'
    }
  }
}
