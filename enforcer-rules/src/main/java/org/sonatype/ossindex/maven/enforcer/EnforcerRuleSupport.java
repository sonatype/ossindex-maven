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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.maven.enforcer.rule.api.EnforcerLevel;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRule2;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

/**
 * Support for {@link EnforcerRule} implementations.
 *
 * @since ???
 */
public abstract class EnforcerRuleSupport
    implements EnforcerRule2
{
  private EnforcerLevel level = EnforcerLevel.ERROR;

  public void setLevel(final EnforcerLevel level) {
    this.level = level;
  }

  @Nonnull
  @Override
  public EnforcerLevel getLevel() {
    return level;
  }

  @Override
  public boolean isCacheable() {
    return false;
  }

  @Nullable
  @Override
  public String getCacheId() {
    return "0";
  }

  @Override
  public boolean isResultValid(@Nonnull EnforcerRule rule) {
    return false;
  }

  @SuppressWarnings("unchecked")
  protected <T> T lookup(final EnforcerRuleHelper helper, final String expression, final Class<T> type) {
    try {
      return (T) helper.evaluate(expression);
    }
    catch (ExpressionEvaluationException e) {
      throw new RuntimeException("Failed to evaluate expression: " + expression, e);
    }
  }

  protected <T> T lookup(final EnforcerRuleHelper helper, final Class<T> type) {
    try {
      return helper.getComponent(type);
    }
    catch (ComponentLookupException e) {
      throw new RuntimeException("Failed to lookup component: " + type.getSimpleName(), e);
    }
  }
}
