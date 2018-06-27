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
    ant = new AntBuilder()
  }

  void test() {
    log.info "Testing Maven $version installation: $basedir"
    assert basedir.exists()

    ant.exec(executable: "$basedir/bin/mvn", failonerror: true) {
      arg(value: '--version')
    }
  }

  void build(final File project) {
    log.info "Building: $project"
    assert project.exists()

    File dir = project.parentFile
    File logFile = new File(dir, 'build.log')

    try {
      ant.exec(executable: "$basedir/bin/mvn", dir: dir, output: logFile, failonerror: true) {
        arg(value: '--show-version')
        arg(value: '--batch-mode')
        arg(value: '--errors')

        arg(value: '--file')
        arg(file: project)

        arg(value: 'verify')
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
