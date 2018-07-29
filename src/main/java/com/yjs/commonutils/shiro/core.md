shiro web 核心类
===================

##1.ShiroFilterFactoryBean

    这个类主要功能:
    
    1.1 配置SecurityManager
    1.2 配置 filterMap
    1.3 配置过滤链
    
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
    package org.apache.shiro.spring.web;
    
    import org.apache.shiro.config.Ini;
    import org.apache.shiro.mgt.SecurityManager;
    import org.apache.shiro.util.CollectionUtils;
    import org.apache.shiro.util.Nameable;
    import org.apache.shiro.util.StringUtils;
    import org.apache.shiro.web.config.IniFilterChainResolverFactory;
    import org.apache.shiro.web.filter.AccessControlFilter;
    import org.apache.shiro.web.filter.authc.AuthenticationFilter;
    import org.apache.shiro.web.filter.authz.AuthorizationFilter;
    import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
    import org.apache.shiro.web.filter.mgt.FilterChainManager;
    import org.apache.shiro.web.filter.mgt.FilterChainResolver;
    import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
    import org.apache.shiro.web.mgt.WebSecurityManager;
    import org.apache.shiro.web.servlet.AbstractShiroFilter;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.BeansException;
    import org.springframework.beans.factory.BeanInitializationException;
    import org.springframework.beans.factory.FactoryBean;
    import org.springframework.beans.factory.config.BeanPostProcessor;
    
    import javax.servlet.Filter;
    import java.util.LinkedHashMap;
    import java.util.Map;
    
    /**
     * {@link org.springframework.beans.factory.FactoryBean FactoryBean} to be used in Spring-based web applications for
     * defining the master Shiro Filter.
     * <h4>Usage</h4>
     * Declare a DelegatingFilterProxy in {@code web.xml}, matching the filter name to the bean id:
     * <pre>
     * &lt;filter&gt;
     *   &lt;filter-name&gt;<b>shiroFilter</b>&lt;/filter-name&gt;
     *   &lt;filter-class&gt;org.springframework.web.filter.DelegatingFilterProxy&lt;filter-class&gt;
     *   &lt;init-param&gt;
     *    &lt;param-name&gt;targetFilterLifecycle&lt;/param-name&gt;
     *     &lt;param-value&gt;true&lt;/param-value&gt;
     *   &lt;/init-param&gt;
     * &lt;/filter&gt;
     * </pre>
     * Then, in your spring XML file that defines your web ApplicationContext:
     * <pre>
     * &lt;bean id="<b>shiroFilter</b>" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean"&gt;
     *    &lt;property name="securityManager" ref="securityManager"/&gt;
     *    &lt;!-- other properties as necessary ... --&gt;
     * &lt;/bean&gt;
     * </pre>
     * <h4>Filter Auto-Discovery</h4>
     * While there is a {@link #setFilters(java.util.Map) filters} property that allows you to assign a filter beans
     * to the 'pool' of filters available when defining {@link #setFilterChainDefinitions(String) filter chains}, it is
     * optional.
     * <p/>
     * This implementation is also a {@link BeanPostProcessor} and will acquire
     * any {@link javax.servlet.Filter Filter} beans defined independently in your Spring application context.  Upon
     * discovery, they will be automatically added to the {@link #setFilters(java.util.Map) map} keyed by the bean ID.
     * That ID can then be used in the filter chain definitions, for example:
     *
     * <pre>
     * &lt;bean id="<b>myCustomFilter</b>" class="com.class.that.implements.javax.servlet.Filter"/&gt;
     * ...
     * &lt;bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean"&gt;
     *    ...
     *    &lt;property name="filterChainDefinitions"&gt;
     *        &lt;value&gt;
     *            /some/path/** = authc, <b>myCustomFilter</b>
     *        &lt;/value&gt;
     *    &lt;/property&gt;
     * &lt;/bean&gt;
     * </pre>
     * <h4>Global Property Values</h4>
     * Most Shiro servlet Filter implementations exist for defining custom Filter
     * {@link #setFilterChainDefinitions(String) chain definitions}.  Most implementations subclass one of the
     * {@link AccessControlFilter}, {@link AuthenticationFilter}, {@link AuthorizationFilter} classes to simplify things,
     * and each of these 3 classes has configurable properties that are application-specific.
     * <p/>
     * A dilemma arises where, if you want to for example set the application's 'loginUrl' for any Filter, you don't want
     * to have to manually specify that value for <em>each</em> filter instance definied.
     * <p/>
     * To prevent configuration duplication, this implementation provides the following properties to allow you
     * to set relevant values in only one place:
     * <ul>
     * <li>{@link #setLoginUrl(String)}</li>
     * <li>{@link #setSuccessUrl(String)}</li>
     * <li>{@link #setUnauthorizedUrl(String)}</li>
     * </ul>
     *
     * Then at startup, any values specified via these 3 properties will be applied to all configured
     * Filter instances so you don't have to specify them individually on each filter instance.  To ensure your own custom
     * filters benefit from this convenience, your filter implementation should subclass one of the 3 mentioned
     * earlier.
     *
     * @see org.springframework.web.filter.DelegatingFilterProxy DelegatingFilterProxy
     * @since 1.0
     */
    public class ShiroFilterFactoryBean implements FactoryBean, BeanPostProcessor {
    
        private static transient final Logger log = LoggerFactory.getLogger(ShiroFilterFactoryBean.class);
    
        private SecurityManager securityManager;
    
        private Map<String, Filter> filters;
    
        private Map<String, String> filterChainDefinitionMap; //urlPathExpression_to_comma-delimited-filter-chain-definition
    
        private String loginUrl;
        private String successUrl;
        private String unauthorizedUrl;
    
        private AbstractShiroFilter instance;
    
        public ShiroFilterFactoryBean() {
            this.filters = new LinkedHashMap<String, Filter>();
            this.filterChainDefinitionMap = new LinkedHashMap<String, String>(); //order matters!
        }
    
        /**
         * Sets the application {@code SecurityManager} instance to be used by the constructed Shiro Filter.  This is a
         * required property - failure to set it will throw an initialization exception.
         *
         * @return the application {@code SecurityManager} instance to be used by the constructed Shiro Filter.
         */
        public SecurityManager getSecurityManager() {
            return securityManager;
        }
    
        /**
         * Sets the application {@code SecurityManager} instance to be used by the constructed Shiro Filter.  This is a
         * required property - failure to set it will throw an initialization exception.
         *
         * @param securityManager the application {@code SecurityManager} instance to be used by the constructed Shiro Filter.
         */
        public void setSecurityManager(SecurityManager securityManager) {
            this.securityManager = securityManager;
        }
    
        /**
         * Returns the application's login URL to be assigned to all acquired Filters that subclass
         * {@link AccessControlFilter} or {@code null} if no value should be assigned globally. The default value
         * is {@code null}.
         *
         * @return the application's login URL to be assigned to all acquired Filters that subclass
         *         {@link AccessControlFilter} or {@code null} if no value should be assigned globally.
         * @see #setLoginUrl
         */
        public String getLoginUrl() {
            return loginUrl;
        }
    
        /**
         * Sets the application's login URL to be assigned to all acquired Filters that subclass
         * {@link AccessControlFilter}.  This is a convenience mechanism: for all configured {@link #setFilters filters},
         * as well for any default ones ({@code authc}, {@code user}, etc), this value will be passed on to each Filter
         * via the {@link AccessControlFilter#setLoginUrl(String)} method<b>*</b>.  This eliminates the need to
         * configure the 'loginUrl' property manually on each filter instance, and instead that can be configured once
         * via this attribute.
         * <p/>
         * <b>*</b>If a filter already has already been explicitly configured with a value, it will
         * <em>not</em> receive this value. Individual filter configuration overrides this global convenience property.
         *
         * @param loginUrl the application's login URL to apply to as a convenience to all discovered
         *                 {@link AccessControlFilter} instances.
         * @see AccessControlFilter#setLoginUrl(String)
         */
        public void setLoginUrl(String loginUrl) {
            this.loginUrl = loginUrl;
        }
    
        /**
         * Returns the application's after-login success URL to be assigned to all acquired Filters that subclass
         * {@link AuthenticationFilter} or {@code null} if no value should be assigned globally. The default value
         * is {@code null}.
         *
         * @return the application's after-login success URL to be assigned to all acquired Filters that subclass
         *         {@link AuthenticationFilter} or {@code null} if no value should be assigned globally.
         * @see #setSuccessUrl
         */
        public String getSuccessUrl() {
            return successUrl;
        }
    
        /**
         * Sets the application's after-login success URL to be assigned to all acquired Filters that subclass
         * {@link AuthenticationFilter}.  This is a convenience mechanism: for all configured {@link #setFilters filters},
         * as well for any default ones ({@code authc}, {@code user}, etc), this value will be passed on to each Filter
         * via the {@link AuthenticationFilter#setSuccessUrl(String)} method<b>*</b>.  This eliminates the need to
         * configure the 'successUrl' property manually on each filter instance, and instead that can be configured once
         * via this attribute.
         * <p/>
         * <b>*</b>If a filter already has already been explicitly configured with a value, it will
         * <em>not</em> receive this value. Individual filter configuration overrides this global convenience property.
         *
         * @param successUrl the application's after-login success URL to apply to as a convenience to all discovered
         *                   {@link AccessControlFilter} instances.
         * @see AuthenticationFilter#setSuccessUrl(String)
         */
        public void setSuccessUrl(String successUrl) {
            this.successUrl = successUrl;
        }
    
        /**
         * Returns the application's after-login success URL to be assigned to all acquired Filters that subclass
         * {@link AuthenticationFilter} or {@code null} if no value should be assigned globally. The default value
         * is {@code null}.
         *
         * @return the application's after-login success URL to be assigned to all acquired Filters that subclass
         *         {@link AuthenticationFilter} or {@code null} if no value should be assigned globally.
         * @see #setSuccessUrl
         */
        public String getUnauthorizedUrl() {
            return unauthorizedUrl;
        }
    
        /**
         * Sets the application's 'unauthorized' URL to be assigned to all acquired Filters that subclass
         * {@link AuthorizationFilter}.  This is a convenience mechanism: for all configured {@link #setFilters filters},
         * as well for any default ones ({@code roles}, {@code perms}, etc), this value will be passed on to each Filter
         * via the {@link AuthorizationFilter#setUnauthorizedUrl(String)} method<b>*</b>.  This eliminates the need to
         * configure the 'unauthorizedUrl' property manually on each filter instance, and instead that can be configured once
         * via this attribute.
         * <p/>
         * <b>*</b>If a filter already has already been explicitly configured with a value, it will
         * <em>not</em> receive this value. Individual filter configuration overrides this global convenience property.
         *
         * @param unauthorizedUrl the application's 'unauthorized' URL to apply to as a convenience to all discovered
         *                        {@link AuthorizationFilter} instances.
         * @see AuthorizationFilter#setUnauthorizedUrl(String)
         */
        public void setUnauthorizedUrl(String unauthorizedUrl) {
            this.unauthorizedUrl = unauthorizedUrl;
        }
    
        /**
         * Returns the filterName-to-Filter map of filters available for reference when defining filter chain definitions.
         * All filter chain definitions will reference filters by the names in this map (i.e. the keys).
         *
         * @return the filterName-to-Filter map of filters available for reference when defining filter chain definitions.
         */
        public Map<String, Filter> getFilters() {
            return filters;
        }
    
        /**
         * Sets the filterName-to-Filter map of filters available for reference when creating
         * {@link #setFilterChainDefinitionMap(java.util.Map) filter chain definitions}.
         * <p/>
         * <b>Note:</b> This property is optional:  this {@code FactoryBean} implementation will discover all beans in the
         * web application context that implement the {@link Filter} interface and automatically add them to this filter
         * map under their bean name.
         * <p/>
         * For example, just defining this bean in a web Spring XML application context:
         * <pre>
         * &lt;bean id=&quot;myFilter&quot; class=&quot;com.class.that.implements.javax.servlet.Filter&quot;&gt;
         * ...
         * &lt;/bean&gt;</pre>
         * Will automatically place that bean into this Filters map under the key '<b>myFilter</b>'.
         *
         * @param filters the optional filterName-to-Filter map of filters available for reference when creating
         *                {@link #setFilterChainDefinitionMap (java.util.Map) filter chain definitions}.
         */
        public void setFilters(Map<String, Filter> filters) {
            this.filters = filters;
        }
    
        /**
         * Returns the chainName-to-chainDefinition map of chain definitions to use for creating filter chains intercepted
         * by the Shiro Filter.  Each map entry should conform to the format defined by the
         * {@link FilterChainManager#createChain(String, String)} JavaDoc, where the map key is the chain name (e.g. URL
         * path expression) and the map value is the comma-delimited string chain definition.
         *
         * @return he chainName-to-chainDefinition map of chain definitions to use for creating filter chains intercepted
         *         by the Shiro Filter.
         */
        public Map<String, String> getFilterChainDefinitionMap() {
            return filterChainDefinitionMap;
        }
    
        /**
         * Sets the chainName-to-chainDefinition map of chain definitions to use for creating filter chains intercepted
         * by the Shiro Filter.  Each map entry should conform to the format defined by the
         * {@link FilterChainManager#createChain(String, String)} JavaDoc, where the map key is the chain name (e.g. URL
         * path expression) and the map value is the comma-delimited string chain definition.
         *
         * @param filterChainDefinitionMap the chainName-to-chainDefinition map of chain definitions to use for creating
         *                                 filter chains intercepted by the Shiro Filter.
         */
        public void setFilterChainDefinitionMap(Map<String, String> filterChainDefinitionMap) {
            this.filterChainDefinitionMap = filterChainDefinitionMap;
        }
    
        /**
         * A convenience method that sets the {@link #setFilterChainDefinitionMap(java.util.Map) filterChainDefinitionMap}
         * property by accepting a {@link java.util.Properties Properties}-compatible string (multi-line key/value pairs).
         * Each key/value pair must conform to the format defined by the
         * {@link FilterChainManager#createChain(String,String)} JavaDoc - each property key is an ant URL
         * path expression and the value is the comma-delimited chain definition.
         *
         * @param definitions a {@link java.util.Properties Properties}-compatible string (multi-line key/value pairs)
         *                    where each key/value pair represents a single urlPathExpression-commaDelimitedChainDefinition.
         */
        public void setFilterChainDefinitions(String definitions) {
            Ini ini = new Ini();
            ini.load(definitions);
            //did they explicitly state a 'urls' section?  Not necessary, but just in case:
            Ini.Section section = ini.getSection(IniFilterChainResolverFactory.URLS);
            if (CollectionUtils.isEmpty(section)) {
                //no urls section.  Since this _is_ a urls chain definition property, just assume the
                //default section contains only the definitions:
                section = ini.getSection(Ini.DEFAULT_SECTION_NAME);
            }
            setFilterChainDefinitionMap(section);
        }
    
        /**
         * Lazily creates and returns a {@link AbstractShiroFilter} concrete instance via the
         * {@link #createInstance} method.
         *
         * @return the application's Shiro Filter instance used to filter incoming web requests.
         * @throws Exception if there is a problem creating the {@code Filter} instance.
         */
        public Object getObject() throws Exception {
            if (instance == null) {
                instance = createInstance();
            }
            return instance;
        }
    
        /**
         * Returns <code>{@link org.apache.shiro.web.servlet.AbstractShiroFilter}.class</code>
         *
         * @return <code>{@link org.apache.shiro.web.servlet.AbstractShiroFilter}.class</code>
         */
        public Class getObjectType() {
            return SpringShiroFilter.class;
        }
    
        /**
         * Returns {@code true} always.  There is almost always only ever 1 Shiro {@code Filter} per web application.
         *
         * @return {@code true} always.  There is almost always only ever 1 Shiro {@code Filter} per web application.
         */
        public boolean isSingleton() {
            return true;
        }
    
        protected FilterChainManager createFilterChainManager() {
    
            DefaultFilterChainManager manager = new DefaultFilterChainManager();
            Map<String, Filter> defaultFilters = manager.getFilters();
            //apply global settings if necessary:
            for (Filter filter : defaultFilters.values()) {
                applyGlobalPropertiesIfNecessary(filter);
            }
    
            //Apply the acquired and/or configured filters:
            Map<String, Filter> filters = getFilters();
            if (!CollectionUtils.isEmpty(filters)) {
                for (Map.Entry<String, Filter> entry : filters.entrySet()) {
                    String name = entry.getKey();
                    Filter filter = entry.getValue();
                    applyGlobalPropertiesIfNecessary(filter);
                    if (filter instanceof Nameable) {
                        ((Nameable) filter).setName(name);
                    }
                    //'init' argument is false, since Spring-configured filters should be initialized
                    //in Spring (i.e. 'init-method=blah') or implement InitializingBean:
                    manager.addFilter(name, filter, false);
                }
            }
    
            //build up the chains:
            Map<String, String> chains = getFilterChainDefinitionMap();
            if (!CollectionUtils.isEmpty(chains)) {
                for (Map.Entry<String, String> entry : chains.entrySet()) {
                    String url = entry.getKey();
                    String chainDefinition = entry.getValue();
                    manager.createChain(url, chainDefinition);
                }
            }
    
            return manager;
        }
    
        /**
         * This implementation:
         * <ol>
         * <li>Ensures the required {@link #setSecurityManager(org.apache.shiro.mgt.SecurityManager) securityManager}
         * property has been set</li>
         * <li>{@link #createFilterChainManager() Creates} a {@link FilterChainManager} instance that reflects the
         * configured {@link #setFilters(java.util.Map) filters} and
         * {@link #setFilterChainDefinitionMap(java.util.Map) filter chain definitions}</li>
         * <li>Wraps the FilterChainManager with a suitable
         * {@link org.apache.shiro.web.filter.mgt.FilterChainResolver FilterChainResolver} since the Shiro Filter
         * implementations do not know of {@code FilterChainManager}s</li>
         * <li>Sets both the {@code SecurityManager} and {@code FilterChainResolver} instances on a new Shiro Filter
         * instance and returns that filter instance.</li>
         * </ol>
         *
         * @return a new Shiro Filter reflecting any configured filters and filter chain definitions.
         * @throws Exception if there is a problem creating the AbstractShiroFilter instance.
         */
        protected AbstractShiroFilter createInstance() throws Exception {
    
            log.debug("Creating Shiro Filter instance.");
    
            SecurityManager securityManager = getSecurityManager();
            if (securityManager == null) {
                String msg = "SecurityManager property must be set.";
                throw new BeanInitializationException(msg);
            }
    
            if (!(securityManager instanceof WebSecurityManager)) {
                String msg = "The security manager does not implement the WebSecurityManager interface.";
                throw new BeanInitializationException(msg);
            }
    
            FilterChainManager manager = createFilterChainManager();
    
            //Expose the constructed FilterChainManager by first wrapping it in a
            // FilterChainResolver implementation. The AbstractShiroFilter implementations
            // do not know about FilterChainManagers - only resolvers:
            PathMatchingFilterChainResolver chainResolver = new PathMatchingFilterChainResolver();
            chainResolver.setFilterChainManager(manager);
    
            //Now create a concrete ShiroFilter instance and apply the acquired SecurityManager and built
            //FilterChainResolver.  It doesn't matter that the instance is an anonymous inner class
            //here - we're just using it because it is a concrete AbstractShiroFilter instance that accepts
            //injection of the SecurityManager and FilterChainResolver:
            return new SpringShiroFilter((WebSecurityManager) securityManager, chainResolver);
        }
    
        private void applyLoginUrlIfNecessary(Filter filter) {
            String loginUrl = getLoginUrl();
            if (StringUtils.hasText(loginUrl) && (filter instanceof AccessControlFilter)) {
                AccessControlFilter acFilter = (AccessControlFilter) filter;
                //only apply the login url if they haven't explicitly configured one already:
                String existingLoginUrl = acFilter.getLoginUrl();
                if (AccessControlFilter.DEFAULT_LOGIN_URL.equals(existingLoginUrl)) {
                    acFilter.setLoginUrl(loginUrl);
                }
            }
        }
    
        private void applySuccessUrlIfNecessary(Filter filter) {
            String successUrl = getSuccessUrl();
            if (StringUtils.hasText(successUrl) && (filter instanceof AuthenticationFilter)) {
                AuthenticationFilter authcFilter = (AuthenticationFilter) filter;
                //only apply the successUrl if they haven't explicitly configured one already:
                String existingSuccessUrl = authcFilter.getSuccessUrl();
                if (AuthenticationFilter.DEFAULT_SUCCESS_URL.equals(existingSuccessUrl)) {
                    authcFilter.setSuccessUrl(successUrl);
                }
            }
        }
    
        private void applyUnauthorizedUrlIfNecessary(Filter filter) {
            String unauthorizedUrl = getUnauthorizedUrl();
            if (StringUtils.hasText(unauthorizedUrl) && (filter instanceof AuthorizationFilter)) {
                AuthorizationFilter authzFilter = (AuthorizationFilter) filter;
                //only apply the unauthorizedUrl if they haven't explicitly configured one already:
                String existingUnauthorizedUrl = authzFilter.getUnauthorizedUrl();
                if (existingUnauthorizedUrl == null) {
                    authzFilter.setUnauthorizedUrl(unauthorizedUrl);
                }
            }
        }
    
        private void applyGlobalPropertiesIfNecessary(Filter filter) {
            applyLoginUrlIfNecessary(filter);
            applySuccessUrlIfNecessary(filter);
            applyUnauthorizedUrlIfNecessary(filter);
        }
    
        /**
         * Inspects a bean, and if it implements the {@link Filter} interface, automatically adds that filter
         * instance to the internal {@link #setFilters(java.util.Map) filters map} that will be referenced
         * later during filter chain construction.
         */
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof Filter) {
                log.debug("Found filter chain candidate filter '{}'", beanName);
                Filter filter = (Filter) bean;
                applyGlobalPropertiesIfNecessary(filter);
                getFilters().put(beanName, filter);
            } else {
                log.trace("Ignoring non-Filter bean '{}'", beanName);
            }
            return bean;
        }
    
        /**
         * Does nothing - only exists to satisfy the BeanPostProcessor interface and immediately returns the
         * {@code bean} argument.
         */
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            return bean;
        }
    
        /**
         * Ordinarily the {@code AbstractShiroFilter} must be subclassed to additionally perform configuration
         * and initialization behavior.  Because this {@code FactoryBean} implementation manually builds the
         * {@link AbstractShiroFilter}'s
         * {@link AbstractShiroFilter#setSecurityManager(org.apache.shiro.web.mgt.WebSecurityManager) securityManager} and
         * {@link AbstractShiroFilter#setFilterChainResolver(org.apache.shiro.web.filter.mgt.FilterChainResolver) filterChainResolver}
         * properties, the only thing left to do is set those properties explicitly.  We do that in a simple
         * concrete subclass in the constructor.
         */
        private static final class SpringShiroFilter extends AbstractShiroFilter {
    
            protected SpringShiroFilter(WebSecurityManager webSecurityManager, FilterChainResolver resolver) {
                super();
                if (webSecurityManager == null) {
                    throw new IllegalArgumentException("WebSecurityManager property cannot be null.");
                }
                setSecurityManager(webSecurityManager);
                if (resolver != null) {
                    setFilterChainResolver(resolver);
                }
            }
        }
    }


