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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Nullable;

import org.sonatype.ossindex.service.client.OssindexClientConfiguration;
import org.sonatype.ossindex.service.client.transport.UserAgentBuilder.Product;

import org.apache.maven.artifact.Artifact;

/**
 * Component-report request.
 *
 * @since 3.0.0
 */
public class ComponentReportRequest
{
  private OssindexClientConfiguration clientConfiguration;

  @Nullable
  private Properties properties;

  /**
   * Artifact (components) to analyze.
   */
  private Collection<Artifact> components;

  /**
   * Maven coordinates to exclude from matching.
   */
  private Set<MavenCoordinates> excludeCoordinates;

  /**
   * CVSS-score threshold.  Vulnerabilities with scores greater-than or equal will match.
   */
  private float cvssScoreThreshold = 0;

  /**
   * Vulnerability identifiers to exclude from matching.
   */
  private Set<String> excludeVulnerabilityIds;

  /**
   * Additional {@code User-Agent} products.
   */
  @Nullable
  private List<Product> products;

  public OssindexClientConfiguration getClientConfiguration() {
    return clientConfiguration;
  }

  public void setClientConfiguration(final OssindexClientConfiguration clientConfiguration) {
    this.clientConfiguration = clientConfiguration;
  }

  /**
   * @since ???
   */
  @Nullable
  public Properties getProperties() {
    return properties;
  }

  /**
   * @since ???
   */
  public void setProperties(@Nullable final Properties properties) {
    this.properties = properties;
  }

  public Collection<Artifact> getComponents() {
    return components;
  }

  public void setComponents(final Collection<Artifact> components) {
    this.components = components;
  }

  public Set<MavenCoordinates> getExcludeCoordinates() {
    if (excludeCoordinates == null) {
      excludeCoordinates = new HashSet<>();
    }
    return excludeCoordinates;
  }

  public void setExcludeCoordinates(final Set<MavenCoordinates> excludeCoordinates) {
    this.excludeCoordinates = excludeCoordinates;
  }

  public float getCvssScoreThreshold() {
    return cvssScoreThreshold;
  }

  public void setCvssScoreThreshold(final float cvssScoreThreshold) {
    this.cvssScoreThreshold = cvssScoreThreshold;
  }

  public Set<String> getExcludeVulnerabilityIds() {
    if (excludeVulnerabilityIds == null) {
      excludeVulnerabilityIds = new HashSet<>();
    }
    return excludeVulnerabilityIds;
  }

  public void setExcludeVulnerabilityIds(final Set<String> excludeVulnerabilityIds) {
    this.excludeVulnerabilityIds = excludeVulnerabilityIds;
  }

  /**
   * @since 3.0.1
   */
  @Nullable
  public List<Product> getProducts() {
    return products;
  }

  /**
   * @since 3.0.1
   */
  public void setProducts(@Nullable final List<Product> products) {
    this.products = products;
  }
}
