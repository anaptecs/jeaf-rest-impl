/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.rest.executor.impl.apache.jeaf;

import java.util.List;

import com.anaptecs.jeaf.rest.executor.impl.config.RESTClientConfiguration;
import com.anaptecs.jeaf.rest.executor.impl.config.impl.ApacheHttpClientConfiguration;
import com.anaptecs.jeaf.rest.executor.impl.config.impl.RESTClientConfigurationImpl;
import com.anaptecs.jeaf.rest.executor.impl.config.impl.CircuitBreakerConfiguration;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.config.Configuration;

public class PropertiesBasedRESTClientConfigurationLoader {
  public static RESTClientConfiguration loadConfiguration( String pResource ) {
    // Load configuration.
    Configuration lResourceConfig = XFun.getConfigurationProvider().getResourceConfiguration(pResource);

    // Let's first create default configuration with all default values.
    RESTClientConfigurationImpl lRESTClientConfig = new RESTClientConfigurationImpl();

    // Set external service URL
    String lExternalServiceURL = lResourceConfig.getConfigurationValue("externalServiceURL", String.class);
    lRESTClientConfig.setExternalServiceURL(lExternalServiceURL);

    // Set cookie domain
    String lCookieDomain = lResourceConfig.getConfigurationValue("cookieDomain", lRESTClientConfig
        .getCookieDomain(), String.class);
    lRESTClientConfig.setCookieDomain(lCookieDomain);

    // Set cookie path
    String lCookiePath = lResourceConfig.getConfigurationValue("cookiePath", lRESTClientConfig
        .getCookiePath(), String.class);
    lRESTClientConfig.setCookiePath(lCookiePath);

    // Set sensitive headers.
    List<String> lSensitiveHeaders = lResourceConfig.getConfigurationValueList("sensitiveHeaders",
        lRESTClientConfig.getSensitiveHeaders(),
        String.class);
    lRESTClientConfig.setSensitiveHeaders(lSensitiveHeaders);

    // Set trace requests
    boolean lTraceRequests = lResourceConfig.getConfigurationValue("traceRequests", lRESTClientConfig
        .traceRequests(),
        Boolean.class);
    lRESTClientConfig.setTraceRequests(lTraceRequests);

    // Set trace responses
    boolean lTraceResponses = lResourceConfig.getConfigurationValue("traceResponses", lRESTClientConfig
        .traceResponses(), Boolean.class);
    lRESTClientConfig.setTraceResponses(lTraceResponses);

    // Create http client configuration
    ApacheHttpClientConfiguration lHttpClientConfig = new ApacheHttpClientConfiguration();

    // Set max pool size
    int lMaxPoolSize = lResourceConfig.getConfigurationValue("maxPoolSize", lHttpClientConfig.getMaxPoolSize(),
        Integer.class);
    lHttpClientConfig.setMaxPoolSize(lMaxPoolSize);

    // Set max idle connections
    int lMaxIdleConnections = lResourceConfig.getConfigurationValue("maxIdleConnections", lHttpClientConfig
        .getMaxIdleConnections(), Integer.class);
    lHttpClientConfig.setMaxIdleConnections(lMaxIdleConnections);

    // Set keep alive duration
    int lKeepAliveDuration = lResourceConfig.getConfigurationValue("keepAliveDuration", lHttpClientConfig
        .getKeepAliveDuration(), Integer.class);
    lHttpClientConfig.setKeepAliveDuration(lKeepAliveDuration);

    // Set inactivity configuration
    int lValidateAfterInactivityDuration = lResourceConfig.getConfigurationValue("validateAfterInactivityDuration",
        lHttpClientConfig.getValidateAfterInactivityDuration(), Integer.class);
    lHttpClientConfig.setValidateAfterInactivityDuration(lValidateAfterInactivityDuration);

    // Set max retries
    int lMaxRetries = lResourceConfig.getConfigurationValue("maxRetries", lHttpClientConfig.getMaxRetries(),
        Integer.class);
    lHttpClientConfig.setMaxRetries(lMaxRetries);

    // Set retry interval
    int lRetryInterval = lResourceConfig.getConfigurationValue("retryInterval", lHttpClientConfig.getRetryInterval(),
        Integer.class);
    lHttpClientConfig.setRetryInterval(lRetryInterval);

    // Set response timeout
    int lResponseTimeout = lResourceConfig.getConfigurationValue("responseTimeout", lHttpClientConfig
        .getResponseTimeout(), Integer.class);
    lHttpClientConfig.setResponseTimeout(lResponseTimeout);

    // Set connect timeout
    int lConnectTimeout = lResourceConfig.getConfigurationValue("connectTimeout", lHttpClientConfig.getConnectTimeout(),
        Integer.class);
    lHttpClientConfig.setConnectTimeout(lConnectTimeout);

    // Set connection request timeout
    int lConnectionRequestTimeout = lResourceConfig.getConfigurationValue("connectionRequestTimeout", lHttpClientConfig
        .getConnectionRequestTimeout(), Integer.class);
    lHttpClientConfig.setConnectionRequestTimeout(lConnectionRequestTimeout);

    lRESTClientConfig.setHttpClientConfiguration(lHttpClientConfig);

    // Create Resilience4J configuration
    CircuitBreakerConfiguration lCircuitBreakerConfig = new CircuitBreakerConfiguration();

    // Set failure rate threshold
    int lFailureRateThreshold = lResourceConfig.getConfigurationValue("failureRateThreshold", lCircuitBreakerConfig
        .getFailureRateThreshold(), Integer.class);
    lCircuitBreakerConfig.setFailureRateThreshold(lFailureRateThreshold);

    // Set duration in open state
    int lDurationInOpenState = lResourceConfig.getConfigurationValue("durationInOpenState", lCircuitBreakerConfig
        .getDurationInOpenState(), Integer.class);
    lCircuitBreakerConfig.setDurationInOpenState(lDurationInOpenState);

    // Set slow request duration
    int lSlowRequestDuration = lResourceConfig.getConfigurationValue("slowRequestDuration", lCircuitBreakerConfig
        .getSlowRequestDuration(), Integer.class);
    lCircuitBreakerConfig.setSlowRequestDuration(lSlowRequestDuration);

    // Set slow request rate threshold
    int lSlowRequestRateThreshold = lResourceConfig.getConfigurationValue("slowRequestRateThreshold",
        lCircuitBreakerConfig.getSlowRequestRateThreshold(), Integer.class);
    lCircuitBreakerConfig.setSlowRequestRateThreshold(lSlowRequestRateThreshold);

    // Set permitted calls in open state
    int lPermittedCallsInHalfOpenState = lResourceConfig.getConfigurationValue("permittedCallsInHalfOpenState",
        lCircuitBreakerConfig.getPermittedCallsInHalfOpenState(), Integer.class);
    lCircuitBreakerConfig.setPermittedCallsInHalfOpenState(lPermittedCallsInHalfOpenState);

    // Set sliding window size
    int lSlidingWindowSizeSeconds = lResourceConfig.getConfigurationValue("slidingWindowSizeSeconds",
        lCircuitBreakerConfig.getSlidingWindowSizeSeconds(), Integer.class);
    lCircuitBreakerConfig.setSlidingWindowSizeSeconds(lSlidingWindowSizeSeconds);

    lRESTClientConfig.setCircuitBreakerConfiguration(lCircuitBreakerConfig);

    return lRESTClientConfig;
  }
}
