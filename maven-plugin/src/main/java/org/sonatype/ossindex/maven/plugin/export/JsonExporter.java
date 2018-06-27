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
package org.sonatype.ossindex.maven.plugin.export;

import java.io.Writer;

import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.ossindex.maven.common.ComponentReportResult;
import org.sonatype.ossindex.service.client.internal.GsonMarshaller;

/**
 * ???
 *
 * @since ???
 */
@Named
@Singleton
public class JsonExporter
  extends ExporterSupport
{
  @Override
  protected boolean accept(final String filename) {
    return filename.endsWith(".json");
  }

  @Override
  protected void export(final ComponentReportResult result, final Writer writer) throws Exception {
    // HACK: this is internal to the client
    GsonMarshaller marshaller = new GsonMarshaller();

    String json = marshaller.marshal(result);
    writer.write(json);
  }
}