##2.FormAuthenticationFilter

    如果是ajax请求,需要重写跳转页面
    
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
    package org.apache.shiro.web.filter.authc;
    
    import org.apache.shiro.authc.AuthenticationException;
    import org.apache.shiro.authc.AuthenticationToken;
    import org.apache.shiro.authc.UsernamePasswordToken;
    import org.apache.shiro.subject.Subject;
    import org.apache.shiro.web.util.WebUtils;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    
    import javax.servlet.ServletRequest;
    import javax.servlet.ServletResponse;
    import javax.servlet.http.HttpServletRequest;
    
    
    /**
     * Requires the requesting user to be authenticated for the request to continue, and if they are not, forces the user
     * to login via by redirecting them to the {@link #setLoginUrl(String) loginUrl} you configure.
     * <p/>
     * <p>This filter constructs a {@link UsernamePasswordToken UsernamePasswordToken} with the values found in
     * {@link #setUsernameParam(String) username}, {@link #setPasswordParam(String) password},
     * and {@link #setRememberMeParam(String) rememberMe} request parameters.  It then calls
     * {@link org.apache.shiro.subject.Subject#login(org.apache.shiro.authc.AuthenticationToken) Subject.login(usernamePasswordToken)},
     * effectively automatically performing a login attempt.  Note that the login attempt will only occur when the
     * {@link #isLoginSubmission(javax.servlet.ServletRequest, javax.servlet.ServletResponse) isLoginSubmission(request,response)}
     * is <code>true</code>, which by default occurs when the request is for the {@link #setLoginUrl(String) loginUrl} and
     * is a POST request.
     * <p/>
     * <p>If the login attempt fails, the resulting <code>AuthenticationException</code> fully qualified class name will
     * be set as a request attribute under the {@link #setFailureKeyAttribute(String) failureKeyAttribute} key.  This
     * FQCN can be used as an i18n key or lookup mechanism to explain to the user why their login attempt failed
     * (e.g. no account, incorrect password, etc).
     * <p/>
     * <p>If you would prefer to handle the authentication validation and login in your own code, consider using the
     * {@link PassThruAuthenticationFilter} instead, which allows requests to the
     * {@link #loginUrl} to pass through to your application's code directly.
     *
     * @see PassThruAuthenticationFilter
     * @since 0.9
     */
    public class FormAuthenticationFilter extends AuthenticatingFilter {
    
        //TODO - complete JavaDoc
    
        public static final String DEFAULT_ERROR_KEY_ATTRIBUTE_NAME = "shiroLoginFailure";
    
        public static final String DEFAULT_USERNAME_PARAM = "username";
        public static final String DEFAULT_PASSWORD_PARAM = "password";
        public static final String DEFAULT_REMEMBER_ME_PARAM = "rememberMe";
    
        private static final Logger log = LoggerFactory.getLogger(FormAuthenticationFilter.class);
    
        private String usernameParam = DEFAULT_USERNAME_PARAM;
        private String passwordParam = DEFAULT_PASSWORD_PARAM;
        private String rememberMeParam = DEFAULT_REMEMBER_ME_PARAM;
    
        private String failureKeyAttribute = DEFAULT_ERROR_KEY_ATTRIBUTE_NAME;
    
        public FormAuthenticationFilter() {
            setLoginUrl(DEFAULT_LOGIN_URL);
        }
    
        @Override
        public void setLoginUrl(String loginUrl) {
            String previous = getLoginUrl();
            if (previous != null) {
                this.appliedPaths.remove(previous);
            }
            super.setLoginUrl(loginUrl);
            if (log.isTraceEnabled()) {
                log.trace("Adding login url to applied paths.");
            }
            this.appliedPaths.put(getLoginUrl(), null);
        }
    
        public String getUsernameParam() {
            return usernameParam;
        }
    
        /**
         * Sets the request parameter name to look for when acquiring the username.  Unless overridden by calling this
         * method, the default is <code>username</code>.
         *
         * @param usernameParam the name of the request param to check for acquiring the username.
         */
        public void setUsernameParam(String usernameParam) {
            this.usernameParam = usernameParam;
        }
    
        public String getPasswordParam() {
            return passwordParam;
        }
    
        /**
         * Sets the request parameter name to look for when acquiring the password.  Unless overridden by calling this
         * method, the default is <code>password</code>.
         *
         * @param passwordParam the name of the request param to check for acquiring the password.
         */
        public void setPasswordParam(String passwordParam) {
            this.passwordParam = passwordParam;
        }
    
        public String getRememberMeParam() {
            return rememberMeParam;
        }
    
        /**
         * Sets the request parameter name to look for when acquiring the rememberMe boolean value.  Unless overridden
         * by calling this method, the default is <code>rememberMe</code>.
         * <p/>
         * RememberMe will be <code>true</code> if the parameter value equals any of those supported by
         * {@link org.apache.shiro.web.util.WebUtils#isTrue(javax.servlet.ServletRequest, String) WebUtils.isTrue(request,value)}, <code>false</code>
         * otherwise.
         *
         * @param rememberMeParam the name of the request param to check for acquiring the rememberMe boolean value.
         */
        public void setRememberMeParam(String rememberMeParam) {
            this.rememberMeParam = rememberMeParam;
        }
    
        public String getFailureKeyAttribute() {
            return failureKeyAttribute;
        }
    
        public void setFailureKeyAttribute(String failureKeyAttribute) {
            this.failureKeyAttribute = failureKeyAttribute;
        }
    
        protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
            if (isLoginRequest(request, response)) {
                if (isLoginSubmission(request, response)) {
                    if (log.isTraceEnabled()) {
                        log.trace("Login submission detected.  Attempting to execute login.");
                    }
                    return executeLogin(request, response);
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("Login page view.");
                    }
                    //allow them to see the login page ;)
                    return true;
                }
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Attempting to access a path which requires authentication.  Forwarding to the " +
                            "Authentication url [" + getLoginUrl() + "]");
                }
    
                saveRequestAndRedirectToLogin(request, response);
                return false;
            }
        }
    
        /**
         * This default implementation merely returns <code>true</code> if the request is an HTTP <code>POST</code>,
         * <code>false</code> otherwise. Can be overridden by subclasses for custom login submission detection behavior.
         *
         * @param request  the incoming ServletRequest
         * @param response the outgoing ServletResponse.
         * @return <code>true</code> if the request is an HTTP <code>POST</code>, <code>false</code> otherwise.
         */
        @SuppressWarnings({"UnusedDeclaration"})
        protected boolean isLoginSubmission(ServletRequest request, ServletResponse response) {
            return (request instanceof HttpServletRequest) && WebUtils.toHttp(request).getMethod().equalsIgnoreCase(POST_METHOD);
        }
    
        protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
            String username = getUsername(request);
            String password = getPassword(request);
            return createToken(username, password, request, response);
        }
    
        protected boolean isRememberMe(ServletRequest request) {
            return WebUtils.isTrue(request, getRememberMeParam());
        }
    
        protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
                                         ServletRequest request, ServletResponse response) throws Exception {
            issueSuccessRedirect(request, response);
            //we handled the success redirect directly, prevent the chain from continuing:
            return false;
        }
    
        protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e,
                                         ServletRequest request, ServletResponse response) {
            if (log.isDebugEnabled()) {
                log.debug( "Authentication exception", e );
            }
            setFailureAttribute(request, e);
            //login failed, let request continue back to the login page:
            return true;
        }
    
        protected void setFailureAttribute(ServletRequest request, AuthenticationException ae) {
            String className = ae.getClass().getName();
            request.setAttribute(getFailureKeyAttribute(), className);
        }
    
        protected String getUsername(ServletRequest request) {
            return WebUtils.getCleanParam(request, getUsernameParam());
        }
    
        protected String getPassword(ServletRequest request) {
            return WebUtils.getCleanParam(request, getPasswordParam());
        }
    
    
    }

