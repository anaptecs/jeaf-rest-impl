/*
 * anaptecs GmbH, Ricarda-Huch-Str. 71, 72760 Reutlingen, Germany
 * 
 * Copyright 2004 - 2019. All rights reserved.
 */
package com.anaptecs.jeaf.rest.executor.impl.apache.jeaf;

import com.anaptecs.jeaf.core.api.ServiceProvider;
import com.anaptecs.jeaf.core.servicechannel.api.ServiceProviderFactory;
import com.anaptecs.jeaf.core.spi.ServiceProviderImplementation;
import com.anaptecs.jeaf.rest.executor.api.jeaf.RESTRequestExecutorServiceProvider;

/**
 * This class is the factory class the service provider implementation ProductServiceRESTProxyServiceProviderImpl().
 * 
 * 
 */
@com.anaptecs.jeaf.core.annotations.ServiceProviderFactory
public final class RESTRequestExecutorServiceProviderFactory extends ServiceProviderFactory {
  /**
   * Initialize object. No actions have to be performed.
   */
  public RESTRequestExecutorServiceProviderFactory( ) {
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
    return new RESTRequestExecutorServiceProviderImpl();
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
    return RESTRequestExecutorServiceProvider.class;
  }
}