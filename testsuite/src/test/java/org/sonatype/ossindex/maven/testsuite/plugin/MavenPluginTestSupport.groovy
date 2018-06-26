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
package org.sonatype.ossindex.maven.testsuite.plugin

import org.sonatype.goodies.testsupport.TestSupport
import org.sonatype.ossindex.maven.testsuite.MavenInstallation

import org.junit.Before
import org.junit.Test

/**
 * ???
 */
abstract class MavenPluginTestSupport
    extends TestSupport
{
  String mavenVersion

  MavenInstallation maven

  @Before
  void setUp() {
    File install = util.resolveFile("target/maven-installations/maven-$mavenVersion")
    maven = new MavenInstallation(mavenVersion, install)
  }

  @Test
  void 'build integration-tests'() {
    maven.test()

    File project = util.resolveFile('../maven-plugin/pom.xml')

    maven.build(project)
  }
}
