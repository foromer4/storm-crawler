/**
 * Licensed to DigitalPebble Ltd under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * DigitalPebble licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.digitalpebble.stormcrawler.protocol;

import crawlercommons.robots.BaseRobotRules;

/**
 * Decorator for RobotRules which indicate whether the rules came from a server
 * or a cache. This allows to bypass politeness during the Fetching.
 **/
public class RobotRules extends BaseRobotRules {

    private boolean cached = false;
    private BaseRobotRules rules;

    RobotRules(BaseRobotRules r, boolean c) {
        rules = r;
        cached = c;
    }

    public boolean fromCache() {
        return cached;
    }

    @Override
    public boolean isAllowed(String url) {
        return rules.isAllowed(url);
    }

    @Override
    public boolean isAllowAll() {
        return rules.isAllowAll();
    }

    @Override
    public boolean isAllowNone() {
        return rules.isAllowNone();
    }

}
