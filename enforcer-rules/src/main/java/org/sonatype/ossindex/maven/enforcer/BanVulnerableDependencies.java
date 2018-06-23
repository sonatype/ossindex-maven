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
package org.sonatype.ossindex.maven.enforcer;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.sonatype.ossindex.maven.common.ComponentReportAssistant;
import org.sonatype.ossindex.maven.common.ComponentReportRequest;
import org.sonatype.ossindex.maven.common.ComponentReportResult;
import org.sonatype.ossindex.service.client.OssindexClientConfiguration;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;

import static com.google.common.base.Preconditions.checkState;

/**
 * Enforcer rule to ban vulnerable dependencies.
 *
 * @since ???
 */
public class BanVulnerableDependencies
    extends EnforcerRuleSupport
{
  private ArtifactFilter filter;

  private boolean transitive = true;

  private OssindexClientConfiguration clientConfiguration = new OssindexClientConfiguration();

  @Nullable
  public ArtifactFilter getFilter() {
    return filter;
  }

  public void setFilter(@Nullable final ArtifactFilter filter) {
    this.filter = filter;
  }

  public boolean isTransitive() {
    return transitive;
  }

  public void setTransitive(final boolean transitive) {
    this.transitive = transitive;
  }

  public OssindexClientConfiguration getClientConfiguration() {
    return clientConfiguration;
  }

  public void setClientConfiguration(final OssindexClientConfiguration clientConfiguration) {
    this.clientConfiguration = clientConfiguration;
  }

  @Override
  public void execute(@Nonnull final EnforcerRuleHelper helper) throws EnforcerRuleException {
    new Task(helper).run();
  }

  /**
   * Encapsulates state for rule evaluation.
   */
  private class Task
  {
    private final Log log;

    private final MavenSession session;

    private final MavenProject project;

    private final DependencyGraphBuilder graphBuilder;

    private final ComponentReportAssistant reportAssistant;

    //private final OssindexClient client;

    public Task(final EnforcerRuleHelper helper) {
      this.log = helper.getLog();
      this.session = lookup(helper, "${session}", MavenSession.class);
      this.project = lookup(helper, "${project}", MavenProject.class);
      this.graphBuilder = lookup(helper, DependencyGraphBuilder.class);
      this.reportAssistant = lookup(helper, ComponentReportAssistant.class);

      checkState(clientConfiguration != null, "Missing configuration");
    }

    public void run() throws EnforcerRuleException {
      // skip if maven is in offline mode
      if (session.isOffline()) {
        log.warn("Skipping " + BanVulnerableDependencies.class.getSimpleName() + " evaluation; offline");
        return;
      }

      // skip if packaging is pom
      if ("pom".equals(project.getPackaging())) {
        log.debug("Skipping POM module");
        return;
      }

      // determine dependencies
      Set<Artifact> dependencies;
      try {
        dependencies = resolveDependencies();
      }
      catch (DependencyGraphBuilderException e) {
        throw new RuntimeException("Failed to resolve dependencies", e);
      }

      // skip if project has no dependencies
      if (dependencies.isEmpty()) {
        log.debug("Skipping; no dependencies found");
        return;
      }

      ComponentReportRequest reportRequest = new ComponentReportRequest();
      reportRequest.setClientConfiguration(clientConfiguration);
      reportRequest.setComponents(dependencies);

      ComponentReportResult reportResult = reportAssistant.request(reportRequest);

      if (reportResult.hasVulnerable()) {
        throw new EnforcerRuleException(reportResult.explain());
      }
    }

    /**
     * Resolve dependencies to inspect for vulnerabilities.
     */
    private Set<Artifact> resolveDependencies() throws DependencyGraphBuilderException {
      Set<Artifact> result = new HashSet<>();

      DependencyNode node = graphBuilder.buildDependencyGraph(project, filter);
      collectArtifacts(result, node);

      return result;
    }

    /**
     * Collect artifacts from dependency.
     *
     * Optionally including transitive dependencies if {@link #transitive} is {@code true}.
     */
    private void collectArtifacts(final Set<Artifact> artifacts, final DependencyNode node) {
      if (node.getChildren() != null) {
        for (DependencyNode child : node.getChildren()) {
          artifacts.add(child.getArtifact());

          if (transitive) {
            collectArtifacts(artifacts, child);
          }
        }
      }
    }
  }
}
