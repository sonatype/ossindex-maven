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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.packageurl.PackageUrl;
import org.sonatype.ossindex.service.api.componentreport.ComponentReport;
import org.sonatype.ossindex.service.client.OssindexClient;
import org.sonatype.ossindex.service.client.OssindexClientConfiguration;
import org.sonatype.ossindex.service.client.internal.GsonMarshaller;
import org.sonatype.ossindex.service.client.internal.OssindexClientImpl;
import org.sonatype.ossindex.service.client.internal.VersionSupplier;
import org.sonatype.ossindex.service.client.transport.HttpClientTransport;
import org.sonatype.ossindex.service.client.transport.Marshaller;
import org.sonatype.ossindex.service.client.transport.Transport;
import org.sonatype.ossindex.service.client.transport.UserAgentSupplier;

import org.apache.maven.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Component-report assistant.
 *
 * @since ???
 */
@Named
@Singleton
public class ComponentReportAssistant
{
  private static final Logger log = LoggerFactory.getLogger(ComponentReportAssistant.class);

  public ComponentReportResult request(final ComponentReportRequest request) {
    checkNotNull(request);
    checkState(request.getComponents() != null, "Missing: components");
    checkState(request.getClientConfiguration() != null, "Missing: client-configuration");

    log.info("Checking for vulnerabilities:");

    // generate package-url and map back to artifacts for result handling
    Map<PackageUrl, Artifact> requests = new HashMap<>();
    for (Artifact artifact : request.getComponents()) {
      log.info("  " + artifact);
      PackageUrl purl = new PackageUrl.Builder()
          .type("maven")
          .namespace(artifact.getGroupId())
          .name(artifact.getArtifactId())
          .version(artifact.getVersion())
          .build();
      requests.put(purl, artifact);
    }

    OssindexClient client = createClient(request.getClientConfiguration());
    ComponentReportResult result = new ComponentReportResult();
    Map<Artifact, ComponentReport> vulnerable = new HashMap<>();
    try {
      Map<PackageUrl, ComponentReport> reports = client.requestComponentReports(new ArrayList<>(requests.keySet()));
      log.trace("Fetched {} component-reports", reports.size());
      result.setReports(reports);

      // TODO: filter vulnerabilities from request

      for (Map.Entry<PackageUrl, ComponentReport> entry : reports.entrySet()) {
        PackageUrl purl = entry.getKey();
        Artifact artifact = requests.get(purl);
        ComponentReport report = entry.getValue();

        // if report contains any vulnerabilities then record artifact mapping
        if (!report.getVulnerabilities().isEmpty()) {
          vulnerable.put(artifact, report);
        }
      }
    }
    catch (Exception e) {
      log.warn("Failed to fetch component-reports", e);
    }
    result.setVulnerable(vulnerable);

    return result;
  }

  private OssindexClient createClient(final OssindexClientConfiguration config) {
    UserAgentSupplier userAgent = new UserAgentSupplier(new VersionSupplier().get());
    Transport transport = new HttpClientTransport(userAgent);
    Marshaller marshaller = new GsonMarshaller();
    return new OssindexClientImpl(config, transport, marshaller);
  }
}
