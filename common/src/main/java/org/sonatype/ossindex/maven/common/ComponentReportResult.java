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
package org.sonatype.ossindex.maven.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.sonatype.goodies.packageurl.PackageUrl;
import org.sonatype.ossindex.service.api.componentreport.ComponentReport;
import org.sonatype.ossindex.service.api.componentreport.ComponentReportVulnerability;

import org.apache.maven.artifact.Artifact;

/**
 * Component-report result.
 *
 * @since ???
 */
public class ComponentReportResult
{
  /**
   * All component-reports.
   */
  private Map<PackageUrl, ComponentReport> reports;

  /**
   * Component-reports which have matching vulnerabilities.
   */
  private Map<Artifact, ComponentReport> vulnerable;

  /**
   * Excluded components which had vulnerabilities.
   */
  private Set<PackageUrl> excludedCoordinates;

  /**
   * Excluded vulnerabilities (by vuln-id or by cvss-score threshold).
   */
  private Set<String> excludedVulnerabilityIds;

  public Map<PackageUrl, ComponentReport> getReports() {
    return reports;
  }

  public void setReports(final Map<PackageUrl, ComponentReport> reports) {
    this.reports = reports;
  }

  public Map<Artifact, ComponentReport> getVulnerable() {
    return vulnerable;
  }

  public void setVulnerable(final Map<Artifact, ComponentReport> vulnerable) {
    this.vulnerable = vulnerable;
  }

  public boolean hasVulnerable() {
    return !vulnerable.isEmpty();
  }

  public Set<PackageUrl> getExcludedCoordinates() {
    if (excludedCoordinates == null) {
      excludedCoordinates = new HashSet<>();
    }
    return excludedCoordinates;
  }

  public void setExcludedCoordinates(final Set<PackageUrl> excludedCoordinates) {
    this.excludedCoordinates = excludedCoordinates;
  }

  public Set<String> getExcludedVulnerabilityIds() {
    if (excludedVulnerabilityIds == null) {
      excludedVulnerabilityIds = new HashSet<>();
    }
    return excludedVulnerabilityIds;
  }

  public void setExcludedVulnerabilityIds(final Set<String> excludedVulnerabilityIds) {
    this.excludedVulnerabilityIds = excludedVulnerabilityIds;
  }

  public boolean hasExclusions() {
    return getExcludedCoordinates().size() + getExcludedVulnerabilityIds().size() != 0;
  }

  /**
   * Render a multi-line string explaining the vulnerabilities.
   */
  public String explain() {
    StringBuilder buff = new StringBuilder();

    if (vulnerable.isEmpty()) {
      buff.append("No vulnerable components detected");
    }
    else {
      buff.append("Detected ").append(vulnerable.size()).append(" vulnerable components:\n");

      // TODO: add more details here for what was excluded

      // include details about each vulnerable dependency
      for (Map.Entry<Artifact, ComponentReport> entry : vulnerable.entrySet()) {
        Artifact artifact = entry.getKey();
        ComponentReport report = entry.getValue();

        // describe artifact and link to component information
        buff.append("  ")
            .append(artifact).append("; ")
            .append(report.getReference())
            .append("\n");

        // include terse details about vulnerability and link to more detailed information
        Iterator<ComponentReportVulnerability> iter = report.getVulnerabilities().iterator();
        while (iter.hasNext()) {
          ComponentReportVulnerability vulnerability = iter.next();
          buff.append("    * ")
              .append(vulnerability.getTitle());

          Float cvssScore = vulnerability.getCvssScore();
          if (cvssScore != null) {
              buff.append(" (").append(cvssScore).append(')');
          }

          buff.append("; ").append(vulnerability.getReference());
          if (iter.hasNext()) {
            buff.append("\n");
          }
        }
      }
    }

    return buff.toString();
  }
}
