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

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.execution.AbstractExecutionListener;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.ExecutionListener;
import org.codehaus.plexus.component.annotations.Component;

// FIXME: this does not appear to actually work or get picked up; not sure why (with inject or component)

/**
 * ???
 *
 * @since ???
 */
@Named
@Singleton
//@Component(role = ExecutionListener.class, hint = "ossindex")
public class ExecutionListenerImpl
    extends AbstractExecutionListener
{
  @Override
  public void projectDiscoveryStarted(final ExecutionEvent event) {
    System.out.println("projectDiscoveryStarted: " + event);
  }

  @Override
  public void sessionStarted(final ExecutionEvent event) {
    System.out.println("sessionStarted: " + event);
  }

  @Override
  public void sessionEnded(final ExecutionEvent event) {
    System.out.println("sessionEnded: " + event);
  }

  @Override
  public void projectSkipped(final ExecutionEvent event) {
    System.out.println("projectSkipped: " + event);
  }

  @Override
  public void projectStarted(final ExecutionEvent event) {
    System.out.println("projectStarted: " + event);
  }

  @Override
  public void projectSucceeded(final ExecutionEvent event) {
    System.out.println("projectSucceeded: " + event);
  }

  @Override
  public void projectFailed(final ExecutionEvent event) {
    System.out.println("projectFailed: " + event);
  }

  @Override
  public void forkStarted(final ExecutionEvent event) {
    System.out.println("forkStarted: " + event);
  }

  @Override
  public void forkSucceeded(final ExecutionEvent event) {
    System.out.println("forkSucceeded: " + event);
  }

  @Override
  public void forkFailed(final ExecutionEvent event) {
    System.out.println("forkFailed: " + event);
  }

  @Override
  public void mojoSkipped(final ExecutionEvent event) {
    System.out.println("mojoSkipped: " + event);
  }

  @Override
  public void mojoStarted(final ExecutionEvent event) {
    System.out.println("mojoStarted: " + event);
  }

  @Override
  public void mojoSucceeded(final ExecutionEvent event) {
    System.out.println("mojoSucceeded: " + event);
  }

  @Override
  public void mojoFailed(final ExecutionEvent event) {
    System.out.println("mojoFailed: " + event);
  }

  @Override
  public void forkedProjectStarted(final ExecutionEvent event) {
    System.out.println("forkedProjectStarted: " + event);
  }

  @Override
  public void forkedProjectSucceeded(final ExecutionEvent event) {
    System.out.println("forkedProjectSucceeded: " + event);
  }

  @Override
  public void forkedProjectFailed(final ExecutionEvent event) {
    System.out.println("forkedProjectFailed: " + event);
  }
}
