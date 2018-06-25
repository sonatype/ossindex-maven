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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
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
public class AuditLifecycleParticipant
    extends AbstractMavenLifecycleParticipant
{
  private static final Logger log = LoggerFactory.getLogger(AuditLifecycleParticipant.class);

  private final Auditor auditor;

  @Inject
  public AuditLifecycleParticipant(final Auditor auditor) {
    this.auditor = checkNotNull(auditor);
  }

  @Override
  public void afterSessionStart(final MavenSession session) throws MavenExecutionException {
    log.info("AFTER SESSION-START: {}", session);
  }

  @Override
  public void afterProjectsRead(final MavenSession session) throws MavenExecutionException {
    log.info("AFTER PROJECTS-READ: {}", session);
  }

  @Override
  public void afterSessionEnd(final MavenSession session) throws MavenExecutionException {
    log.info("AFTER SESSION-END: {}", session);
  }
}
