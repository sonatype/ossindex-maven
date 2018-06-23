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

import org.sonatype.ossindex.service.client.OssindexClientConfiguration;

import org.apache.maven.artifact.Artifact;

/**
 * ???
 *
 * @since ???
 */
public class ComponentReportRequest
{
  private OssindexClientConfiguration clientConfiguration;

  private Collection<Artifact> components;

  public OssindexClientConfiguration getClientConfiguration() {
    return clientConfiguration;
  }

  public void setClientConfiguration(final OssindexClientConfiguration clientConfiguration) {
    this.clientConfiguration = clientConfiguration;
  }

  public Collection<Artifact> getComponents() {
    return components;
  }

  public void setComponents(final Collection<Artifact> components) {
    this.components = components;
  }
}
