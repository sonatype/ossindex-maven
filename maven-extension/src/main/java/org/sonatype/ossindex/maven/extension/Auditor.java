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
package org.sonatype.ossindex.maven.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.aether.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ???
 *
 * @since ???
 */
@Named
@Singleton
public class Auditor
{
  private static final Logger log = LoggerFactory.getLogger(Auditor.class);

  private final List<Artifact> tracked = Collections.synchronizedList(new ArrayList<Artifact>());

  public void track(final Artifact artifact) {
    checkNotNull(artifact);
    tracked.add(artifact);
    log.info("Tracking: {}", artifact);
  }

  public void audit() {
    log.info("Audit");

    List<Artifact> artifacts;
    synchronized (tracked) {
      artifacts = new ArrayList<>(tracked);
      tracked.clear();
    }

    log.info("Audit {} artifacts:", artifacts.size());
    for (Artifact artifact : artifacts) {
      log.info("  {}", artifact);
    }

    // HACK: testing
    throw new RuntimeException("FOO");
  }
}
