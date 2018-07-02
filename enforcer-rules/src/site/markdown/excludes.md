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
# Excludes

Sometimes `BanVulnerableDependencies` may detect vulnerabilities which are not relevant.
In those cases *exclusions* can be configured to prevent from vulnerabilities from matching.

## Exclude Specific Vulnerabilities

Specific vulnerabilities can be excluded by *vulnerability-id*.

For example to exclude [39d74cc8-457a-4e57-89ef-a258420138c5](https://ossindex.sonatype.org/vuln/39d74cc8-457a-4e57-89ef-a258420138c5):

    <configuration>
      <rules>
        <banVulnerable implementation="org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies">
          <excludeVulnerabilityIds>
            <exclude>39d74cc8-457a-4e57-89ef-a258420138c5</exclude>
          </<excludeVulnerabilityIds>
        </banVulnerable>
      </rules>
    </configuration>

## Exclude Specific Components

Specific components can be excluded by coordinates.

For example to exclude [commons-fileupload:commons-fileupload:1.3](https://ossindex.sonatype.org/component/maven:commons-fileupload/commons-fileupload@1.3):

    <configuration>
      <rules>
        <banVulnerable implementation="org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies">
          <excludeCoordinates>
            <exclude>
              <groupId>commons-fileupload</groupId>
              <artifactId>commons-fileupload</artifactId>
              <version>1.3</version>
            </exclude>
          </excludeCoordinates>
        </banVulnerable>
      </rules>
    </configuration>

**NOTE:** Only exact coordinate matches are excluded.