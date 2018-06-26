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

import org.junit.Before
import org.junit.Test

/**
 * ???
 */
abstract class MavenPluginTestSupport
    extends TestSupport
{
  String mavenVersion

  AntBuilder ant

  @Before
  void setUp() {
    ant = new AntBuilder()
  }

  @Test
  void 'build integration-tests'() {
    File install = util.resolveFile("target/maven-installations/maven-$mavenVersion")
    assert install.exists()

    log "Testing Maven $mavenVersion installation"

    ant.exec(executable: "$install/bin/mvn", failonerror: true) {
      arg(value: '--version')
    }

    File basedir = util.resolveFile('..')
    File project = new File(basedir, 'maven-plugin/pom.xml')
    assert project.exists()

    // TODO: copy tree to target/<something>?

    log "Building $project with integration-tests"

    ant.exec(executable: "$install/bin/mvn", failonerror: true) {
      arg(value: '--show-version')
      arg(value: '--batch-mode')
      arg(value: '--errors')

      arg(value: '--file')
      arg(file: project)

      arg(value: '--activate-profiles')
      arg(value: 'it')

      arg(value: 'verify')

      // disable maven-enforcer-plugin execution which will fail on different versions, etc
      arg(value: '-Denforcer.skip=true')
    }
  }
}
