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
package org.sonatype.ossindex.maven.common

import org.sonatype.goodies.packageurl.PackageUrl
import org.sonatype.goodies.testsupport.TestSupport
import org.sonatype.ossindex.service.api.componentreport.ComponentReport
import org.sonatype.ossindex.service.api.componentreport.ComponentReportVulnerability

import org.junit.Test

/**
 * Tests for {@link ComponentReportResult}.
 */
class ComponentReportResultTest
    extends TestSupport
{
  @Test
  void 'has vulnerable'() {
    def underTest = new ComponentReportResult()
    assert !underTest.hasVulnerable()

    def artifact = TestArtifactFactory.create('foo', 'bar', '1')
    def report = new ComponentReport()
    underTest.vulnerable.put(artifact, report)
    assert underTest.hasVulnerable()
  }

  @Test
  void 'has exclusions'() {
    def underTest = new ComponentReportResult()
    assert !underTest.hasExclusions()

    underTest.excludedVulnerabilityIds.add('1')
    assert underTest.hasExclusions()

    underTest.excludedVulnerabilityIds.clear()
    assert !underTest.hasExclusions()

    underTest.excludedCoordinates.add(MavenCoordinates.parse('foo:bar:1'))
    assert underTest.hasExclusions()
  }

  @Test
  void 'explain no vulnerable'() {
    def underTest = new ComponentReportResult()

    def text = underTest.explain()
    log text

    assert text.contains('No vulnerable components detected')
  }

  @Test
  void 'explain vulnerable'() {
    def underTest = new ComponentReportResult()
    def artifact1 = TestArtifactFactory.create('foo', 'bar', '1')
    def purl1 = PackageUrl.parse('maven:foo/bar@1')
    def report1 = new ComponentReport(
        coordinates: purl1,
        reference: URI.create("http://ossindex.example.com/component/$purl1"),
        vulnerabilities: [
            new ComponentReportVulnerability(
                id: '1',
                title: 'vuln-1',
                cvssScore: 1.1,
                reference: URI.create("http://ossindex.example.com/vuln/1"),
            ),
            new ComponentReportVulnerability(
                id: '2',
                title: 'vuln-2',
                cvssScore: 3,
                reference: URI.create("http://ossindex.example.com/vuln/2"),
            )
        ]
    )
    underTest.vulnerable.put(artifact1, report1)

    def text = underTest.explain()
    log text

    assert text.contains('Detected 1 vulnerable components')
    assert text.contains('vuln-1 (1.1);')
    assert text.contains('vuln-2 (3.0);')
  }

  @Test
  void 'explain vulnerable with excluded id'() {
    def underTest = new ComponentReportResult()

    def artifact1 = TestArtifactFactory.create('foo', 'bar', '1')
    def purl1 = PackageUrl.parse('maven:foo/bar@1')
    def report1 = new ComponentReport(
        coordinates: purl1,
        reference: URI.create("http://ossindex.example.com/component/$purl1"),
        vulnerabilities: [
            new ComponentReportVulnerability(
                id: '1',
                title: 'vuln-1',
                cvssScore: 1.1,
                reference: URI.create("http://ossindex.example.com/vuln/1"),
            ),
            new ComponentReportVulnerability(
                id: '2',
                title: 'vuln-2',
                cvssScore: 3,
                reference: URI.create("http://ossindex.example.com/vuln/2"),
            )
        ]
    )
    underTest.vulnerable.put(artifact1, report1)
    underTest.excludedVulnerabilityIds.add('1')

    def text = underTest.explain()
    log text

    assert text.contains('Detected 1 vulnerable components')
    assert !text.contains('vuln-1 (1.1);')
    assert text.contains('vuln-2 (3.0);')
    assert text.contains('Excluded vulnerabilities')
  }

  @Test
  void 'explain vulnerable with excluded coordinates'() {
    def underTest = new ComponentReportResult()

    def artifact1 = TestArtifactFactory.create('foo', 'bar', '1')
    def purl1 = PackageUrl.parse('maven:foo/bar@1')
    def report1 = new ComponentReport(
        coordinates: purl1,
        reference: URI.create("http://ossindex.example.com/component/$purl1"),
        vulnerabilities: [
            new ComponentReportVulnerability(
                id: '1',
                title: 'vuln-1',
                cvssScore: 1.1,
                reference: URI.create("http://ossindex.example.com/vuln/1"),
            ),
            new ComponentReportVulnerability(
                id: '2',
                title: 'vuln-2',
                cvssScore: 3,
                reference: URI.create("http://ossindex.example.com/vuln/2"),
            )
        ]
    )
    underTest.vulnerable.put(artifact1, report1)

    underTest.excludedCoordinates.add(MavenCoordinates.parse('a:b:1'))

    def text = underTest.explain()
    log text

    assert text.contains('Detected 1 vulnerable components')
    assert text.contains('vuln-1 (1.1);')
    assert text.contains('vuln-2 (3.0);')
    assert text.contains('Excluded coordinates')
  }
}
