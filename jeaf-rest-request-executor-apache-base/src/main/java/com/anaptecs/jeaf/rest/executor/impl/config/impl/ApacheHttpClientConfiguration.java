/*
 * anaptecs GmbH, Ricarda-Huch-Str. 71, 72760 Reutlingen, Germany
 * 
 * Copyright 2004 - 2019. All rights reserved.
 */
package com.anaptecs.jeaf.rest.executor.impl.config.impl;

import com.anaptecs.jeaf.rest.executor.impl.config.RESTClientConfiguration;

/**
 * Class provided a {@link RESTClientConfiguration} that reads all required configuration parameters from a YAML file.
 * 
 * @author JEAF Development Team
 */
public class ApacheHttpClientConfiguration {
  /**
   * Maximum size of the connection pool.
   */
  private int maxPoolSize = 5;

  /**
   * Maximum amount of idle connections in the connection pool.
   */
  private int maxIdleConnections = 5;

  /**
   * Keep alive duration for connection to REST service (in milliseconds).
   */
  private int keepAliveDuration = 20000;

  /**
   * Parameter configures the time period in milliseconds after which a connection is validated before it is taken from
   * the pool again.
   */
  private int validateAfterInactivityDuration = 10000;

  /**
   * Maximum amount of retries before a call to the REST service is considered to be failed.
   */
  private int maxRetries = 0;

  /**
   * Interval in milliseconds after which the REST service is called again in case that retries are configured.
   */
  private int retryInterval = 100;

  /**
   * Response timeout in milliseconds for calls to REST service. Please be aware that this is a very sensitive parameter
   * and needs to be fine-tuned for your purposes.
   */
  private int responseTimeout = 5000;

  /**
   * Timeout in milliseconds to establish connections to the REST service. As connections are pooled this parameter
   * should not have a too strong influence on the overall behavior. However please ensure that it fits to your
   * environment.
   */
  private int connectTimeout = 2000;

  /**
   * Timeout in milliseconds when requesting a connection from the pool of http connections. This parameter especially
   * becomes important in cases where a connection pool is configured too small or in cases of unexpected high load.
   */
  private int connectionRequestTimeout = 100;

  /**
   * Method returns the maximum size of the connection pool.
   * 
   * @return int Maximum pool size.
   */
  public int getMaxPoolSize( ) {
    return maxPoolSize;
  }

  /**
   * Method returns the maximum amount of idle connections in the connection pool.
   * 
   * @return int Maximum amount of idle connections.
   */
  public int getMaxIdleConnections( ) {
    return maxIdleConnections;
  }

  /**
   * Method returns the keep alive duration for connection to REST service (in milliseconds).
   * 
   * @return int Connection keep alive duration.
   */
  public int getKeepAliveDuration( ) {
    return keepAliveDuration;
  }

  /**
   * Method returns the time period in milliseconds after which a connection is validated before it is taken from the
   * pool again.
   * 
   * @return int Time period in milliseconds after which a connection is validated before it is taken from the pool
   * again.
   */
  public int getValidateAfterInactivityDuration( ) {
    return validateAfterInactivityDuration;
  }

  /**
   * Method returns the maximum amount of retries before a call to the REST service is considered to be failed.
   * 
   * @return int Maximum amount of retries before a call to the REST service is considered to be failed.
   */
  public int getMaxRetries( ) {
    return maxRetries;
  }

  /**
   * Method returns the interval in milliseconds after which the REST service is called again in case that retries are
   * configured.
   * 
   * @return int Interval in milliseconds after which the REST service is called again in case that retries are
   * configured.
   */
  public int getRetryInterval( ) {
    return retryInterval;
  }

  /**
   * Method returns the response timeout in milliseconds for calls to REST service.
   * 
   * @return int Response timeout in milliseconds for calls to REST service.
   */
  public int getResponseTimeout( ) {
    return responseTimeout;
  }

  /**
   * Method returns the timeout in milliseconds to establish connections to the REST service. As connections are pooled
   * this parameter should not have a too strong influence on the overall behavior.
   * 
   * @return int Timeout in milliseconds to establish connections to the REST service.
   */
  public int getConnectTimeout( ) {
    return connectTimeout;
  }

  /**
   * Method returns the connection request timeout when a http connection is taken from the pool.
   * 
   * @return int Connection request timeout in milliseconds.
   */
  public int getConnectionRequestTimeout( ) {
    return connectionRequestTimeout;
  }

  public void setMaxPoolSize( int pMaxPoolSize ) {
    maxPoolSize = pMaxPoolSize;
  }

  public void setMaxIdleConnections( int pMaxIdleConnections ) {
    maxIdleConnections = pMaxIdleConnections;
  }

  public void setKeepAliveDuration( int pKeepAliveDuration ) {
    keepAliveDuration = pKeepAliveDuration;
  }

  public void setValidateAfterInactivityDuration( int pValidateAfterInactivityDuration ) {
    validateAfterInactivityDuration = pValidateAfterInactivityDuration;
  }

  public void setMaxRetries( int pMaxRetries ) {
    maxRetries = pMaxRetries;
  }

  public void setRetryInterval( int pRetryInterval ) {
    retryInterval = pRetryInterval;
  }

  public void setResponseTimeout( int pResponseTimeout ) {
    responseTimeout = pResponseTimeout;
  }

  public void setConnectTimeout( int pConnectTimeout ) {
    connectTimeout = pConnectTimeout;
  }

  public void setConnectionRequestTimeout( int pConnectionRequestTimeout ) {
    connectionRequestTimeout = pConnectionRequestTimeout;
  }

}
