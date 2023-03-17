/*
 * anaptecs GmbH, Ricarda-Huch-Str. 71, 72760 Reutlingen, Germany
 * 
 * Copyright 2004 - 2019. All rights reserved.
 */
package com.anaptecs.jeaf.rest.resource.impl.jeaf;

import com.anaptecs.jeaf.core.api.ServiceProvider;
import com.anaptecs.jeaf.core.servicechannel.api.ServiceProviderFactory;
import com.anaptecs.jeaf.core.spi.ServiceProviderImplementation;
import com.anaptecs.jeaf.rest.resource.api.jeaf.ValidationExecutorServiceProvider;

/**
 * This class is the factory class the service provider implementation
 * {@link ValidationExecutorServiceProviderImpl}.
 * 
 * @author JEAF Development Team
 */
@com.anaptecs.jeaf.core.annotations.ServiceProviderFactory
public final class ValidationExecutorProviderServiceProviderFactory extends ServiceProviderFactory {
  /**
   * Initialize object. No actions have to be performed.
   */
  public ValidationExecutorProviderServiceProviderFactory( ) {
    // Nothing to do.
  }

  /**
   * Method creates a new instance of the service provider.
   * 
   * @return {@link ServiceProviderImplementation} Instance of service provider. The method never returns null.
   * 
   * @see com.anaptecs.jeaf.core.servicechannel.api.ServiceProviderFactory#createServiceProviderImplementation()
   */
  public ServiceProviderImplementation createServiceProviderImplementation( ) {
    return new ValidationExecutorServiceProviderImpl();
  }

  /**
   * Method returns the interface of the service provider created by this factory.
   * 
   * @return Class Class object of interface that belongs to the service provider that is created by this factory. The
   * method never returns null.
   * 
   * @see com.anaptecs.jeaf.core.servicechannel.api.ServiceProviderFactory#getServiceProviderInterface()
   */
  public Class<? extends ServiceProvider> getServiceProviderInterface( ) {
    return ValidationExecutorServiceProvider.class;
  }
}