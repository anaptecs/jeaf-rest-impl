/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.rest.resource.impl.jeaf;

import com.anaptecs.jeaf.core.spi.ServiceProviderImplementation;
import com.anaptecs.jeaf.rest.resource.api.jeaf.ValidationExecutorServiceProvider;
import com.anaptecs.jeaf.rest.resource.impl.ValidationExecutorImpl;
import com.anaptecs.jeaf.xfun.api.errorhandling.SystemException;
import com.anaptecs.jeaf.xfun.api.health.CheckLevel;
import com.anaptecs.jeaf.xfun.api.health.HealthCheckResult;

/**
 * Class implements a {@link ValidationExecutorServiceProvider} that is based on {@link ValidationExecutorImpl}.
 * 
 * @author JEAF Development Team
 */
public class ValidationExecutorServiceProviderImpl extends ValidationExecutorImpl
    implements ValidationExecutorServiceProvider, ServiceProviderImplementation {

  @Override
  public HealthCheckResult check( CheckLevel pLevel ) {
    // Nothing to do.
    return null;
  }

  @Override
  public void initialize( ) throws SystemException {
    // Nothing to do.
  }
}
