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
 * Support for {@code maven-plugin} tests.
 */
abstract class MavenPluginTestSupport
    extends TestSupport
{
  String mavenVersion

  AntBuilder ant

  MavenInstallation maven

  @Before
  void setUp() {
    ant = new AntBuilder()
    File install = util.resolveFile("target/maven-installations/maven-$mavenVersion").canonicalFile
    maven = new MavenInstallation(mavenVersion, install)
  }

  @Test
  void 'build integration-tests'() {
    maven.test()

    File project = util.resolveFile('../maven-plugin').canonicalFile
    File workspace = util.resolveFile("target/it-workspace/${getClass().simpleName}")
    log "Preparing workspace: $workspace"

    ant.mkdir(dir: workspace)
    ant.copy(todir: workspace) {
      fileset(dir: project) {
        include(name: 'pom.xml')
        include(name: 'src/**')
      }
    }

    maven.build(new File(workspace, 'pom.xml'))
  }
}
