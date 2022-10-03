/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.rest.impl.executor.test;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.rest.executor.impl.config.ApacheHttpClientConfiguration;
import com.anaptecs.jeaf.rest.executor.impl.config.CircuitBreakerConfiguration;
import com.anaptecs.jeaf.rest.executor.impl.config.RESTClientConfigurationImpl;

class ConfigurationTest {
  @Test
  void testApacheConfiguration( ) {
    ApacheHttpClientConfiguration lHttpClientConfiguration = new ApacheHttpClientConfiguration();
    lHttpClientConfiguration.setConnectionRequestTimeout(47);
    assertEquals(47, lHttpClientConfiguration.getConnectionRequestTimeout());
    lHttpClientConfiguration.setConnectTimeout(32);
    assertEquals(32, lHttpClientConfiguration.getConnectTimeout());
    lHttpClientConfiguration.setKeepAliveDuration(888);
    assertEquals(888, lHttpClientConfiguration.getKeepAliveDuration());
    lHttpClientConfiguration.setMaxIdleConnections(35);
    assertEquals(35, lHttpClientConfiguration.getMaxIdleConnections());
    lHttpClientConfiguration.setMaxPoolSize(89);
    assertEquals(89, lHttpClientConfiguration.getMaxPoolSize());
    lHttpClientConfiguration.setMaxRetries(7);
    assertEquals(7, lHttpClientConfiguration.getMaxRetries());
    lHttpClientConfiguration.setResponseTimeout(745);
    assertEquals(745, lHttpClientConfiguration.getResponseTimeout());
    lHttpClientConfiguration.setRetryInterval(333);
    assertEquals(333, lHttpClientConfiguration.getRetryInterval());
    lHttpClientConfiguration.setValidateAfterInactivityDuration(9874);
    assertEquals(9874, lHttpClientConfiguration.getValidateAfterInactivityDuration());

    // Test default values.
    lHttpClientConfiguration = new ApacheHttpClientConfiguration();
    assertEquals(100, lHttpClientConfiguration.getConnectionRequestTimeout());
    assertEquals(2000, lHttpClientConfiguration.getConnectTimeout());
    assertEquals(20000, lHttpClientConfiguration.getKeepAliveDuration());
    assertEquals(5, lHttpClientConfiguration.getMaxIdleConnections());
    assertEquals(5, lHttpClientConfiguration.getMaxPoolSize());
    assertEquals(0, lHttpClientConfiguration.getMaxRetries());
    assertEquals(5000, lHttpClientConfiguration.getResponseTimeout());
    assertEquals(100, lHttpClientConfiguration.getRetryInterval());
    assertEquals(10000, lHttpClientConfiguration.getValidateAfterInactivityDuration());

  }

  @Test
  void testCircuitBreakerConfiguration( ) {
    CircuitBreakerConfiguration lCircuitBreakerConfiguration = new CircuitBreakerConfiguration();
    lCircuitBreakerConfiguration.setDurationInOpenState(47);
    assertEquals(47, lCircuitBreakerConfiguration.getDurationInOpenState());
    lCircuitBreakerConfiguration.setFailureRateThreshold(80);
    assertEquals(80, lCircuitBreakerConfiguration.getFailureRateThreshold());
    lCircuitBreakerConfiguration.setPermittedCallsInHalfOpenState(18);
    assertEquals(18, lCircuitBreakerConfiguration.getPermittedCallsInHalfOpenState());
    lCircuitBreakerConfiguration.setSlidingWindowSizeSeconds(60);
    assertEquals(60, lCircuitBreakerConfiguration.getSlidingWindowSizeSeconds());
    lCircuitBreakerConfiguration.setSlowRequestDuration(55555);
    assertEquals(55555, lCircuitBreakerConfiguration.getSlowRequestDuration());
    lCircuitBreakerConfiguration.setSlowRequestRateThreshold(99);
    assertEquals(99, lCircuitBreakerConfiguration.getSlowRequestRateThreshold());

    // Test default values
    lCircuitBreakerConfiguration = new CircuitBreakerConfiguration();
    assertEquals(20000, lCircuitBreakerConfiguration.getDurationInOpenState());
    assertEquals(5, lCircuitBreakerConfiguration.getFailureRateThreshold());
    assertEquals(2, lCircuitBreakerConfiguration.getPermittedCallsInHalfOpenState());
    assertEquals(5, lCircuitBreakerConfiguration.getSlidingWindowSizeSeconds());
    assertEquals(5000, lCircuitBreakerConfiguration.getSlowRequestDuration());
    assertEquals(30, lCircuitBreakerConfiguration.getSlowRequestRateThreshold());
  }