##3.DefaultSecurityManager

    安全管理核心
    3.1 配置CookieRememberMeManager
    3.2 配置CacheManager
    3.3 配置SessionManager等等管理器
    3.4 配置AuthorizingRealm
    
    
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
    package org.apache.shiro.web.mgt;
    
    import org.apache.shiro.mgt.DefaultSecurityManager;
    import org.apache.shiro.mgt.DefaultSubjectDAO;
    import org.apache.shiro.mgt.SessionStorageEvaluator;
    import org.apache.shiro.mgt.SubjectDAO;
    import org.apache.shiro.realm.Realm;
    import org.apache.shiro.session.mgt.SessionContext;
    import org.apache.shiro.session.mgt.SessionKey;
    import org.apache.shiro.session.mgt.SessionManager;
    import org.apache.shiro.subject.Subject;
    import org.apache.shiro.subject.SubjectContext;
    import org.apache.shiro.util.LifecycleUtils;
    import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
    import org.apache.shiro.web.session.mgt.*;
    import org.apache.shiro.web.subject.WebSubject;
    import org.apache.shiro.web.subject.WebSubjectContext;
    import org.apache.shiro.web.subject.support.DefaultWebSubjectContext;
    import org.apache.shiro.web.util.WebUtils;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    
    import javax.servlet.ServletRequest;
    import javax.servlet.ServletResponse;
    import java.io.Serializable;
    import java.util.Collection;
    
    
    /**
     * Default {@link WebSecurityManager WebSecurityManager} implementation used in web-based applications or any
     * application that requires HTTP connectivity (SOAP, http remoting, etc).
     *
     * @since 0.2
     */
    public class DefaultWebSecurityManager extends DefaultSecurityManager implements WebSecurityManager {
    
        //TODO - complete JavaDoc
    
        private static final Logger log = LoggerFactory.getLogger(DefaultWebSecurityManager.class);
    
        @Deprecated
        public static final String HTTP_SESSION_MODE = "http";
        @Deprecated
        public static final String NATIVE_SESSION_MODE = "native";
    
        /**
         * @deprecated as of 1.2.  This should NOT be used for anything other than determining if the sessionMode has changed.
         */
        @Deprecated
        private String sessionMode;
    
        public DefaultWebSecurityManager() {
            super();
            DefaultWebSessionStorageEvaluator webEvalutator = new DefaultWebSessionStorageEvaluator();  
            ((DefaultSubjectDAO) this.subjectDAO).setSessionStorageEvaluator(webEvalutator);
            this.sessionMode = HTTP_SESSION_MODE;
            setSubjectFactory(new DefaultWebSubjectFactory());
            setRememberMeManager(new CookieRememberMeManager());
            setSessionManager(new ServletContainerSessionManager());
            webEvalutator.setSessionManager(getSessionManager());
        }
    
        @SuppressWarnings({"UnusedDeclaration"})
        public DefaultWebSecurityManager(Realm singleRealm) {
            this();
            setRealm(singleRealm);
        }
    
        @SuppressWarnings({"UnusedDeclaration"})
        public DefaultWebSecurityManager(Collection<Realm> realms) {
            this();
            setRealms(realms);
        }
    
        @Override
        protected SubjectContext createSubjectContext() {
            return new DefaultWebSubjectContext();
        }
    
        @Override
        //since 1.2.1 for fixing SHIRO-350
        public void setSubjectDAO(SubjectDAO subjectDAO) {
            super.setSubjectDAO(subjectDAO);
            applySessionManagerToSessionStorageEvaluatorIfPossible();
        }
    
        //since 1.2.1 for fixing SHIRO-350
        @Override
        protected void afterSessionManagerSet() {
            super.afterSessionManagerSet();
            applySessionManagerToSessionStorageEvaluatorIfPossible();
        }
    
        //since 1.2.1 for fixing SHIRO-350:
        private void applySessionManagerToSessionStorageEvaluatorIfPossible() {
            SubjectDAO subjectDAO = getSubjectDAO();
            if (subjectDAO instanceof DefaultSubjectDAO) {
                SessionStorageEvaluator evaluator = ((DefaultSubjectDAO)subjectDAO).getSessionStorageEvaluator();
                if (evaluator instanceof DefaultWebSessionStorageEvaluator) {
                    ((DefaultWebSessionStorageEvaluator)evaluator).setSessionManager(getSessionManager());
                }
            }
        }
    
        @Override
        protected SubjectContext copy(SubjectContext subjectContext) {
            if (subjectContext instanceof WebSubjectContext) {
                return new DefaultWebSubjectContext((WebSubjectContext) subjectContext);
            }
            return super.copy(subjectContext);
        }
    
        @SuppressWarnings({"UnusedDeclaration"})
        @Deprecated
        public String getSessionMode() {
            return sessionMode;
        }
    
        /**
         * @param sessionMode
         * @deprecated since 1.2
         */
        @Deprecated
        public void setSessionMode(String sessionMode) {
            log.warn("The 'sessionMode' property has been deprecated.  Please configure an appropriate WebSessionManager " +
                    "instance instead of using this property.  This property/method will be removed in a later version.");
            String mode = sessionMode;
            if (mode == null) {
                throw new IllegalArgumentException("sessionMode argument cannot be null.");
            }
            mode = sessionMode.toLowerCase();
            if (!HTTP_SESSION_MODE.equals(mode) && !NATIVE_SESSION_MODE.equals(mode)) {
                String msg = "Invalid sessionMode [" + sessionMode + "].  Allowed values are " +
                        "public static final String constants in the " + getClass().getName() + " class: '"
                        + HTTP_SESSION_MODE + "' or '" + NATIVE_SESSION_MODE + "', with '" +
                        HTTP_SESSION_MODE + "' being the default.";
                throw new IllegalArgumentException(msg);
            }
            boolean recreate = this.sessionMode == null || !this.sessionMode.equals(mode);
            this.sessionMode = mode;
            if (recreate) {
                LifecycleUtils.destroy(getSessionManager());
                SessionManager sessionManager = createSessionManager(mode);
                this.setInternalSessionManager(sessionManager);
            }
        }
    
        @Override
        public void setSessionManager(SessionManager sessionManager) {
            this.sessionMode = null;
            if (sessionManager != null && !(sessionManager instanceof WebSessionManager)) {
                if (log.isWarnEnabled()) {
                    String msg = "The " + getClass().getName() + " implementation expects SessionManager instances " +
                            "that implement the " + WebSessionManager.class.getName() + " interface.  The " +
                            "configured instance is of type [" + sessionManager.getClass().getName() + "] which does not " +
                            "implement this interface..  This may cause unexpected behavior.";
                    log.warn(msg);
                }
            }
            setInternalSessionManager(sessionManager);
        }
    
        /**
         * @param sessionManager
         * @since 1.2
         */
        private void setInternalSessionManager(SessionManager sessionManager) {
            super.setSessionManager(sessionManager);
        }
    
        /**
         * @since 1.0
         */
        public boolean isHttpSessionMode() {
            SessionManager sessionManager = getSessionManager();
            return sessionManager instanceof WebSessionManager && ((WebSessionManager)sessionManager).isServletContainerSessions();
        }
    
        protected SessionManager createSessionManager(String sessionMode) {
            if (sessionMode == null || !sessionMode.equalsIgnoreCase(NATIVE_SESSION_MODE)) {
                log.info("{} mode - enabling ServletContainerSessionManager (HTTP-only Sessions)", HTTP_SESSION_MODE);
                return new ServletContainerSessionManager();
            } else {
                log.info("{} mode - enabling DefaultWebSessionManager (non-HTTP and HTTP Sessions)", NATIVE_SESSION_MODE);
                return new DefaultWebSessionManager();
            }
        }
    
        @Override
        protected SessionContext createSessionContext(SubjectContext subjectContext) {
            SessionContext sessionContext = super.createSessionContext(subjectContext);
            if (subjectContext instanceof WebSubjectContext) {
                WebSubjectContext wsc = (WebSubjectContext) subjectContext;
                ServletRequest request = wsc.resolveServletRequest();
                ServletResponse response = wsc.resolveServletResponse();
                DefaultWebSessionContext webSessionContext = new DefaultWebSessionContext(sessionContext);
                if (request != null) {
                    webSessionContext.setServletRequest(request);
                }
                if (response != null) {
                    webSessionContext.setServletResponse(response);
                }
    
                sessionContext = webSessionContext;
            }
            return sessionContext;
        }
    
        @Override
        protected SessionKey getSessionKey(SubjectContext context) {
            if (WebUtils.isWeb(context)) {
                Serializable sessionId = context.getSessionId();
                ServletRequest request = WebUtils.getRequest(context);
                ServletResponse response = WebUtils.getResponse(context);
                return new WebSessionKey(sessionId, request, response);
            } else {
                return super.getSessionKey(context);
    
            }
        }
    
        @Override
        protected void beforeLogout(Subject subject) {
            super.beforeLogout(subject);
            removeRequestIdentity(subject);
        }
    
        protected void removeRequestIdentity(Subject subject) {
            if (subject instanceof WebSubject) {
                WebSubject webSubject = (WebSubject) subject;
                ServletRequest request = webSubject.getServletRequest();
                if (request != null) {
                    request.setAttribute(ShiroHttpServletRequest.IDENTITY_REMOVED_KEY, Boolean.TRUE);
                }
            }
        }
    }

