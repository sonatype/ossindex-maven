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

By default `BanVulnerableDependencies` will cause failures will cause `BUILD FAILURE` if any vulnerabilities are detected:

Example build output:

```
[INFO] --- maven-enforcer-plugin:3.0.0-M1:enforce (checks) @ vulnerable-fail ---
[INFO] Checking for vulnerabilities:
[INFO]   commons-fileupload:commons-fileupload:jar:1.3:compile
[INFO]   commons-io:commons-io:jar:2.2:compile
[INFO] Exclude coordinates: []
[INFO] Exclude vulnerability identifiers: []
[INFO] CVSS-score threshold: 0.0
[WARNING] Rule 0: org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies failed with message:
Detected 1 vulnerable components:
  commons-fileupload:commons-fileupload:jar:1.3:compile; https://ossindex.sonatype.org/component/maven:commons-fileupload/commons-fileupload@1.3
    * [CVE-2014-0050]  Permissions, Privileges, and Access Controls (7.5); https://ossindex.sonatype.org/vuln/43e6c5a5-b586-4b31-9244-b62b6e36f2d0
    * Arbitrary file upload via deserialization (0.0); https://ossindex.sonatype.org/vuln/fb810cbf-d8fb-4f30-b79b-82652ae7192a
    * [CVE-2016-3092]  Improper Input Validation (7.5); https://ossindex.sonatype.org/vuln/39d74cc8-457a-4e57-89ef-a258420138c5
    * [CVE-2016-1000031]  Improper Access Control (9.8); https://ossindex.sonatype.org/vuln/3d5968a4-4e14-4a98-8816-a4e847bc1426

[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 1.292 s
[INFO] Finished at: 2018-07-02T12:53:21-07:00
[INFO] ------------------------------------------------------------------------
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-enforcer-plugin:3.0.0-M1:enforce (checks) on project vulnerable-fail: Some Enforcer rules have failed. Look above for specific messages explaining why the rule failed. -> [Help 1]
[ERROR] 
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR] 
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoExecutionException
```

## Warn

To prevent build failure configure the `level` parameter to `WARN`:

    <configuration>
      <rules>
        <banVulnerable implementation="org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies">
          <level>WARN</level>
        </banVulnerable>
      </rules>
    </configuration>

Example build output:

```
[INFO] --- maven-enforcer-plugin:3.0.0-M1:enforce (checks) @ vulnerable-warn ---
[INFO] Checking for vulnerabilities:
[INFO]   commons-fileupload:commons-fileupload:jar:1.3:compile
[INFO]   commons-io:commons-io:jar:2.2:compile
[INFO] Exclude coordinates: []
[INFO] Exclude vulnerability identifiers: []
[INFO] CVSS-score threshold: 0.0
[WARNING] Rule 0: org.sonatype.ossindex.maven.enforcer.BanVulnerableDependencies warned with message:
Detected 1 vulnerable components:
  commons-fileupload:commons-fileupload:jar:1.3:compile; https://ossindex.sonatype.org/component/maven:commons-fileupload/commons-fileupload@1.3
    * [CVE-2014-0050]  Permissions, Privileges, and Access Controls (7.5); https://ossindex.sonatype.org/vuln/43e6c5a5-b586-4b31-9244-b62b6e36f2d0
    * Arbitrary file upload via deserialization (0.0); https://ossindex.sonatype.org/vuln/fb810cbf-d8fb-4f30-b79b-82652ae7192a
    * [CVE-2016-3092]  Improper Input Validation (7.5); https://ossindex.sonatype.org/vuln/39d74cc8-457a-4e57-89ef-a258420138c5
    * [CVE-2016-1000031]  Improper Access Control (9.8); https://ossindex.sonatype.org/vuln/3d5968a4-4e14-4a98-8816-a4e847bc1426

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 1.209 s
[INFO] Finished at: 2018-07-02T12:53:19-07:00
[INFO] ------------------------------------------------------------------------
```