  @Test
  void testRESTClientConfiguration( ) {
    RESTClientConfigurationImpl lClientConfiguration = new RESTClientConfigurationImpl();
    lClientConfiguration.setCookieDomain("localhost");
    lClientConfiguration.setCookiePath("/cookies");
    lClientConfiguration.setExternalServiceURL("http://localhost:8080");
    lClientConfiguration.setSensitiveHeaders(Arrays.asList("Hello", "World"));
    lClientConfiguration.setTraceRequests(false);
    lClientConfiguration.setTraceResponses(true);

    ApacheHttpClientConfiguration lHttpClientConfiguration = new ApacheHttpClientConfiguration();
    lHttpClientConfiguration.setConnectionRequestTimeout(47);
    lHttpClientConfiguration.setConnectTimeout(32);
    lHttpClientConfiguration.setKeepAliveDuration(888);
    lHttpClientConfiguration.setMaxIdleConnections(35);
    lHttpClientConfiguration.setMaxPoolSize(89);
    lHttpClientConfiguration.setMaxRetries(7);
    lHttpClientConfiguration.setResponseTimeout(745);
    lHttpClientConfiguration.setRetryInterval(333);
    lHttpClientConfiguration.setValidateAfterInactivityDuration(9874);
    lClientConfiguration.setHttpClientConfiguration(lHttpClientConfiguration);

    CircuitBreakerConfiguration lCircuitBreakerConfiguration = new CircuitBreakerConfiguration();
    lCircuitBreakerConfiguration.setDurationInOpenState(47);
    lCircuitBreakerConfiguration.setFailureRateThreshold(80);
    lCircuitBreakerConfiguration.setPermittedCallsInHalfOpenState(18);
    lCircuitBreakerConfiguration.setSlidingWindowSizeSeconds(60);
    lCircuitBreakerConfiguration.setSlowRequestDuration(55555);
    lCircuitBreakerConfiguration.setSlowRequestRateThreshold(99);
    lClientConfiguration.setCircuitBreakerConfiguration(lCircuitBreakerConfiguration);

    // Test configured values
    assertEquals("localhost", lClientConfiguration.getCookieDomain());
    assertEquals("/cookies", lClientConfiguration.getCookiePath());
    assertEquals("http://localhost:8080", lClientConfiguration.getExternalServiceURL());
    assertEquals(2, lClientConfiguration.getSensitiveHeaders().size());
    assertEquals("Hello", lClientConfiguration.getSensitiveHeaders().get(0));
    assertEquals("World", lClientConfiguration.getSensitiveHeaders().get(1));
    assertEquals(2, lClientConfiguration.getSensitiveHeaderNames().size());
    assertEquals("hello", lClientConfiguration.getSensitiveHeaderNames().get(0));
    assertEquals("world", lClientConfiguration.getSensitiveHeaderNames().get(1));
    assertEquals(false, lClientConfiguration.traceRequests());
    assertEquals(true, lClientConfiguration.traceResponses());

    // Test http client config
    assertEquals(47, lClientConfiguration.getConnectionRequestTimeout());
    assertEquals(32, lClientConfiguration.getConnectTimeout());
    assertEquals(888, lClientConfiguration.getKeepAliveDuration());
    assertEquals(35, lClientConfiguration.getMaxIdleConnections());
    assertEquals(89, lClientConfiguration.getMaxPoolSize());
    assertEquals(7, lClientConfiguration.getMaxRetries());
    assertEquals(745, lClientConfiguration.getResponseTimeout());
    assertEquals(333, lClientConfiguration.getRetryInterval());
    assertEquals(9874, lClientConfiguration.getValidateAfterInactivityDuration());

    // Test circuit breaker config
    assertEquals(47, lClientConfiguration.getDurationInOpenState());
    assertEquals(80, lClientConfiguration.getFailureRateThreshold());
    assertEquals(18, lClientConfiguration.getPermittedCallsInHalfOpenState());
    assertEquals(60, lClientConfiguration.getSlidingWindowSizeSeconds());
    assertEquals(55555, lClientConfiguration.getSlowRequestDuration());
    assertEquals(99, lClientConfiguration.getSlowRequestRateThreshold());

    // No exception is expected.
    lClientConfiguration.validate();

    // Test default configuration
    lClientConfiguration = new RESTClientConfigurationImpl();
    assertEquals(null, lClientConfiguration.getCookieDomain());
    assertEquals(null, lClientConfiguration.getCookiePath());
    assertEquals(null, lClientConfiguration.getExternalServiceURL());
    assertEquals(1, lClientConfiguration.getSensitiveHeaders().size());
    assertEquals("Authorization", lClientConfiguration.getSensitiveHeaders().get(0));
    assertEquals(1, lClientConfiguration.getSensitiveHeaderNames().size());
    assertEquals("authorization", lClientConfiguration.getSensitiveHeaderNames().get(0));
    assertEquals(true, lClientConfiguration.traceRequests());
    assertEquals(false, lClientConfiguration.traceResponses());

    assertEquals(100, lClientConfiguration.getConnectionRequestTimeout());
    assertEquals(2000, lClientConfiguration.getConnectTimeout());
    assertEquals(20000, lClientConfiguration.getKeepAliveDuration());
    assertEquals(5, lClientConfiguration.getMaxIdleConnections());
    assertEquals(5, lClientConfiguration.getMaxPoolSize());
    assertEquals(0, lClientConfiguration.getMaxRetries());
    assertEquals(5000, lClientConfiguration.getResponseTimeout());
    assertEquals(100, lClientConfiguration.getRetryInterval());
    assertEquals(10000, lClientConfiguration.getValidateAfterInactivityDuration());

    assertEquals(20000, lClientConfiguration.getDurationInOpenState());
    assertEquals(5, lClientConfiguration.getFailureRateThreshold());
    assertEquals(2, lClientConfiguration.getPermittedCallsInHalfOpenState());
    assertEquals(5, lClientConfiguration.getSlidingWindowSizeSeconds());
    assertEquals(5000, lClientConfiguration.getSlowRequestDuration());
    assertEquals(30, lClientConfiguration.getSlowRequestRateThreshold());

    try {
      lClientConfiguration.validate();
      fail();
    }
    catch (IllegalArgumentException e) {
      assertEquals(
          "Mandatory configuration parameter 'externalServiceURL' is not set. Please fix your configuration and try again.",
          e.getMessage());
    }

    lClientConfiguration = new RESTClientConfigurationImpl();
    lClientConfiguration.setSensitiveHeaders(null);
    assertEquals(null, lClientConfiguration.getSensitiveHeaders());
    assertEquals(0, lClientConfiguration.getSensitiveHeaderNames().size());

  }
}
