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

import java.util.Objects;

import javax.annotation.Nullable;

import org.sonatype.goodies.packageurl.PackageUrl;

import com.google.common.base.MoreObjects;

/**
 * Maven coordinates for exclusion matching.
 *
 * @since ???
 */
public class MavenCoordinates
{
  @Nullable
  private String groupId;

  @Nullable
  private String artifactId;

  // TODO: for now we can skip these, but probably should add these one day when supported

  //private String classifier;

  //private String type;

  @Nullable
  private String version;

  public MavenCoordinates(@Nullable final String groupId,
                          @Nullable final String artifactId,
                          @Nullable final String version)
  {
    this.groupId = groupId;
    this.artifactId = artifactId;
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
  public String getVersion() {
    return version;
  }

  public void setVersion(@Nullable final String version) {
    this.version = version;
  }

  /**
   * Convert {@link PackageUrl} to {@link MavenCoordinates}.
   */
  static MavenCoordinates from(final PackageUrl purl) {
    return new MavenCoordinates(purl.getNamespaceAsString(), purl.getName(), purl.getVersion());
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
        Objects.equals(version, that.version);
  }

  @Override
  public int hashCode() {
    return Objects.hash(groupId, artifactId, version);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("groupId", groupId)
        .add("artifactId", artifactId)
        .add("version", version)
        .toString();
  }
}
