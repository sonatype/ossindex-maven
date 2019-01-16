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
import java.util.Properties;

import javax.annotation.Nullable;

import org.joda.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Property helper.
 *
 * @since 3.0.3
 */
public class PropertyHelper
{
  private static final Logger log = LoggerFactory.getLogger(PropertyHelper.class);

  /**
   * Merge properties.
   */
  public static Properties merge(final Properties first, final Properties second) {
    checkNotNull(first);
    checkNotNull(second);

    Properties merged = new Properties();
    merged.putAll(first);
    merged.putAll(second);

    return merged;
  }

  /**
   * Get string property.
   */
  @Nullable
  public static String getString(final Properties properties, final String name) {
    checkNotNull(properties);
    checkNotNull(name);

    String value = properties.getProperty(name);
    log.trace("Property: {}={}", name, value);
    return value;
  }

  /**
   * Get boolean property.
   */
  public static boolean getBoolean(final Properties properties, final String name, final boolean defaultValue) {
    String value = getString(properties, name);
    if (value != null) {
      return Boolean.parseBoolean(value);
    }
    return defaultValue;
  }

  /**
   * Get file property.
   */
  @Nullable
  public static File getFile(final Properties properties, final String name) {
    String value = getString(properties, name);
    if (value != null) {
      return new File(value);
    }
    return null;
  }

  /**
   * Get duration property.
   *
   * @since 3.0.4
   */
  @Nullable
  public static Duration getDuration(final Properties properties, final String name) {
    String value = getString(properties, name);
    if (value != null) {
      try {
        return Duration.parse(value);
      }
      catch (IllegalArgumentException e) {
        log.warn("Invalid duration property: {}={}", name, value);
      }
    }
    return null;
  }
}
