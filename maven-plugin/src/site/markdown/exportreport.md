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
# Export Report

The [ossindex:audit](audit-mojo.html) can optionally record a report file which contains the details about the
component reports that are used to determine if a dependency is vulnerable or not.

To enable this feature configure the [reportFile](audit-mojo.html#reportFile) parameter to the location where
the report will be written.

    <plugin>
      <groupId>org.sonatype.ossindex.maven</groupId>
      <artifactId>ossindex-maven-plugin</artifactId>
      <configuration>
        <reportFile>${project.build.directory}/audit-report.json</reportFile>
      </configuration>
    </plugin>

or via CLI:

    mvn ossindex:audit -Dossindex.reportFile=target/audit-report.json

## Formats

The file extension is used to inform which format the report will be written as.

| Format | File Extension |
| ------ | -------------- |
| JSON   | `.json`        |
| XML    | `.xml`         |
| Text   | `.txt`         |
