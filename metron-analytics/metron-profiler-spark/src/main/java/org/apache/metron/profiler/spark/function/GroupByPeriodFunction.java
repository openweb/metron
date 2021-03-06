/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.metron.profiler.spark.function;

import org.apache.metron.profiler.MessageRoute;
import org.apache.metron.profiler.ProfilePeriod;
import org.apache.spark.api.java.function.MapFunction;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.apache.metron.profiler.spark.BatchProfilerConfig.PERIOD_DURATION;
import static org.apache.metron.profiler.spark.BatchProfilerConfig.PERIOD_DURATION_UNITS;

/**
 * Defines how {@link MessageRoute} are grouped.
 *
 * The routes are grouped by (profile, entity, periodId) so that all of the required
 * messages are available to produce a {@link org.apache.metron.profiler.ProfileMeasurement}.
 */
public class GroupByPeriodFunction implements MapFunction<MessageRoute, String> {

  /**
   * The duration of each profile period.
   */
  private int periodDuration;

  /**
   * The units of the period duration.
   */
  private TimeUnit periodDurationUnits;

  public GroupByPeriodFunction(Properties profilerProperties) {
    periodDurationUnits = TimeUnit.valueOf(PERIOD_DURATION_UNITS.get(profilerProperties, String.class));
    periodDuration = PERIOD_DURATION.get(profilerProperties, Integer.class);
  }

  @Override
  public String call(MessageRoute route) {
    ProfilePeriod period = ProfilePeriod.fromTimestamp(route.getTimestamp(), periodDuration, periodDurationUnits);
    return route.getProfileDefinition().getProfile() + "-" + route.getEntity() + "-" + period.getPeriod();
  }
}
