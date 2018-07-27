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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sonatype.ossindex.service.api.componentreport.ComponentReport;
import org.sonatype.ossindex.service.api.componentreport.ComponentReportVulnerability;

import org.apache.maven.artifact.Artifact;

/**
 * Component-report result.
 *
 * @since 3.0.0
 */
public class ComponentReportResult
{
  /**
   * All component-reports.
   */
  private Map<Artifact, ComponentReport> reports;

  /**
   * Component-reports which have matching vulnerabilities.
   */
  private Map<Artifact, ComponentReport> vulnerable;

  /**
   * Excluded components which had vulnerabilities.
   */
  private Set<MavenCoordinates> excludedCoordinates;

  /**
   * Excluded vulnerabilities (by vuln-id or by cvss-score threshold).
   */
  private Set<ComponentReportVulnerability> excludedVulnerabilities;

  public Map<Artifact, ComponentReport> getReports() {
    if (reports == null) {
      reports = new HashMap<>();
    }
    return reports;
  }

  public void setReports(final Map<Artifact, ComponentReport> reports) {
    this.reports = reports;
  }

  public Map<Artifact, ComponentReport> getVulnerable() {
    if (vulnerable == null) {
      vulnerable = new HashMap<>();
    }
    return vulnerable;
  }

  public void setVulnerable(final Map<Artifact, ComponentReport> vulnerable) {
    this.vulnerable = vulnerable;
  }

  /**
   * Returns {@code true} if report has any vulnerabilities.
   */
  public boolean hasVulnerable() {
    return !getVulnerable().isEmpty();
  }

  public Set<MavenCoordinates> getExcludedCoordinates() {
    if (excludedCoordinates == null) {
      excludedCoordinates = new HashSet<>();
    }
    return excludedCoordinates;
  }

  public void setExcludedCoordinates(final Set<MavenCoordinates> excludedCoordinates) {
    this.excludedCoordinates = excludedCoordinates;
  }

  public Set<ComponentReportVulnerability> getExcludedVulnerabilities() {
    if (excludedVulnerabilities == null) {
      excludedVulnerabilities = new HashSet<>();
    }
    return excludedVulnerabilities;
  }

  public void setExcludedVulnerabilities(final Set<ComponentReportVulnerability> excludedVulnerabilities) {
    this.excludedVulnerabilities = excludedVulnerabilities;
  }

  /**
   * Returns {@code true} if report has any coordinates or vulnerabilities that were excluded.
   */
  public boolean hasExclusions() {
    return getExcludedCoordinates().size() + getExcludedVulnerabilities().size() != 0;
  }

  /**
   * Render a multi-line string explaining the vulnerabilities.
   */
  public String explain() {
    StringBuilder buff = new StringBuilder();

    if (getVulnerable().isEmpty()) {
      buff.append("No vulnerable components detected");
    }
    else {
      buff.append("Detected ").append(getVulnerable().size()).append(" vulnerable components:\n");

      // include details about each vulnerable dependency
      for (Map.Entry<Artifact, ComponentReport> entry : getVulnerable().entrySet()) {
        Artifact artifact = entry.getKey();
        ComponentReport report = entry.getValue();

        // describe artifact and link to component information
        buff.append("  ")
            .append(artifact).append("; ")
            .append(report.getReference())
            .append("\n");

        // include terse details about vulnerability and link to more detailed information
        for (ComponentReportVulnerability vulnerability : report.getVulnerabilities()) {
          // if vulnerability was excluded, skip; report will still contain this for inspection however
          if (getExcludedVulnerabilities().contains(vulnerability)) {
            continue;
          }

          buff.append("    * ");
          explainVulnerability(buff, vulnerability);
        }
      }

      // if anything was included, include a brief summary for clarity
      if (hasExclusions()) {
        buff.append("\n");
        Set<ComponentReportVulnerability> excludedVulnerabilities = getExcludedVulnerabilities();
        if (!excludedVulnerabilities.isEmpty()) {
          buff.append("Excluded vulnerabilities:\n");
          for (ComponentReportVulnerability vulnerability : excludedVulnerabilities) {
            buff.append("  - ");
            explainVulnerability(buff, vulnerability);
          }
        }

        Set<MavenCoordinates> excludedCoordinates = getExcludedCoordinates();
        if (!excludedCoordinates.isEmpty()) {
          buff.append("Excluded coordinates:\n");
          for (MavenCoordinates coordinates : excludedCoordinates) {
            // TODO: would be nice to give more details here, may need to refactor model slightly to support that
            buff.append("  - ").append(coordinates).append("\n");
          }
        }
      }
    }

    return buff.toString();
  }

  private void explainVulnerability(final StringBuilder buff, final ComponentReportVulnerability vulnerability) {
    buff.append(vulnerability.getTitle());

    Float cvssScore = vulnerability.getCvssScore();
    if (cvssScore != null) {
      buff.append(" (").append(cvssScore).append(')');
    }

    buff.append("; ").append(vulnerability.getReference()).append("\n");
  }
}
