/*
 * anaptecs GmbH, Ricarda-Huch-Str. 71, 72760 Reutlingen, Germany
 * 
 * Copyright 2004 - 2019. All rights reserved.
 */
package com.anaptecs.jeaf.rest.executor.impl.apache;

import java.util.List;

/**
 * Class provides all the configuration parameters that are required by REST Proxy Service implementation.
 */
public interface RESTClientConfiguration {
  /**
   * Method returns the URL of the REST service that is proxied by this service implementation.
   * 
   * @return String URL of the REST service. The Method never returns null.
   */
  String getExternalServiceURL( );

  /**
   * Method returns the domain of the cookie that is used in requests.
   * 
   * @return String Cookie domain that should be used.
   */
  public String getCookieDomain( );

  /**
   * Method returns the path of the cookie that is used in requests.
   * 
   * @return String Cookie path that should be used.
   */
  public String getCookiePath( );

  /**
   * Method returns the http header names that are considered to be sensitive.
   * 
   * @return {@link List} List with the names of all http headers that are considered to be sensitive. All returned
   * header names have to be normalized to lower-case. The method never returns null.
   */
  public List<String> getSensitiveHeaderNames( );

  /**
   * Method returns the maximum size of the connection pool.
   * 
   * @return int Maximum pool size.
   */
  public int getMaxPoolSize( );

  /**
   * Method returns the maximum amount of idle connections in the connection pool.
   * 
   * @return int Maximum amount of idle connections.
   */
  public int getMaxIdleConnections( );

  /**
   * Method returns the keep alive duration for connection to REST service (in milliseconds).
   * 
   * @return int Connection keep alive duration.
   */
  public int getKeepAliveDuration( );

  /**
   * Method returns the time period in milliseconds after which a connection is validated before it is taken from the
   * pool again.
   * 
   * @return int Time period in milliseconds after which a connection is validated before it is taken from the pool
   * again.
   */
  public int getValidateAfterInactivityDuration( );

  /**
   * Method returns the maximum amount of retries before a call to the REST service is considered to be failed.
   * 
   * @return int Maximum amount of retries before a call to the REST service is considered to be failed.
   */
  public int getMaxRetries( );

  /**
   * Method returns the interval in milliseconds after which the REST service is called again in case that retries are
   * configured.
   * 
   * @return int Interval in milliseconds after which the REST service is called again in case that retries are
   * configured.
   */
  public int getRetryInterval( );

  /**
   * Method returns the response timeout in milliseconds for calls to REST service.
   * 
   * @return int Response timeout in milliseconds for calls to REST service.
   */
  public int getResponseTimeout( );

  /**
   * Method returns the timeout in milliseconds to establish connections to the REST service. As connections are pooled
   * this parameter should not have a too strong influence on the overall behavior.
   * 
   * @return int Timeout in milliseconds to establish connections to the REST service.
   */
  public int getConnectTimeout( );

  /**
   * Method returns the connection request timeout when a http connection is taken from the pool.
   * 
   * @return int Connection request timeout in milliseconds.
   */
  public int getConnectionRequestTimeout( );

  /**
   * Method returns if requests should be traced or not
   * 
   * @return boolean Method returns <code>true</code> if request should be traced and <code>false</code> otherwise.
   */
  public boolean traceRequests( );

  /**
   * Method returns if responses should be traced or not
   * 
   * @return boolean Method returns <code>true</code> if responses should be traced and <code>false</code> otherwise.
   */
  public boolean traceResponses( );

  /**
   * Method returns the failure rate threshold (percent of requests) defines which amount of failed request must be
   * exceeded due to technical problems that the circuit breaker opens and no further request will be sent to the REST
   * service.
   * 
   * @return int Failure rate threshold.
   */
  public int getFailureRateThreshold( );

  /**
   * Method returns the duration in milliseconds that the circuit breaker stays open until request will be sent to the
   * REST service again.
   * 
   * @return int Duration in milliseconds that the circuit breaker stays open until request will be sent to the REST
   * service again.
   */
  public int getDurationInOpenState( );

  /**
   * Method returns the duration in milliseconds above which calls are considered as slow and increase the slow calls
   * percentage.
   * 
   * @return int Duration in milliseconds above which calls are considered as slow.
   */
  public int getSlowRequestDuration( );

  /**
   * Method returns the slow request threshold in percentage. The circuit breaker considers a call as slow when the call
   * duration is greater than <code>slowCallDuration</code>. When the percentage of slow calls is equal to or greater
   * than the threshold, the circuit breaker transitions to open and starts short-circuiting calls.
   * 
   * Value must between 0 and 100.
   * 
   * @return int Slow request threshold in percentage.
   */
  public int getSlowRequestRateThreshold( );

  /**
   * Method returns the number of permitted calls when the circuit breaker is half open.
   * 
   * @return int Number of permitted calls when the circuit breaker is half open.
   */
  public int getPermittedCallsInHalfOpenState( );

  /**
   * Method returns the size of the sliding window in seconds which is used to record the outcome of calls when the
   * circuit breaker is closed.
   * 
   * The value must be greater than 0.
   * 
   * @return int Size of the sliding window in seconds which is used to record the outcome of calls.
   */
  public int getSlidingWindowSizeSeconds( );
}
