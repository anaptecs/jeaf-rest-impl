/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.rest.impl.executor.test;

import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.rest.executor.impl.apache.YAMLBasedRESTClientConfiguration;

class YAMLConfigTest {

  @Test
  void testYAMLConfiguration( ) {
    YAMLBasedRESTClientConfiguration lConfiguration = YAMLBasedRESTClientConfiguration.loadConfiguration(
        "productservice.yml");

  }
}
