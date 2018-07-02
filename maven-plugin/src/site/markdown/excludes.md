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

Sometimes audit may detect vulnerabilities which are not relevant.
In those cases audit can be configured to *exclude* from vulnerabilities from matching.

## Exclude Specific Vulnerabilities

Specific vulnerabilities can be excluded by *vulnerability-id*.

For example to exclude [39d74cc8-457a-4e57-89ef-a258420138c5](https://ossindex.sonatype.org/vuln/39d74cc8-457a-4e57-89ef-a258420138c5):

    <plugin>
      <groupId>org.sonatype.ossindex.maven</groupId>
      <artifactId>ossindex-maven-plugin</artifactId>
      <configuration>
        <excludeVulnerabilityIds>
          <exclude>39d74cc8-457a-4e57-89ef-a258420138c5</exclude>
        </<excludeVulnerabilityIds
      </configuration>
    </plugin>

This exclusion can also be configured via CLI property as a comma-separated list of ids:

    mvn ossindex:audit -Dossindex.excludeVulnerabilityIds=39d74cc8-457a-4e57-89ef-a258420138c5

## Exclude Specific Components

Specific components can be excluded by coordinates.

For example to exclude [commons-fileupload:commons-fileupload:1.3](https://ossindex.sonatype.org/component/maven:commons-fileupload/commons-fileupload@1.3):

    <plugin>
      <groupId>org.sonatype.ossindex.maven</groupId>
      <artifactId>ossindex-maven-plugin</artifactId>
      <configuration>
        <excludeCoordinates>
          <exclude>
            <groupId>commons-fileupload</groupId>
            <artifactId>commons-fileupload</artifactId>
            <version>1.3</version>
          </exclude>
        </excludeCoordinates>
      </configuration>
    </plugin>

This exclusion can also be configured via CLI property as a comma-separated list of coordinates:

    mvn ossindex:audit -Dossindex.excludeCoordinates=commons-fileupload:commons-fileupload:1.3

**NOTE:** Only exact coordinate matches are excluded.