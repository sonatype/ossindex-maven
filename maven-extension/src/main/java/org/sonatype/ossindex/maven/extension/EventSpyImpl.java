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

import org.apache.maven.eventspy.AbstractEventSpy;

/**
 * ???
 *
 * @since ???
 */
@Named
@Singleton
public class EventSpyImpl
    extends AbstractEventSpy
{
  @Override
  public void init(final Context context) throws Exception {
    System.out.println("init: " + context);
  }

  @Override
  public void onEvent(final Object event) throws Exception {
    System.out.println("event (" + event.getClass().getSimpleName() + "): " + event);
  }

  @Override
  public void close() throws Exception {
    System.out.println("close");
  }
}
