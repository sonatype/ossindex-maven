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

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.eclipse.aether.RepositoryEvent;
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
public class AuditEventSpy
    extends AbstractEventSpy
{
  private static final Logger log = LoggerFactory.getLogger(AuditEventSpy.class);

  private final Auditor auditor;

  @Inject
  public AuditEventSpy(final Auditor auditor) {
    this.auditor = checkNotNull(auditor);
  }

  @Override
  public void init(final Context context) throws Exception {
    log.info("INIT: {}", context);
    for (Map.Entry<String, Object> entry : context.getData().entrySet()) {
      log.info("  {}={}", entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void onEvent(final Object event) throws Exception {
    log.info("EVENT {}: {}", event.getClass().getSimpleName(), event);

    if (event instanceof RepositoryEvent) {
      RepositoryEvent target = (RepositoryEvent) event;
      if (target.getType() == RepositoryEvent.EventType.ARTIFACT_RESOLVED) {
        auditor.track(target.getArtifact());
      }
    }
    else if (event instanceof ExecutionEvent) {
      ExecutionEvent target = (ExecutionEvent) event;
      if (target.getType() == ExecutionEvent.Type.ProjectSucceeded) {
        auditor.audit();
      }
    }
  }

  @Override
  public void close() throws Exception {
    log.info("CLOSE");
  }
}
