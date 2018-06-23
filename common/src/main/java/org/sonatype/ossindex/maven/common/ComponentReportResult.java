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

import java.util.Iterator;
import java.util.Map;

import org.sonatype.goodies.packageurl.PackageUrl;
import org.sonatype.ossindex.service.api.componentreport.ComponentReport;
import org.sonatype.ossindex.service.api.componentreport.ComponentReportVulnerability;

import org.apache.maven.artifact.Artifact;

/**
 * ???
 *
 * @since ???
 */
public class ComponentReportResult
{
  private Map<PackageUrl, ComponentReport> reports;

  private Map<Artifact, ComponentReport> vulnerable;

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

  public String explain() {
    StringBuilder buff = new StringBuilder();

    if (vulnerable.isEmpty()) {
      buff.append("No vulnerable components detected");
    }
    else {
      buff.append("Detected ").append(vulnerable.size()).append(" vulnerable components:\n");

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
              .append(vulnerability.getTitle())
              .append("; ").append(vulnerability.getReference());
          if (iter.hasNext()) {
            buff.append("\n");
          }
        }
      }
    }

    return buff.toString();
  }
}