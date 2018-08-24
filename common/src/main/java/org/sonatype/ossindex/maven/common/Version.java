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

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper to load version information from build via {@link #RESOURCE} resource.
 *
 * @since 3.0.1
 */
@SuppressWarnings("Duplicates")
public class Version
{
  private static final Logger log = LoggerFactory.getLogger(Version.class);

  public static final String RESOURCE = "version.properties";

  public static final String UNKNOWN = "unknown";

  private final Class owner;

  Version(final Class owner) {
    this.owner = checkNotNull(owner);
  }

  private Properties load() {
    Properties result = new Properties();
    URL resource = owner.getResource(RESOURCE);
    if (resource == null) {
      log.warn("Missing resource: {}", RESOURCE);
    }
    else {
      log.debug("Resource: {}", resource);
      try {
        try (InputStream input = resource.openStream()) {
          result.load(input);
        }
      }
      catch (Exception e) {
        log.warn("Failed to load resource: {}", RESOURCE, e);
      }
      log.debug("Properties: {}", result);
    }
    return result;
  }

  private Properties properties;

  private Properties properties() {
    if (properties == null) {
      properties = load();
    }
    return properties;
  }

  private String property(final String name) {
    String value = properties().getProperty(name);
    if (value == null || value.contains("${")) {
      return UNKNOWN;
    }
    return value;
  }

  public String getVersion() {
    return property("version");
  }

  public String getTimestamp() {
    return property("timestamp");
  }

  public String getTag() {
    return property("tag");
  }

  @Override
  public String toString() {
    return String.format("%s (%s; %s)", getVersion(), getTimestamp(), getTag());
  }

  public static Version get() {
    return new Version(Version.class);
  }
}
