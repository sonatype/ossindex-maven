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
# Fail or Warn

By default [ossindex:audit](audit-mojo.html) will cause `BUILD FAILURE` if any vulnerabilities are detected:

Example build output:

```
[INFO] --- ossindex-maven-plugin:1-SNAPSHOT:audit (default-cli) @ vulnerable-fail ---
[INFO] Checking for vulnerabilities:
[INFO]   commons-fileupload:commons-fileupload:jar:1.3:compile
[INFO]   commons-io:commons-io:jar:2.2:compile
[INFO] Exclude coordinates: []
[INFO] Exclude vulnerability identifiers: []
[INFO] CVSS-score threshold: 0.0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 1.491 s
[INFO] Finished at: 2018-07-02T12:54:10-07:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.sonatype.ossindex.maven:ossindex-maven-plugin:1-SNAPSHOT:audit (default-cli) on project vulnerable-fail: Detected 1 vulnerable components:
[ERROR]   commons-fileupload:commons-fileupload:jar:1.3:compile; https://ossindex.sonatype.org/component/maven:commons-fileupload/commons-fileupload@1.3
[ERROR]     * [CVE-2014-0050]  Permissions, Privileges, and Access Controls (7.5); https://ossindex.sonatype.org/vuln/43e6c5a5-b586-4b31-9244-b62b6e36f2d0
[ERROR]     * Arbitrary file upload via deserialization (0.0); https://ossindex.sonatype.org/vuln/fb810cbf-d8fb-4f30-b79b-82652ae7192a
[ERROR]     * [CVE-2016-3092]  Improper Input Validation (7.5); https://ossindex.sonatype.org/vuln/39d74cc8-457a-4e57-89ef-a258420138c5
[ERROR]     * [CVE-2016-1000031]  Improper Access Control (9.8); https://ossindex.sonatype.org/vuln/3d5968a4-4e14-4a98-8816-a4e847bc1426
[ERROR] -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException
```

## Warn

To prevent build failure configure the [fail](audit-mojo.html#fail) parameter:

    <plugin>
      <groupId>org.sonatype.ossindex.maven</groupId>
      <artifactId>ossindex-maven-plugin</artifactId>
      <configuration>
        <fail>false</fail>
      </configuration>
    </plugin>

or via CLI:

    mvn ossindex:audit -Dossindex.fail=false

Example build output:

```
[INFO] --- ossindex-maven-plugin:1-SNAPSHOT:audit (default-cli) @ vulnerable-warn ---
[INFO] Checking for vulnerabilities:
[INFO]   commons-fileupload:commons-fileupload:jar:1.3:compile
[INFO]   commons-io:commons-io:jar:2.2:compile
[INFO] Exclude coordinates: []
[INFO] Exclude vulnerability identifiers: []
[INFO] CVSS-score threshold: 0.0
[WARNING] Detected 1 vulnerable components:
  commons-fileupload:commons-fileupload:jar:1.3:compile; https://ossindex.sonatype.org/component/maven:commons-fileupload/commons-fileupload@1.3
    * [CVE-2014-0050]  Permissions, Privileges, and Access Controls (7.5); https://ossindex.sonatype.org/vuln/43e6c5a5-b586-4b31-9244-b62b6e36f2d0
    * Arbitrary file upload via deserialization (0.0); https://ossindex.sonatype.org/vuln/fb810cbf-d8fb-4f30-b79b-82652ae7192a
    * [CVE-2016-3092]  Improper Input Validation (7.5); https://ossindex.sonatype.org/vuln/39d74cc8-457a-4e57-89ef-a258420138c5
    * [CVE-2016-1000031]  Improper Access Control (9.8); https://ossindex.sonatype.org/vuln/3d5968a4-4e14-4a98-8816-a4e847bc1426

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 1.347 s
[INFO] Finished at: 2018-07-02T12:54:07-07:00
[INFO] ------------------------------------------------------------------------
```