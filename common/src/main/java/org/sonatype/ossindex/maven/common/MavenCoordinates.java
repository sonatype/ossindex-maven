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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.sonatype.goodies.packageurl.PackageUrl;

import com.google.common.base.Splitter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Maven coordinates.
 *
 * @since 3.0.0
 */
public class MavenCoordinates
{
  @Nullable
  private String groupId;

  @Nullable
  private String artifactId;

  @Nullable
  private String type;

  @Nullable
  private String classifier;

  @Nullable
  private String version;

  public MavenCoordinates(@Nullable final String groupId,
                          @Nullable final String artifactId,
                          @Nullable final String version)
  {
    this(groupId, artifactId, null, null, version);
  }

  public MavenCoordinates(@Nullable final String groupId,
                          @Nullable final String artifactId,
                          @Nullable final String type,
                          @Nullable final String classifier,
                          @Nullable final String version)
  {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.classifier = classifier;
    this.type = type;
    this.version = version;
  }

  public MavenCoordinates() {
    // empty
  }

  @Nullable
  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(@Nullable final String groupId) {
    this.groupId = groupId;
  }

  @Nullable
  public String getArtifactId() {
    return artifactId;
  }

  public void setArtifactId(@Nullable final String artifactId) {
    this.artifactId = artifactId;
  }

  @Nullable
  public String getType() {
    return type;
  }

  public void setType(@Nullable final String type) {
    this.type = type;
  }

  @Nullable
  public String getClassifier() {
    return classifier;
  }

  public void setClassifier(@Nullable final String classifier) {
    this.classifier = classifier;
  }

  @Nullable
  public String getVersion() {
    return version;
  }

  public void setVersion(@Nullable final String version) {
    this.version = version;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MavenCoordinates that = (MavenCoordinates) o;
    return Objects.equals(groupId, that.groupId) &&
        Objects.equals(artifactId, that.artifactId) &&
        Objects.equals(type, that.type) &&
        Objects.equals(classifier, that.classifier) &&
        Objects.equals(version, that.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, artifactId, type, classifier, version);
  }

  @Override
  public String toString() {
    StringBuilder buff = new StringBuilder();

    buff.append(groupId).append(':');
    buff.append(artifactId).append(':');
    if (type != null) {
      buff.append(type).append(':');
      if (classifier != null) {
        buff.append(classifier).append(':');
      }
    }
    buff.append(version);

    return buff.toString();
  }

  //
  // Conversion
  //

  /**
   * Convert {@link PackageUrl} to {@link MavenCoordinates}.
   */
  static MavenCoordinates from(final PackageUrl purl) {
    return new MavenCoordinates(purl.getNamespaceAsString(), purl.getName(), purl.getVersion());
  }

  /**
   * Convert {@link Artifact} to {@link MavenCoordinates}.
   */
  static MavenCoordinates from(final Artifact artifact, final boolean withClassifierAndExtension) {
    if (withClassifierAndExtension) {
      ArtifactHandler handler = artifact.getArtifactHandler();
      String extension = handler.getExtension();
      return new MavenCoordinates(
              artifact.getGroupId(),
              artifact.getArtifactId(),
              artifact.getVersion(),
              artifact.getClassifier(),
              extension
      );
    }
    else {
      return new MavenCoordinates(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());
    }
  }

  //
  // Helpers
  //

  static boolean isExcluded(final Set<MavenCoordinates> excluded, final Artifact artifact) {
    return excluded.contains(from(artifact, false)) ||
            excluded.contains(from(artifact, true));
  }

  //
  // Parsing
  //

  // SEE: https://github.com/apache/maven-resolver/blob/maven-resolver-1.1.1/maven-resolver-api/src/main/java/org/eclipse/aether/artifact/DefaultArtifact.java#L56

  private static final Pattern PATTERN = Pattern.compile("([^: ]+):([^: ]+)(:([^: ]*)(:([^: ]+))?)?:([^: ]+)");

  /**
   * Parse coordinates from string value.
   */
  public static MavenCoordinates parse(final String value) {
    checkNotNull(value);

    Matcher m = PATTERN.matcher(value);
    checkArgument(m.matches(),
        "Invalid coordinates %s; expected format: <groupId>:<artifactId>[:<type>[:<classifier>]]:<version>",
        value);

    String groupId = m.group(1);
    String artifactId = m.group(2);
    String type = m.group(4);
    String classifier = m.group(6);
    String version = m.group(7);
    return new MavenCoordinates(groupId, artifactId, type, classifier, version);
  }

  /**
   * Parse coordinates (comma-separated) list from string value.
   */
  public static List<MavenCoordinates> parseList(final String value) {
    checkNotNull(value);
    List<MavenCoordinates> result = new LinkedList<>();
    for (String coordinates : Splitter.on(',').trimResults().omitEmptyStrings().split(value)) {
      result.add(parse(coordinates));
    }
    return result;
  }
}
