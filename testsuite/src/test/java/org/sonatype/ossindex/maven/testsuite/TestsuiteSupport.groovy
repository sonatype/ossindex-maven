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
import org.sonatype.goodies.testsupport.port.PortRegistry

import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.littleshoot.proxy.impl.DefaultHttpProxyServer

/**
 * Support for testsuite.
 */
abstract class TestsuiteSupport
    extends TestSupport
{
  /**
   * Port registry to select unused ports for some tests.
   */
  private static final PortRegistry portRegistry = new PortRegistry()

  @ClassRule
  public static final ProxyRule proxy = new ProxyRule(
      DefaultHttpProxyServer.bootstrap()
          .withPort(portRegistry.reservePort())
  )

  private final String mavenVersion

  private AntBuilder ant

  private MavenInstallation maven

  String[] arguments

  Map<String,String> overrides

  TestsuiteSupport(final String mavenVersion) {
    this.mavenVersion = mavenVersion
  }

  @Before
  void setUp() {
    ant = new AntBuilder()
    File install = util.resolveFile("target/maven-installations/maven-$mavenVersion").canonicalFile
    maven = new MavenInstallation(mavenVersion, install)
  }

  /**
   * Load environment properties.
   */
  private Properties loadEnvironment() {
    File file = util.resolveFile('target/environment.properties')

    Properties env = new Properties()
    file.withReader { input ->
      env.load(input)
    }

    return env
  }

  @Test
  void 'build integration-tests'() {
    maven.test()

    File project = util.resolveFile('src/it/projects').canonicalFile
    File workspace = util.resolveFile("target/it-workspace/${getClass().simpleName}")
    log "Preparing workspace: $workspace"

    def env = loadEnvironment()

    // argument properties
    env.putAll([
        'apache-maven-invoker.version' : '3.1.0',
        'apache-maven-enforcer.version': '3.0.0',
        'http-proxy.port': proxy.port,
        'workspace.basedir': workspace.absolutePath
    ])
    if (overrides != null) {
      env.putAll(overrides)
    }

    log 'Environment:'
    env.sort().each { key, value ->
      log "  $key=$value"
    }

    ant.mkdir(dir: workspace)
    ant.copy(todir: workspace, overwrite: true, filtering: true) {
      fileset(dir: project) {
        include(name: '**')
      }
      filterset {
        env.each {
          filter(token: it.key, value: it.value)
        }
      }
    }

    maven.build(new File(workspace, 'pom.xml'), arguments)
  }
}
