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
    '3.0.5', // 3.0.x presently doesn't work, here only to confirm that
    '3.1.1',
    '3.3.9',
    '3.5.4'
]

// Prepare Maven installations for each version we test with

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

// Generate environment.properties to bridge some configuration from Maven to tests

def env = new Properties()
env.put('project.version', project.version)
env.put('ossindex.baseUrl', properties['ossindex.baseUrl'])

def envFile = new File(basedir, 'target/environment.properties')
envFile.withWriter { output ->
  env.store(output, '')
}

/*
    def filters = [
        'project.version'              : '1-SNAPSHOT',
        'ossindex.baseUrl'             : 'https://ossindex.sonatype.org',
        'apache-maven-invoker.version' : '3.1.0',
        'apache-maven-enforcer.version': '3.0.0-M1'
    ]
 */