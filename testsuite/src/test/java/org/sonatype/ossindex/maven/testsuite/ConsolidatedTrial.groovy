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
package org.sonatype.ossindex.maven.testsuite

import org.sonatype.goodies.testsupport.TestSupport

import org.junit.Before
import org.junit.Test

/**
 * ???
 */
class ConsolidatedTrial
    extends TestSupport
{
  String mavenVersion = '3.5.4'

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

    File project = util.resolveFile('src/it/projects').canonicalFile
    File workspace = util.resolveFile("target/it-workspace/${getClass().simpleName}")
    log "Preparing workspace: $workspace"

    def filters = [
        'project.version'              : '1-SNAPSHOT',
        'ossindex.baseUrl'             : 'https://ossindex.sonatype.org',
        'apache-maven-invoker.version' : '3.1.0',
        'apache-maven-enforcer.version': '3.0.0-M1'
    ]

    ant.mkdir(dir: workspace)
    ant.copy(todir: workspace, overwrite: true, filtering: true) {
      fileset(dir: project) {
        include(name: '**')
      }
      filterset {
        filters.each {
          filter(token: it.key, value: it.value)
        }
      }
    }

    maven.build(new File(workspace, 'pom.xml'))
  }
}
