/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.rest.composite.impl.kryo.jeaf;

import com.anaptecs.jeaf.core.spi.ServiceProviderImplementation;
import com.anaptecs.jeaf.rest.composite.api.jeaf.CompositeTypeConverterServiceProvider;
import com.anaptecs.jeaf.rest.composite.impl.kryo.KryoCompositeTypeConverter;
import com.anaptecs.jeaf.xfun.api.errorhandling.SystemException;
import com.anaptecs.jeaf.xfun.api.health.CheckLevel;
import com.anaptecs.jeaf.xfun.api.health.HealthCheckResult;

/**
 * Class implements a {@link RESTRequestExecutor} that is based on Apache HTTP Client and Resilience4J circuit breaker.
 * Configurations are read from YAML files via the classpath. The configuration file must have the lower case simple
 * name of the service.
 * 
 * @author JEAF Development Team
 */
public class KryoCompositeTypeConverterServiceProviderImpl extends KryoCompositeTypeConverter implements
    CompositeTypeConverterServiceProvider, ServiceProviderImplementation {

  @Override
  public HealthCheckResult check( CheckLevel pLevel ) {
    return null;
  }

  @Override
  public void initialize( ) throws SystemException {
    // Nothing to do.
  }
}
