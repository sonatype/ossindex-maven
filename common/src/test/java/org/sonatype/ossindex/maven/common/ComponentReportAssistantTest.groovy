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

import org.apache.maven.artifact.DefaultArtifact
import org.apache.maven.artifact.handler.ArtifactHandler
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Tests for {@link ComponentReportAssistant}.
 */
class ComponentReportAssistantTest
    extends TestSupport
{
  private ComponentReportAssistant underTest

  @Before
  void setUp() {
    underTest = new ComponentReportAssistant()
  }

  @Test
  void 'artifact to package-url'() {
    def handler = mock(ArtifactHandler.class)
    when(handler.getClassifier()).thenReturn(null)

    def artifact = new DefaultArtifact(
        'a.b.c', 'test', '1', null, 'jar', null,
        handler
    )

    def purl = ComponentReportAssistant.packageUrl(artifact)
    assert purl.namespaceAsString == 'a.b.c'
    assert purl.name == 'test'
    assert purl.version == '1'
  }

  @Test
  void 'inclusion matching'() {
    def request = new ComponentReportRequest()
    def report = new ComponentReport(
        coordinates: PackageUrl.parse('maven:foo/bar@1'),
        vulnerabilities: [
            new ComponentReportVulnerability(
                id: '1',
                cvssScore: 1
            ),
            new ComponentReportVulnerability(
                id: '2',
                cvssScore: 2
            )
        ]
    )

    assert underTest.match(request, report)
  }

  @Test
  void 'inclusion matching; no vulnerabilities'() {
    def request = new ComponentReportRequest()
    def report = new ComponentReport(
        coordinates: PackageUrl.parse('maven:foo/bar@1'),
        vulnerabilities: []
    )

    assert !underTest.match(request, report)
  }

  @Test
  void 'inclusion matching; exclude by id'() {
    def request = new ComponentReportRequest()
    def report = new ComponentReport(
        coordinates: PackageUrl.parse('maven:foo/bar@1'),
        vulnerabilities: [
            new ComponentReportVulnerability(
                id: '1',
                cvssScore: 1
            ),
            new ComponentReportVulnerability(
                id: '2',
                cvssScore: 2
            )
        ]
    )

    request.getExcludeVulnerabilityIds().add('1')
    assert underTest.match(request, report)

    request.getExcludeVulnerabilityIds().add('2')
    assert !underTest.match(request, report)
  }

  @Test
  void 'inclusion matching; exclude by cvss score'() {
    def request = new ComponentReportRequest()
    def report = new ComponentReport(
        coordinates: PackageUrl.parse('maven:foo/bar@1'),
        vulnerabilities: [
            new ComponentReportVulnerability(
                id: '1',
                cvssScore: 1
            ),
            new ComponentReportVulnerability(
                id: '2',
                cvssScore: 2
            )
        ]
    )

    request.setCvssScoreThreshold(2)
    assert underTest.match(request, report)

    request.setCvssScoreThreshold(3)
    assert !underTest.match(request, report)
  }

  @Test
  void 'inclusion matching; exclude by coordinates'() {
    def request = new ComponentReportRequest()
    def report = new ComponentReport(
        coordinates: PackageUrl.parse('maven:foo/bar@1'),
        vulnerabilities: [
            new ComponentReportVulnerability(
                id: '1',
                cvssScore: 1
            ),
            new ComponentReportVulnerability(
                id: '2',
                cvssScore: 2
            )
        ]
    )

    request.getExcludeCoordinates().add(new MavenCoordinates('a', 'b', '1'))
    assert underTest.match(request, report)

    request.getExcludeCoordinates().add(new MavenCoordinates('foo', 'bar', '1'))
    assert !underTest.match(request, report)
  }
}
