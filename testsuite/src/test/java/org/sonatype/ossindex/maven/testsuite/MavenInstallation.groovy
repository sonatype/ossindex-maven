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

import javax.annotation.Nullable

import groovy.util.logging.Slf4j
import org.apache.tools.ant.BuildException

/**
 * Maven installation helper.
 */
@Slf4j
class MavenInstallation
{
  private final String version

  private final File basedir

  private final AntBuilder ant

  MavenInstallation(final String version, final File basedir) {
    this.version = version
    this.basedir = basedir
    this.ant = new AntBuilder()
  }

  void test() {
    log.info "Testing Maven $version installation: $basedir"
    assert basedir.exists()

    ant.exec(executable: "$basedir/bin/mvn", failonerror: true) {
      env(key: 'M2_HOME', file: basedir)
      env(key: 'JAVA_TOOL_OPTIONS', value: '')
      arg(value: '--version')
    }
  }

  @Nullable
  private static File detectLocalRepo() {
    String path = System.getProperty('maven.repo.local')
    if (path != null) {
      return new File(path).canonicalFile
    }
    return null
  }

  void build(final File project, final String... arguments) {
    log.info "Building: $project"
    assert project.exists()

    File dir = project.parentFile
    File logFile = new File(dir, 'build.log')

    File localRepo = detectLocalRepo()
    log.info("Local repository: $localRepo")

    try {
      ant.exec(executable: "$basedir/bin/mvn", dir: dir, output: logFile, failonerror: true) {
        env(key: 'M2_HOME', file: basedir)
        env(key: 'JAVA_TOOL_OPTIONS', value: '')

        arg(value: '--show-version')
        arg(value: '--batch-mode')
        arg(value: '--errors')

        if (localRepo != null) {
          arg(value: "-Dmaven.repo.local=${localRepo.absolutePath}")
        }

        arg(value: '--file')
        arg(file: project)

        arg(value: 'verify')

        for (custom in arguments) {
          arg(value: custom)
        }
      }
    }
    catch (BuildException e) {
      log.error("Build failed", e)

      def lines = logFile.readLines()

      def max = 100
      if (lines.size() < max) {
        max = lines.size()
      }

      def snip = lines[-max..-1]
      log.error("\n----8<----\n${snip.join('\n')}\n----8<----")

      throw e
    }
  }
}
