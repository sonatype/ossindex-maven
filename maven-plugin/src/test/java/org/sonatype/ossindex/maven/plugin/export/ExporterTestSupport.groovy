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
package org.sonatype.ossindex.maven.plugin.export

import org.sonatype.goodies.packageurl.PackageUrl
import org.sonatype.goodies.testsupport.TestSupport
import org.sonatype.ossindex.maven.common.ComponentReportResult
import org.sonatype.ossindex.maven.common.MavenCoordinates
import org.sonatype.ossindex.service.api.componentreport.ComponentReport
import org.sonatype.ossindex.service.api.componentreport.ComponentReportVulnerability

import org.junit.Before
import org.junit.Test

/**
 * ???
 */
abstract class ExporterTestSupport
    extends TestSupport
{
  abstract protected Exporter getUnderTest()

  @Test
  void 'export'() {
    def result = new ComponentReportResult()

    def artifact1 = TestArtifactFactory.create('foo', 'bar', '1')
    def purl1 = PackageUrl.parse('maven:foo/bar@1')

    def vuln1 = new ComponentReportVulnerability(
        id: '1',
        title: 'vuln-1',
        description: 'vulnerability 1',
        cvssScore: 1.1,
        cvssVector: 'vector',
        cwe: 'bar',
        reference: URI.create("http://ossindex.example.com/vuln/1"),
    )
    def vuln2 = new ComponentReportVulnerability(
        id: '2',
        title: 'vuln-2',
        description: 'vulnerability 2',
        cvssScore: 3,
        cvssVector: 'vector',
        cwe: 'foo',
        reference: URI.create("http://ossindex.example.com/vuln/2"),
    )

    def report1 = new ComponentReport(
        coordinates: purl1,
        description: 'example component',
        reference: URI.create("http://ossindex.example.com/component/$purl1"),
        vulnerabilities: [ vuln1, vuln2 ]
    )

    result.reports.put(artifact1, report1)
    result.vulnerable.put(artifact1, report1)
    result.excludedCoordinates.add(MavenCoordinates.parse('a:b:1'))
    result.excludedCoordinates.add(MavenCoordinates.parse('c:d:2'))
    result.excludedVulnerabilities.add(vuln1)

    def file = util.createTempFile('report-')
    underTest.export(result, file)
    log file.text
  }
}
