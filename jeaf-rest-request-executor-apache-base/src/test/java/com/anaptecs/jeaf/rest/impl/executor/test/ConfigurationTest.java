/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.rest.impl.executor.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.rest.executor.impl.config.ApacheHttpClientConfiguration;

public class ConfigurationTest {
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
}
