/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jsecurity.web.filter;

import org.jsecurity.util.AntPathMatcher;
import static org.jsecurity.util.StringUtils.split;
import static org.jsecurity.web.WebUtils.getPathWithinApplication;
import static org.jsecurity.web.WebUtils.toHttp;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>Base class for Filters that will process only specified paths and allow all others to pass through.</p>
 *
 * @author Les Hazlewood
 * @since 0.9
 */
public abstract class PathMatchingFilter extends RedirectingFilter implements PathConfigProcessor {

    protected AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * A collection of path-to-config entries where the key is a path which this filter should process and
     * the value is the (possibly null) configuration element specific to this Filter for that specific path.
     *
     * <p>To put it another way, the keys are the paths (urls) that this Filter will process.
     * <p>The values are filter-specific data that this Filter should use when processing the corresponding
     * key (path).  The values can be null if no Filter-specific config was specified for that url.
     */
    protected Map<String, Object> appliedPaths = new LinkedHashMap<String, Object>();


    public void processPathConfig(String path, String config) {
        String[] values = null;
        if (config != null) {
            values = split(config);
        }

        this.appliedPaths.put(path, values);
    }

    /**
     * Default implemenation of this method. Always returns true. Sub-classes should override this method.
     *
     * @param request
     * @param response
     * @return true - allow the request chain to continue in this default implementation
     * @throws Exception
     */
    public boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {

        if (this.appliedPaths != null && !this.appliedPaths.isEmpty()) {

            String requestURI = getPathWithinApplication(toHttp(request));

            // If URL path isn't matched, we allow the request to go through so default to true
            boolean continueChain = true;
            for (String path : this.appliedPaths.keySet()) {

                if (log.isTraceEnabled()) {
                    log.trace("Attempting to match path [" + path + "] against current requestURI [" + requestURI + "]...");
                }

                // If the path does match, then pass on to the subclass implementation for specific checks:
                if (pathMatcher.match(path, requestURI)) {
                    if (log.isTraceEnabled()) {
                        log.trace("matched path [" + path + "] for requestURI [" + requestURI + "].  " +
                                "Performing onPreHandle check...");
                    }
                    Object config = this.appliedPaths.get(path);
                    continueChain = onPreHandle(request, response, config);
                }

                if (!continueChain) {
                    //it is expected the subclass renders the response directly, so just return false
                    return false;
                }
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("appliedPaths property is null or empty.  This Filter will passthrough immediately.");
            }
        }

        return true;
    }

    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object configValue) throws Exception {
        return true;
    }
}
