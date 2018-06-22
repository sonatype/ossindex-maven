<!--

    Copyright (c) 2018-present Sonatype, Inc. All rights reserved.

    This program is licensed to you under the Apache License Version 2.0,
    and you may not use this file except in compliance with the Apache License Version 2.0.
    You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.

    Unless required by applicable law or agreed to in writing,
    software distributed under the Apache License Version 2.0 is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.

-->
# Sonatype OSS Index - Maven Enforcer Rules

Adds [maven-enforcer-plugin][2] rules to integrate security information from [Sonatype OSS Index][1] into Maven builds.

## Features

* Ban vulnerable dependencies

## Usage

Requires an internet connection and access to [Sonatype OSS Index][1] service.
When Maven is in offline mode, rules that depend on a network connection are skipped.

Configure an execution of the `maven-enforcer-plugin` and configure `ossindex-maven-enforcer-rules` dependency and rule:

    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-enforcer-plugin</artifactId>
      <version>3.0.0-M1</version>
      <dependencies>
        <dependency>
          <groupId>org.sonatype.ossindex.maven</groupId>
          <artifactId>ossindex-maven-enforcer-rules</artifactId>
          <version>1-SNAPSHOT</version>
        </dependency>
      </dependencies>
      <executions>
        <execution>
          <phase>validate</phase>
          <goals>
            <goal>enforce</goal>
          </goals>
          <configuration>
            <rules>
              <banVunerableDependencies implementation="org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies"/>
            </rules>
          </configuration>
        </execution>
      </executions>
    </plugin>

[1]: https://ossindex.sonatype.org
[2]: https://maven.apache.org/enforcer/maven-enforcer-plugin