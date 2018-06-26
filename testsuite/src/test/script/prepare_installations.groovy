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

def installs = new File(basedir, 'target/maven-installations')
def dists = new File(basedir, 'target/maven-distributions')
def versions = [
    '3.0.5',
    '3.1.1',
    '3.3.9',
    '3.5.4'
]

versions.each { version ->
  log.info "Preparing: $version"
  def dist = new File(dists, "apache-maven-${version}-bin.zip")
  def install = new File(installs, "maven-$version")
  ant.mkdir(dir: install)
  ant.unzip(src: dist, dest: install) {
    cutdirsmapper(dirs: 1)
  }
  ant.chmod(perm: 'u+rx') {
    fileset(dir: install) {
      include(name: 'bin/mvn')
    }
  }
}