##4.AuthorizingRealm

    一边继承该类,覆盖认证和授权方法

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
    package org.apache.shiro.realm;
    
    import org.apache.shiro.authc.credential.CredentialsMatcher;
    import org.apache.shiro.authz.*;
    import org.apache.shiro.authz.permission.*;
    import org.apache.shiro.cache.Cache;
    import org.apache.shiro.cache.CacheManager;
    import org.apache.shiro.subject.PrincipalCollection;
    import org.apache.shiro.util.CollectionUtils;
    import org.apache.shiro.util.Initializable;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    
    import java.util.*;
    import java.util.concurrent.atomic.AtomicInteger;
    
    
    /**
     * An {@code AuthorizingRealm} extends the {@code AuthenticatingRealm}'s capabilities by adding Authorization
     * (access control) support.
     * <p/>
     * This implementation will perform all role and permission checks automatically (and subclasses do not have to
     * write this logic) as long as the
     * {@link #getAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)} method returns an
     * {@link AuthorizationInfo}.  Please see that method's JavaDoc for an in-depth explanation.
     * <p/>
     * If you find that you do not want to utilize the {@link AuthorizationInfo AuthorizationInfo} construct,
     * you are of course free to subclass the {@link AuthenticatingRealm AuthenticatingRealm} directly instead and
     * implement the remaining Realm interface methods directly.  You might do this if you want have better control
     * over how the Role and Permission checks occur for your specific data source.  However, using AuthorizationInfo
     * (and its default implementation {@link org.apache.shiro.authz.SimpleAuthorizationInfo SimpleAuthorizationInfo}) is sufficient in the large
     * majority of Realm cases.
     *
     * @see org.apache.shiro.authz.SimpleAuthorizationInfo
     * @since 0.2
     */
    public abstract class AuthorizingRealm extends AuthenticatingRealm
            implements Authorizer, Initializable, PermissionResolverAware, RolePermissionResolverAware {
    
        //TODO - complete JavaDoc
    
        /*-------------------------------------------
        |             C O N S T A N T S             |
        ============================================*/
        private static final Logger log = LoggerFactory.getLogger(AuthorizingRealm.class);
    
        /**
         * The default suffix appended to the realm name for caching AuthorizationInfo instances.
         */
        private static final String DEFAULT_AUTHORIZATION_CACHE_SUFFIX = ".authorizationCache";
    
        private static final AtomicInteger INSTANCE_COUNT = new AtomicInteger();
    
        /*-------------------------------------------
        |    I N S T A N C E   V A R I A B L E S    |
        ============================================*/
        /**
         * The cache used by this realm to store AuthorizationInfo instances associated with individual Subject principals.
         */
        private boolean authorizationCachingEnabled;
        private Cache<Object, AuthorizationInfo> authorizationCache;
        private String authorizationCacheName;
    
        private PermissionResolver permissionResolver;
    
        private RolePermissionResolver permissionRoleResolver;
    
        /*-------------------------------------------
        |         C O N S T R U C T O R S           |
        ============================================*/
    
        public AuthorizingRealm() {
            this(null, null);
        }
    
        public AuthorizingRealm(CacheManager cacheManager) {
            this(cacheManager, null);
        }
    
        public AuthorizingRealm(CredentialsMatcher matcher) {
            this(null, matcher);
        }
    
        public AuthorizingRealm(CacheManager cacheManager, CredentialsMatcher matcher) {
            super();
            if (cacheManager != null) setCacheManager(cacheManager);
            if (matcher != null) setCredentialsMatcher(matcher);
    
            this.authorizationCachingEnabled = true;
            this.permissionResolver = new WildcardPermissionResolver();
    
            int instanceNumber = INSTANCE_COUNT.getAndIncrement();
            this.authorizationCacheName = getClass().getName() + DEFAULT_AUTHORIZATION_CACHE_SUFFIX;
            if (instanceNumber > 0) {
                this.authorizationCacheName = this.authorizationCacheName + "." + instanceNumber;
            }
        }
    
        /*-------------------------------------------
        |  A C C E S S O R S / M O D I F I E R S    |
        ============================================*/
    
        public void setName(String name) {
            super.setName(name);
            String authzCacheName = this.authorizationCacheName;
            if (authzCacheName != null && authzCacheName.startsWith(getClass().getName())) {
                //get rid of the default class-name based cache name.  Create a more meaningful one
                //based on the application-unique Realm name:
                this.authorizationCacheName = name + DEFAULT_AUTHORIZATION_CACHE_SUFFIX;
            }
        }
    
        public void setAuthorizationCache(Cache<Object, AuthorizationInfo> authorizationCache) {
            this.authorizationCache = authorizationCache;
        }
    
        public Cache<Object, AuthorizationInfo> getAuthorizationCache() {
            return this.authorizationCache;
        }
    
        public String getAuthorizationCacheName() {
            return authorizationCacheName;
        }
    
        @SuppressWarnings({"UnusedDeclaration"})
        public void setAuthorizationCacheName(String authorizationCacheName) {
            this.authorizationCacheName = authorizationCacheName;
        }
    
        /**
         * Returns {@code true} if authorization caching should be utilized if a {@link CacheManager} has been
         * {@link #setCacheManager(org.apache.shiro.cache.CacheManager) configured}, {@code false} otherwise.
         * <p/>
         * The default value is {@code true}.
         *
         * @return {@code true} if authorization caching should be utilized, {@code false} otherwise.
         */
        public boolean isAuthorizationCachingEnabled() {
            return isCachingEnabled() && authorizationCachingEnabled;
        }
    
        /**
         * Sets whether or not authorization caching should be utilized if a {@link CacheManager} has been
         * {@link #setCacheManager(org.apache.shiro.cache.CacheManager) configured}, {@code false} otherwise.
         * <p/>
         * The default value is {@code true}.
         *
         * @param authenticationCachingEnabled the value to set
         */
        @SuppressWarnings({"UnusedDeclaration"})
        public void setAuthorizationCachingEnabled(boolean authenticationCachingEnabled) {
            this.authorizationCachingEnabled = authenticationCachingEnabled;
            if (authenticationCachingEnabled) {
                setCachingEnabled(true);
            }
        }
    
        public PermissionResolver getPermissionResolver() {
            return permissionResolver;
        }
    
        public void setPermissionResolver(PermissionResolver permissionResolver) {
            if (permissionResolver == null) throw new IllegalArgumentException("Null PermissionResolver is not allowed");
            this.permissionResolver = permissionResolver;
        }
    
        public RolePermissionResolver getRolePermissionResolver() {
            return permissionRoleResolver;
        }
    
        public void setRolePermissionResolver(RolePermissionResolver permissionRoleResolver) {
            this.permissionRoleResolver = permissionRoleResolver;
        }
    
        /*--------------------------------------------
        |               M E T H O D S               |
        ============================================*/
    
        /**
         * Initializes this realm and potentially enables a cache, depending on configuration.
         * <p/>
         * When this method is called, the following logic is executed:
         * <ol>
         * <li>If the {@link #setAuthorizationCache cache} property has been set, it will be
         * used to cache the AuthorizationInfo objects returned from {@link #getAuthorizationInfo}
         * method invocations.
         * All future calls to {@code getAuthorizationInfo} will attempt to use this cache first
         * to alleviate any potentially unnecessary calls to an underlying data store.</li>
         * <li>If the {@link #setAuthorizationCache cache} property has <b>not</b> been set,
         * the {@link #setCacheManager cacheManager} property will be checked.
         * If a {@code cacheManager} has been set, it will be used to create an authorization
         * {@code cache}, and this newly created cache which will be used as specified in #1.</li>
         * <li>If neither the {@link #setAuthorizationCache (org.apache.shiro.cache.Cache) cache}
         * or {@link #setCacheManager(org.apache.shiro.cache.CacheManager) cacheManager}
         * properties are set, caching will be disabled and authorization look-ups will be delegated to
         * subclass implementations for each authorization check.</li>
         * </ol>
         */
        protected void onInit() {
            super.onInit();
            //trigger obtaining the authorization cache if possible
            getAvailableAuthorizationCache();
        }
    
        protected void afterCacheManagerSet() {
            super.afterCacheManagerSet();
            //trigger obtaining the authorization cache if possible
            getAvailableAuthorizationCache();
        }
    
        private Cache<Object, AuthorizationInfo> getAuthorizationCacheLazy() {
    
            if (this.authorizationCache == null) {
    
                if (log.isDebugEnabled()) {
                    log.debug("No authorizationCache instance set.  Checking for a cacheManager...");
                }
    
                CacheManager cacheManager = getCacheManager();
    
                if (cacheManager != null) {
                    String cacheName = getAuthorizationCacheName();
                    if (log.isDebugEnabled()) {
                        log.debug("CacheManager [" + cacheManager + "] has been configured.  Building " +
                                "authorization cache named [" + cacheName + "]");
                    }
                    this.authorizationCache = cacheManager.getCache(cacheName);
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("No cache or cacheManager properties have been set.  Authorization cache cannot " +
                                "be obtained.");
                    }
                }
            }
    
            return this.authorizationCache;
        }
    
        private Cache<Object, AuthorizationInfo> getAvailableAuthorizationCache() {
            Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
            if (cache == null && isAuthorizationCachingEnabled()) {
                cache = getAuthorizationCacheLazy();
            }
            return cache;
        }
    
        /**
         * Returns an account's authorization-specific information for the specified {@code principals},
         * or {@code null} if no account could be found.  The resulting {@code AuthorizationInfo} object is used
         * by the other method implementations in this class to automatically perform access control checks for the
         * corresponding {@code Subject}.
         * <p/>
         * This implementation obtains the actual {@code AuthorizationInfo} object from the subclass's
         * implementation of
         * {@link #doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection) doGetAuthorizationInfo}, and then
         * caches it for efficient reuse if caching is enabled (see below).
         * <p/>
         * Invocations of this method should be thought of as completely orthogonal to acquiring
         * {@link #getAuthenticationInfo(org.apache.shiro.authc.AuthenticationToken) authenticationInfo}, since either could
         * occur in any order.
         * <p/>
         * For example, in &quot;Remember Me&quot; scenarios, the user identity is remembered (and
         * assumed) for their current session and an authentication attempt during that session might never occur.
         * But because their identity would be remembered, that is sufficient enough information to call this method to
         * execute any necessary authorization checks.  For this reason, authentication and authorization should be
         * loosely coupled and not depend on each other.
         * <h3>Caching</h3>
         * The {@code AuthorizationInfo} values returned from this method are cached for efficient reuse
         * if caching is enabled.  Caching is enabled automatically when an {@link #setAuthorizationCache authorizationCache}
         * instance has been explicitly configured, or if a {@link #setCacheManager cacheManager} has been configured, which
         * will be used to lazily create the {@code authorizationCache} as needed.
         * <p/>
         * If caching is enabled, the authorization cache will be checked first and if found, will return the cached
         * {@code AuthorizationInfo} immediately.  If caching is disabled, or there is a cache miss, the authorization
         * info will be looked up from the underlying data store via the
         * {@link #doGetAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)} method, which must be implemented
         * by subclasses.
         * <h4>Changed Data</h4>
         * If caching is enabled and if any authorization data for an account is changed at
         * runtime, such as adding or removing roles and/or permissions, the subclass implementation should clear the
         * cached AuthorizationInfo for that account via the
         * {@link #clearCachedAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection) clearCachedAuthorizationInfo}
         * method.  This ensures that the next call to {@code getAuthorizationInfo(PrincipalCollection)} will
         * acquire the account's fresh authorization data, where it will then be cached for efficient reuse.  This
         * ensures that stale authorization data will not be reused.
         *
         * @param principals the corresponding Subject's identifying principals with which to look up the Subject's
         *                   {@code AuthorizationInfo}.
         * @return the authorization information for the account associated with the specified {@code principals},
         *         or {@code null} if no account could be found.
         */
        protected AuthorizationInfo getAuthorizationInfo(PrincipalCollection principals) {
    
            if (principals == null) {
                return null;
            }
    
            AuthorizationInfo info = null;
    
            if (log.isTraceEnabled()) {
                log.trace("Retrieving AuthorizationInfo for principals [" + principals + "]");
            }
    
            Cache<Object, AuthorizationInfo> cache = getAvailableAuthorizationCache();
            if (cache != null) {
                if (log.isTraceEnabled()) {
                    log.trace("Attempting to retrieve the AuthorizationInfo from cache.");
                }
                Object key = getAuthorizationCacheKey(principals);
                info = cache.get(key);
                if (log.isTraceEnabled()) {
                    if (info == null) {
                        log.trace("No AuthorizationInfo found in cache for principals [" + principals + "]");
                    } else {
                        log.trace("AuthorizationInfo found in cache for principals [" + principals + "]");
                    }
                }
            }
    
    
            if (info == null) {
                // Call template method if the info was not found in a cache
                info = doGetAuthorizationInfo(principals);
                // If the info is not null and the cache has been created, then cache the authorization info.
                if (info != null && cache != null) {
                    if (log.isTraceEnabled()) {
                        log.trace("Caching authorization info for principals: [" + principals + "].");
                    }
                    Object key = getAuthorizationCacheKey(principals);
                    cache.put(key, info);
                }
            }
    
            return info;
        }
    
        protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
            return principals;
        }
    
        /**
         * Clears out the AuthorizationInfo cache entry for the specified account.
         * <p/>
         * This method is provided as a convenience to subclasses so they can invalidate a cache entry when they
         * change an account's authorization data (add/remove roles or permissions) during runtime.  Because an account's
         * AuthorizationInfo can be cached, there needs to be a way to invalidate the cache for only that account so that
         * subsequent authorization operations don't used the (old) cached value if account data changes.
         * <p/>
         * After this method is called, the next authorization check for that same account will result in a call to
         * {@link #getAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection) getAuthorizationInfo}, and the
         * resulting return value will be cached before being returned so it can be reused for later authorization checks.
         * <p/>
         * If you wish to clear out all associated cached data (and not just authorization data), use the
         * {@link #clearCache(org.apache.shiro.subject.PrincipalCollection)} method instead (which will in turn call this
         * method by default).
         *
         * @param principals the principals of the account for which to clear the cached AuthorizationInfo.
         */
        protected void clearCachedAuthorizationInfo(PrincipalCollection principals) {
            if (principals == null) {
                return;
            }
    
            Cache<Object, AuthorizationInfo> cache = getAvailableAuthorizationCache();
            //cache instance will be non-null if caching is enabled:
            if (cache != null) {
                Object key = getAuthorizationCacheKey(principals);
                cache.remove(key);
            }
        }
    
        /**
         * Retrieves the AuthorizationInfo for the given principals from the underlying data store.  When returning
         * an instance from this method, you might want to consider using an instance of
         * {@link org.apache.shiro.authz.SimpleAuthorizationInfo SimpleAuthorizationInfo}, as it is suitable in most cases.
         *
         * @param principals the primary identifying principals of the AuthorizationInfo that should be retrieved.
         * @return the AuthorizationInfo associated with this principals.
         * @see org.apache.shiro.authz.SimpleAuthorizationInfo
         */
        protected abstract AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals);
    
        //visibility changed from private to protected per SHIRO-332
        protected Collection<Permission> getPermissions(AuthorizationInfo info) {
            Set<Permission> permissions = new HashSet<Permission>();
    
            if (info != null) {
                Collection<Permission> perms = info.getObjectPermissions();
                if (!CollectionUtils.isEmpty(perms)) {
                    permissions.addAll(perms);
                }
                perms = resolvePermissions(info.getStringPermissions());
                if (!CollectionUtils.isEmpty(perms)) {
                    permissions.addAll(perms);
                }
    
                perms = resolveRolePermissions(info.getRoles());
                if (!CollectionUtils.isEmpty(perms)) {
                    permissions.addAll(perms);
                }
            }
    
            if (permissions.isEmpty()) {
                return Collections.emptySet();
            } else {
                return Collections.unmodifiableSet(permissions);
            }
        }
    
        private Collection<Permission> resolvePermissions(Collection<String> stringPerms) {
            Collection<Permission> perms = Collections.emptySet();
            PermissionResolver resolver = getPermissionResolver();
            if (resolver != null && !CollectionUtils.isEmpty(stringPerms)) {
                perms = new LinkedHashSet<Permission>(stringPerms.size());
                for (String strPermission : stringPerms) {
                    Permission permission = resolver.resolvePermission(strPermission);
                    perms.add(permission);
                }
            }
            return perms;
        }
    
        private Collection<Permission> resolveRolePermissions(Collection<String> roleNames) {
            Collection<Permission> perms = Collections.emptySet();
            RolePermissionResolver resolver = getRolePermissionResolver();
            if (resolver != null && !CollectionUtils.isEmpty(roleNames)) {
                perms = new LinkedHashSet<Permission>(roleNames.size());
                for (String roleName : roleNames) {
                    Collection<Permission> resolved = resolver.resolvePermissionsInRole(roleName);
                    if (!CollectionUtils.isEmpty(resolved)) {
                        perms.addAll(resolved);
                    }
                }
            }
            return perms;
        }
    
        public boolean isPermitted(PrincipalCollection principals, String permission) {
            Permission p = getPermissionResolver().resolvePermission(permission);
            return isPermitted(principals, p);
        }
    
        public boolean isPermitted(PrincipalCollection principals, Permission permission) {
            AuthorizationInfo info = getAuthorizationInfo(principals);
            return isPermitted(permission, info);
        }
    
        //visibility changed from private to protected per SHIRO-332
        protected boolean isPermitted(Permission permission, AuthorizationInfo info) {
            Collection<Permission> perms = getPermissions(info);
            if (perms != null && !perms.isEmpty()) {
                for (Permission perm : perms) {
                    if (perm.implies(permission)) {
                        return true;
                    }
                }
            }
            return false;
        }
    
        public boolean[] isPermitted(PrincipalCollection subjectIdentifier, String... permissions) {
            List<Permission> perms = new ArrayList<Permission>(permissions.length);
            for (String permString : permissions) {
                perms.add(getPermissionResolver().resolvePermission(permString));
            }
            return isPermitted(subjectIdentifier, perms);
        }
    
        public boolean[] isPermitted(PrincipalCollection principals, List<Permission> permissions) {
            AuthorizationInfo info = getAuthorizationInfo(principals);
            return isPermitted(permissions, info);
        }
    
        protected boolean[] isPermitted(List<Permission> permissions, AuthorizationInfo info) {
            boolean[] result;
            if (permissions != null && !permissions.isEmpty()) {
                int size = permissions.size();
                result = new boolean[size];
                int i = 0;
                for (Permission p : permissions) {
                    result[i++] = isPermitted(p, info);
                }
            } else {
                result = new boolean[0];
            }
            return result;
        }
    
        public boolean isPermittedAll(PrincipalCollection subjectIdentifier, String... permissions) {
            if (permissions != null && permissions.length > 0) {
                Collection<Permission> perms = new ArrayList<Permission>(permissions.length);
                for (String permString : permissions) {
                    perms.add(getPermissionResolver().resolvePermission(permString));
                }
                return isPermittedAll(subjectIdentifier, perms);
            }
            return false;
        }
    
        public boolean isPermittedAll(PrincipalCollection principal, Collection<Permission> permissions) {
            AuthorizationInfo info = getAuthorizationInfo(principal);
            return info != null && isPermittedAll(permissions, info);
        }
    
        protected boolean isPermittedAll(Collection<Permission> permissions, AuthorizationInfo info) {
            if (permissions != null && !permissions.isEmpty()) {
                for (Permission p : permissions) {
                    if (!isPermitted(p, info)) {
                        return false;
                    }
                }
            }
            return true;
        }
    
        public void checkPermission(PrincipalCollection subjectIdentifier, String permission) throws AuthorizationException {
            Permission p = getPermissionResolver().resolvePermission(permission);
            checkPermission(subjectIdentifier, p);
        }
    
        public void checkPermission(PrincipalCollection principal, Permission permission) throws AuthorizationException {
            AuthorizationInfo info = getAuthorizationInfo(principal);
            checkPermission(permission, info);
        }
    
        protected void checkPermission(Permission permission, AuthorizationInfo info) {
            if (!isPermitted(permission, info)) {
                String msg = "User is not permitted [" + permission + "]";
                throw new UnauthorizedException(msg);
            }
        }
    
        public void checkPermissions(PrincipalCollection subjectIdentifier, String... permissions) throws AuthorizationException {
            if (permissions != null) {
                for (String permString : permissions) {
                    checkPermission(subjectIdentifier, permString);
                }
            }
        }
    
        public void checkPermissions(PrincipalCollection principal, Collection<Permission> permissions) throws AuthorizationException {
            AuthorizationInfo info = getAuthorizationInfo(principal);
            checkPermissions(permissions, info);
        }
    
        protected void checkPermissions(Collection<Permission> permissions, AuthorizationInfo info) {
            if (permissions != null && !permissions.isEmpty()) {
                for (Permission p : permissions) {
                    checkPermission(p, info);
                }
            }
        }
    
        public boolean hasRole(PrincipalCollection principal, String roleIdentifier) {
            AuthorizationInfo info = getAuthorizationInfo(principal);
            return hasRole(roleIdentifier, info);
        }
    
        protected boolean hasRole(String roleIdentifier, AuthorizationInfo info) {
            return info != null && info.getRoles() != null && info.getRoles().contains(roleIdentifier);
        }
    
        public boolean[] hasRoles(PrincipalCollection principal, List<String> roleIdentifiers) {
            AuthorizationInfo info = getAuthorizationInfo(principal);
            boolean[] result = new boolean[roleIdentifiers != null ? roleIdentifiers.size() : 0];
            if (info != null) {
                result = hasRoles(roleIdentifiers, info);
            }
            return result;
        }
    
        protected boolean[] hasRoles(List<String> roleIdentifiers, AuthorizationInfo info) {
            boolean[] result;
            if (roleIdentifiers != null && !roleIdentifiers.isEmpty()) {
                int size = roleIdentifiers.size();
                result = new boolean[size];
                int i = 0;
                for (String roleName : roleIdentifiers) {
                    result[i++] = hasRole(roleName, info);
                }
            } else {
                result = new boolean[0];
            }
            return result;
        }
    
        public boolean hasAllRoles(PrincipalCollection principal, Collection<String> roleIdentifiers) {
            AuthorizationInfo info = getAuthorizationInfo(principal);
            return info != null && hasAllRoles(roleIdentifiers, info);
        }
    
        private boolean hasAllRoles(Collection<String> roleIdentifiers, AuthorizationInfo info) {
            if (roleIdentifiers != null && !roleIdentifiers.isEmpty()) {
                for (String roleName : roleIdentifiers) {
                    if (!hasRole(roleName, info)) {
                        return false;
                    }
                }
            }
            return true;
        }
    
        public void checkRole(PrincipalCollection principal, String role) throws AuthorizationException {
            AuthorizationInfo info = getAuthorizationInfo(principal);
            checkRole(role, info);
        }
    
        protected void checkRole(String role, AuthorizationInfo info) {
            if (!hasRole(role, info)) {
                String msg = "User does not have role [" + role + "]";
                throw new UnauthorizedException(msg);
            }
        }
    
        public void checkRoles(PrincipalCollection principal, Collection<String> roles) throws AuthorizationException {
            AuthorizationInfo info = getAuthorizationInfo(principal);
            checkRoles(roles, info);
        }
    
        public void checkRoles(PrincipalCollection principal, String... roles) throws AuthorizationException {
            checkRoles(principal, Arrays.asList(roles));
        }
    
        protected void checkRoles(Collection<String> roles, AuthorizationInfo info) {
            if (roles != null && !roles.isEmpty()) {
                for (String roleName : roles) {
                    checkRole(roleName, info);
                }
            }
        }
    
        /**
         * Calls {@code super.doClearCache} to ensure any cached authentication data is removed and then calls
         * {@link #clearCachedAuthorizationInfo(org.apache.shiro.subject.PrincipalCollection)} to remove any cached
         * authorization data.
         * <p/>
         * If overriding in a subclass, be sure to call {@code super.doClearCache} to ensure this behavior is maintained.
         *
         * @param principals the principals of the account for which to clear any cached AuthorizationInfo
         * @since 1.2
         */
        @Override
        protected void doClearCache(PrincipalCollection principals) {
            super.doClearCache(principals);
            clearCachedAuthorizationInfo(principals);
        }
    }
