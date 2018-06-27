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
package org.sonatype.ossindex.maven.plugin.export;

import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.sonatype.ossindex.service.api.componentreport.ComponentReport;
import org.sonatype.ossindex.service.api.componentreport.ComponentReportVulnerability;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Exported report container.
 *
 * We have to provide some pre-rendered types for {@link org.apache.maven.artifact.Artifact}
 * and {@link org.sonatype.ossindex.maven.common.MavenCoordinates}.
 *
 * @since ???
 * @see org.sonatype.ossindex.maven.common.ComponentReportResult
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ComponentReportExport
{
  /**
   * All component-reports.
   *
   * Maps artifact (as rendered maven-coordinates) to report.
   */
  @JsonProperty
  @XmlElement
  private Map<String, ComponentReport> reports;

  /**
   * Component-reports which have matching vulnerabilities.
   *
   * Maps artifact (as rendered maven-coordinates) to report.
   */
  @JsonProperty
  @XmlElement
  private Map<String, ComponentReport> vulnerable;

  /**
   * Excluded components which had vulnerabilities.
   */
  @JsonProperty
  @XmlElementWrapper
  @XmlElement(name="coordinates")
  private Set<String> excludedCoordinates;

  /**
   * Excluded vulnerabilities (by vuln-id or by cvss-score threshold).
   */
  @JsonProperty
  @XmlElementWrapper
  @XmlElement(name="vulnerability")
  private Set<ComponentReportVulnerability> excludedVulnerabilities;

  public Map<String, ComponentReport> getReports() {
    return reports;
  }

  public void setReports(final Map<String, ComponentReport> reports) {
    this.reports = reports;
  }

  public Map<String, ComponentReport> getVulnerable() {
    return vulnerable;
  }

  public void setVulnerable(final Map<String, ComponentReport> vulnerable) {
    this.vulnerable = vulnerable;
  }

  public Set<String> getExcludedCoordinates() {
    return excludedCoordinates;
  }

  public void setExcludedCoordinates(final Set<String> excludedCoordinates) {
    this.excludedCoordinates = excludedCoordinates;
  }

  public Set<ComponentReportVulnerability> getExcludedVulnerabilities() {
    return excludedVulnerabilities;
  }

  public void setExcludedVulnerabilities(final Set<ComponentReportVulnerability> excludedVulnerabilities) {
    this.excludedVulnerabilities = excludedVulnerabilities;
  }
}
