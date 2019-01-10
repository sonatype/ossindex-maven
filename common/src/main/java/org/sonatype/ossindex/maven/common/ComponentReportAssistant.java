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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.goodies.packageurl.PackageUrl;
import org.sonatype.ossindex.service.api.componentreport.ComponentReport;
import org.sonatype.ossindex.service.api.componentreport.ComponentReportVulnerability;
import org.sonatype.ossindex.service.client.OssindexClient;
import org.sonatype.ossindex.service.client.OssindexClientConfiguration;
import org.sonatype.ossindex.service.client.cache.DirectoryCache;
import org.sonatype.ossindex.service.client.internal.OssindexClientImpl;
import org.sonatype.ossindex.service.client.internal.VersionSupplier;
import org.sonatype.ossindex.service.client.marshal.GsonMarshaller;
import org.sonatype.ossindex.service.client.marshal.Marshaller;
import org.sonatype.ossindex.service.client.transport.HttpClientTransport;
import org.sonatype.ossindex.service.client.transport.Transport;
import org.sonatype.ossindex.service.client.transport.UserAgentBuilder;
import org.sonatype.ossindex.service.client.transport.UserAgentBuilder.Product;
import org.sonatype.ossindex.service.client.transport.UserAgentSupplier;

import com.google.common.annotations.VisibleForTesting;
import org.apache.maven.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Component-report assistant.
 *
 * @since 3.0.0
 */
@Named
@Singleton
public class ComponentReportAssistant
{
  private static final Logger log = LoggerFactory.getLogger(ComponentReportAssistant.class);

  /**
   * Request component-report and apply exclusion matching.
   */
  public ComponentReportResult request(final ComponentReportRequest request) {
    checkNotNull(request);
    checkState(request.getComponents() != null, "Missing: components");
    checkState(!request.getComponents().isEmpty(), "At least one component must be specified");
    checkState(request.getClientConfiguration() != null, "Missing: client-configuration");

    log.info("Checking for vulnerabilities; {} artifacts", request.getComponents().size());

    // generate package-url and map back to artifacts for result handling
    Map<PackageUrl, Artifact> purlArtifacts = new HashMap<>();
    for (Artifact artifact : request.getComponents()) {
      log.debug("  {}", artifact);
      purlArtifacts.put(packageUrl(artifact), artifact);
    }

    log.info("Exclude coordinates: {}", request.getExcludeCoordinates());
    log.info("Exclude vulnerability identifiers: {}", request.getExcludeVulnerabilityIds());
    log.info("CVSS-score threshold: {}", request.getCvssScoreThreshold());

    ComponentReportResult result = new ComponentReportResult();
    OssindexClient client = createClient(request);
    try {
      Map<PackageUrl, ComponentReport> reports = client.requestComponentReports(new ArrayList<>(purlArtifacts.keySet()));
      log.trace("Fetched {} component-reports", reports.size());

      for (Map.Entry<PackageUrl, ComponentReport> entry : reports.entrySet()) {
        PackageUrl purl = entry.getKey();
        Artifact artifact = purlArtifacts.get(purl);
        ComponentReport report = entry.getValue();

        // FIXME: figure out why we have null values; this shouldn't normally happen
        if (report == null) {
          log.warn("Missing report for: {}", purl);
          continue;
        }
        result.getReports().put(artifact, report);

        // filter and maybe record vulnerable mapping
        if (match(request, result, report)) {
          result.getVulnerable().put(artifact, report);
        }
      }
    }
    catch (Exception e) {
      log.warn("Failed to fetch component-reports", e);
    }
    finally {
      try {
        client.close();
      }
      catch (Exception e) {
        log.warn("Failed to close client", e);
      }
    }

    return result;
  }

  /**
   * Convert {@link Artifact} to {@link PackageUrl}.
   */
  @VisibleForTesting
  static PackageUrl packageUrl(final Artifact artifact) {
    return new PackageUrl.Builder()
        .type("maven")
        .namespace(artifact.getGroupId())
        .name(artifact.getArtifactId())
        .version(artifact.getVersion())
        .build();
  }

  /**
   * Create client to communicate with OSS Index.
   */
  @VisibleForTesting
  OssindexClient createClient(final ComponentReportRequest request) {
    UserAgentSupplier userAgent = new UserAgentSupplier(new VersionSupplier().get())
    {
      /**
       * Customize with details from request if present.
       */
      @Override
      protected void customize(final UserAgentBuilder builder) {
        List<Product> products = request.getProducts();
        if (products != null) {
          for (Product product : products) {
            builder.product(product);
          }
        }
      }
    };
    Transport transport = new HttpClientTransport(userAgent);
    Marshaller marshaller = new GsonMarshaller();

    OssindexClientConfiguration config = request.getClientConfiguration();

    // maybe disable persistent cache
    boolean cacheDisabled = PropertyHelper.getBoolean(request.getProperties(), "ossindex.cache.disable", false);
    if (cacheDisabled) {
      // null will default to memory cache
      config.setCacheConfiguration(null);
    }
    else if (config.getCacheConfiguration() == null) {
      // if cache not otherwise configured, then prepare directory cache
      DirectoryCache.Configuration cacheConfig = new DirectoryCache.Configuration();

      // allow user to change the default location of the cache
      File cacheDir = PropertyHelper.getFile(request.getProperties(), "ossindex.cache.directory");
      if (cacheDir != null) {
        cacheConfig.setBaseDir(cacheDir.toPath());
      }

      config.setCacheConfiguration(cacheConfig);
    }

    return new OssindexClientImpl(config, transport, marshaller);
  }

  /**
   * Apply exclusion matching rules.
   */
  @VisibleForTesting
  boolean match(final ComponentReportRequest request, final ComponentReportResult result, final ComponentReport report) {
    List<ComponentReportVulnerability> vulnerabilities = report.getVulnerabilities();

    // do not include if there were no vulnerabilities detected
    if (vulnerabilities.isEmpty()) {
      return false;
    }

    // do not include if component coordinates are excluded
    MavenCoordinates coordinates = MavenCoordinates.from(report.getCoordinates());
    if (request.getExcludeCoordinates().contains(coordinates)) {
      log.warn("Excluding coordinates: {}", coordinates);
      result.getExcludedCoordinates().add(coordinates);
      return false;
    }

    // else check each vulnerability for matches
    int matched = 0;
    float cvssScoreThreshold = request.getCvssScoreThreshold();
    Set<String> excludeVulnerabilityIds = request.getExcludeVulnerabilityIds();

    for (ComponentReportVulnerability vulnerability : vulnerabilities) {
      boolean include = false;

      Float cvssScore = vulnerability.getCvssScore();
      if (cvssScore != null) {
        if (cvssScore >= cvssScoreThreshold) {
          include = true;
        }
        else {
          log.warn("Excluding CVSS-score: {}", cvssScore);
        }
      }

      if (excludeVulnerabilityIds.contains(vulnerability.getId())) {
        log.warn("Excluding vulnerability ID: {}", vulnerability.getId());
        include = false;
      }

      if (include) {
        matched++;
      }
      else {
        result.getExcludedVulnerabilities().add(vulnerability);
      }
    }

    // if any match was found, then include
    return matched != 0;
  }
}
