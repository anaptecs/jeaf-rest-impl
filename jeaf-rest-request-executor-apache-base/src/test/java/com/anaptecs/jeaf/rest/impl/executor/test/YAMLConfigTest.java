/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.rest.impl.executor.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.rest.executor.impl.config.RESTClientConfiguration;
import com.anaptecs.jeaf.rest.executor.impl.config.yaml.YAMLBasedRESTClientConfigurationLoader;

class YAMLConfigTest {

  @Test
  void testYAMLConfiguration( ) {
    RESTClientConfiguration lConfiguration = YAMLBasedRESTClientConfigurationLoader.loadConfiguration(
        "productservice.yaml");
    assertNotNull(lConfiguration);

    // Test REST Client configuration parameters
    assertEquals("http://localhost:8099", lConfiguration.getExternalServiceURL());
    assertEquals("localhost", lConfiguration.getCookieDomain());
    assertEquals("/", lConfiguration.getCookiePath());
    assertEquals(2, lConfiguration.getSensitiveHeaderNames().size());
    assertEquals("authorization", lConfiguration.getSensitiveHeaderNames().get(0));
    assertEquals("verysecureheader", lConfiguration.getSensitiveHeaderNames().get(1));
    assertEquals(false, lConfiguration.traceRequests());
    assertEquals(true, lConfiguration.traceResponses());

    // Test HTTP Client Configuration
    assertEquals(33, lConfiguration.getMaxPoolSize());
    assertEquals(7, lConfiguration.getMaxIdleConnections());
    assertEquals(20011, lConfiguration.getKeepAliveDuration());
    assertEquals(10080, lConfiguration.getValidateAfterInactivityDuration());
    assertEquals(42, lConfiguration.getMaxRetries());
    assertEquals(123, lConfiguration.getRetryInterval());
    assertEquals(5111, lConfiguration.getResponseTimeout());
    assertEquals(2001, lConfiguration.getConnectTimeout());
    assertEquals(101, lConfiguration.getConnectionRequestTimeout());

    // Test circuit breaker configuration
    assertEquals(11, lConfiguration.getFailureRateThreshold());
    assertEquals(20001, lConfiguration.getDurationInOpenState());
    assertEquals(5001, lConfiguration.getSlowRequestDuration());
    assertEquals(31, lConfiguration.getSlowRequestRateThreshold());
    assertEquals(5, lConfiguration.getPermittedCallsInHalfOpenState());
    assertEquals(43, lConfiguration.getSlidingWindowSizeSeconds());

    // Test loading of invalid configuration
    try {
      YAMLBasedRESTClientConfigurationLoader.loadConfiguration("empty-productservice.yaml");
    }
    catch (IllegalArgumentException e) {
      assertEquals(
          "Mandatory configuration parameter 'externalServiceURL' is not set. Please fix your configuration and try again.",
          e.getMessage());
    }
  }

  @Test
  void testYAMLConfigurationDefaultValues( ) {
    RESTClientConfiguration lConfiguration = YAMLBasedRESTClientConfigurationLoader.loadConfiguration(
        "minimal-productservice.yaml");

    // Test REST Client configuration parameters
    assertEquals("http://localhost:666", lConfiguration.getExternalServiceURL());
    assertEquals(null, lConfiguration.getCookieDomain());
    assertEquals(null, lConfiguration.getCookiePath());
    assertEquals(1, lConfiguration.getSensitiveHeaderNames().size());
    assertEquals("authorization", lConfiguration.getSensitiveHeaderNames().get(0));
    assertEquals(true, lConfiguration.traceRequests());
    assertEquals(false, lConfiguration.traceResponses());

    // Test HTTP Client Configuration
    assertEquals(5, lConfiguration.getMaxPoolSize());
    assertEquals(5, lConfiguration.getMaxIdleConnections());
    assertEquals(20000, lConfiguration.getKeepAliveDuration());
    assertEquals(10000, lConfiguration.getValidateAfterInactivityDuration());
    assertEquals(0, lConfiguration.getMaxRetries());
    assertEquals(100, lConfiguration.getRetryInterval());
    assertEquals(5000, lConfiguration.getResponseTimeout());
    assertEquals(2000, lConfiguration.getConnectTimeout());
    assertEquals(100, lConfiguration.getConnectionRequestTimeout());

    // Test circuit breaker configuration
    assertEquals(5, lConfiguration.getFailureRateThreshold());
    assertEquals(20000, lConfiguration.getDurationInOpenState());
    assertEquals(5000, lConfiguration.getSlowRequestDuration());
    assertEquals(30, lConfiguration.getSlowRequestRateThreshold());
    assertEquals(2, lConfiguration.getPermittedCallsInHalfOpenState());
    assertEquals(5, lConfiguration.getSlidingWindowSizeSeconds());
  }
}
