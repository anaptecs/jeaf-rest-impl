/*
 * anaptecs GmbH, Ricarda-Huch-Str. 71, 72760 Reutlingen, Germany
 * 
 * Copyright 2004 - 2019. All rights reserved.
 */
package com.anaptecs.jeaf.rest.executor.impl.config.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.anaptecs.jeaf.rest.executor.impl.config.RESTClientConfiguration;

/**
 * Class provided a {@link RESTClientConfiguration} that reads all required configuration parameters from a YAML file.
 * 
 * @author JEAF Development Team
 */
public class RESTClientConfigurationImpl implements RESTClientConfiguration {
  /**
   * URL of the REST service that is proxied by this service implementation.
   */
  private String externalServiceURL;

  /**
   * Domain of the cookie that is used in requests.
   */
  private String cookieDomain;

  /**
   * Path of the cookie that is used in requests.
   */
  private String cookiePath;

  /**
   * List of http headers that is considered to be security sensitive.
   */
  private List<String> sensitiveHeaders = Arrays.asList("Authorization");

  private List<String> sensitiveHeaderNames;

  /**
   * Attribute defines if http requests should be traced.
   */
  private boolean traceRequests = true;

  /**
   * Attribute defines if http responses should be traced.
   */
  private boolean traceResponses = false;

  /**
   * Configuration for Apache HTTP Client
   */
  private ApacheHttpClientConfiguration httpClientConfiguration = new ApacheHttpClientConfiguration();

  /**
   * Configuration for Resilience4J circuit breaker.
   */
  private CircuitBreakerConfiguration circuitBreakerConfiguration = new CircuitBreakerConfiguration();

  /**
   * Method validates this configuration object if all required configuration parameters as set.
   */
  public void validate( ) {
    if (externalServiceURL == null) {
      throw new IllegalArgumentException(
          "Mandatory configuration parameter 'externalServiceURL' is not set. Please fix your configuration and try again.");
    }
  }

  /**
   * Initialize object. This constructor is intended to be used in cases where properties are set via reflection based
   * e.g. when deserializing an object using YAML libraries.
   */
  public RESTClientConfigurationImpl( ) {
  }

  /**
   * Method returns the URL of the REST service that is proxied by this service implementation.
   * 
   * @return String URL of the REST service. The Method never returns null.
   */
  @Override
  public String getExternalServiceURL( ) {
    return externalServiceURL;
  }

  /**
   * Method returns the domain of the cookie that is used in requests.
   * 
   * @return String Cookie domain that should be used.
   */
  @Override
  public String getCookieDomain( ) {
    return cookieDomain;
  }

  /**
   * Method returns the path of the cookie that is used in requests.
   * 
   * @return String Cookie path that should be used.
   */
  @Override
  public String getCookiePath( ) {
    return cookiePath;
  }

  public List<String> getSensitiveHeaders( ) {
    return sensitiveHeaders;
  }

  /**
   * Method returns the http header names that are considered to be sensitive.
   * 
   * @return {@link List} List with the names of all http headers that are considered to be sensitive. All returned
   * header names are normalized to lower-case. The method never returns null.
   */
  @Override
  public List<String> getSensitiveHeaderNames( ) {
    if (sensitiveHeaderNames == null) {
      if (sensitiveHeaders != null) {
        sensitiveHeaderNames = new ArrayList<>(sensitiveHeaders.size());
        for (String lNext : sensitiveHeaders) {
          sensitiveHeaderNames.add(lNext.toLowerCase());
        }
      }
      else {
        sensitiveHeaderNames = Collections.emptyList();
      }
    }
    return sensitiveHeaderNames;
  }

  /**
   * Method returns if requests should be traced or not
   * 
   * @return boolean Method returns <code>true</code> if request should be traced and <code>false</code> otherwise.
   */
  @Override
  public boolean traceRequests( ) {
    return traceRequests;
  }

  /**
   * Method returns if responses should be traced or not
   * 
   * @return boolean Method returns <code>true</code> if responses should be traced and <code>false</code> otherwise.
   */
  @Override
  public boolean traceResponses( ) {
    return traceResponses;
  }

  /**
   * Method returns the maximum size of the connection pool.
   * 
   * @return int Maximum pool size.
   */
  @Override
  public int getMaxPoolSize( ) {
    return httpClientConfiguration.getMaxPoolSize();
  }

  /**
   * Method returns the maximum amount of idle connections in the connection pool.
   * 
   * @return int Maximum amount of idle connections.
   */
  @Override
  public int getMaxIdleConnections( ) {
    return httpClientConfiguration.getMaxIdleConnections();
  }

  /**
   * Method returns the keep alive duration for connection to REST service (in milliseconds).
   * 
   * @return int Connection keep alive duration.
   */
  @Override
  public int getKeepAliveDuration( ) {
    return httpClientConfiguration.getKeepAliveDuration();
  }

  /**
   * Method returns the time period in milliseconds after which a connection is validated before it is taken from the
   * pool again.
   * 
   * @return int Time period in milliseconds after which a connection is validated before it is taken from the pool
   * again.
   */
  @Override
  public int getValidateAfterInactivityDuration( ) {
    return httpClientConfiguration.getValidateAfterInactivityDuration();
  }

  /**
   * Method returns the maximum amount of retries before a call to the REST service is considered to be failed.
   * 
   * @return int Maximum amount of retries before a call to the REST service is considered to be failed.
   */
  @Override
  public int getMaxRetries( ) {
    return httpClientConfiguration.getMaxRetries();
  }

  /**
   * Method returns the interval in milliseconds after which the REST service is called again in case that retries are
   * configured.
   * 
   * @return int Interval in milliseconds after which the REST service is called again in case that retries are
   * configured.
   */
  @Override
  public int getRetryInterval( ) {
    return httpClientConfiguration.getRetryInterval();
  }

  /**
   * Method returns the response timeout in milliseconds for calls to REST service.
   * 
   * @return int Response timeout in milliseconds for calls to REST service.
   */
  @Override
  public int getResponseTimeout( ) {
    return httpClientConfiguration.getResponseTimeout();
  }

  /**
   * Method returns the timeout in milliseconds to establish connections to the REST service. As connections are pooled
   * this parameter should not have a too strong influence on the overall behavior.
   * 
   * @return int Timeout in milliseconds to establish connections to the REST service.
   */
  @Override
  public int getConnectTimeout( ) {
    return httpClientConfiguration.getConnectTimeout();
  }

  /**
   * Method returns the connection request timeout when a http connection is taken from the pool.
   * 
   * @return int Connection request timeout in milliseconds.
   */
  @Override
  public int getConnectionRequestTimeout( ) {
    return httpClientConfiguration.getConnectionRequestTimeout();
  }

  /**
   * Method returns the failure rate threshold (percent of requests) defines which amount of failed request must be
   * exceeded due to technical problems that the circuit breaker opens and no further request will be sent to the REST
   * service.
   * 
   * @return int Failure rate threshold.
   */
  @Override
  public int getFailureRateThreshold( ) {
    return circuitBreakerConfiguration.getFailureRateThreshold();
  }

  /**
   * Method returns the duration in milliseconds that the circuit breaker stays open until request will be sent to the
   * REST service again.
   * 
   * @return int Duration in milliseconds that the circuit breaker stays open until request will be sent to the REST
   * service again.
   */
  @Override
  public int getDurationInOpenState( ) {
    return circuitBreakerConfiguration.getDurationInOpenState();
  }

  /**
   * Method returns the duration in milliseconds above which calls are considered as slow and increase the slow calls
   * percentage.
   * 
   * @return int Duration in milliseconds above which calls are considered as slow.
   */
  @Override
  public int getSlowRequestDuration( ) {
    return circuitBreakerConfiguration.getSlowRequestDuration();
  }

  /**
   * Method returns the slow request threshold in percentage. The circuit breaker considers a call as slow when the call
   * duration is greater than <code>slowCallDuration</code>. When the percentage of slow calls is equal to or greater
   * than the threshold, the circuit breaker transitions to open and starts short-circuiting calls.
   * 
   * Value must between 0 and 100.
   * 
   * @return int Slow request threshold in percentage.
   */
  @Override
  public int getSlowRequestRateThreshold( ) {
    return circuitBreakerConfiguration.getSlowRequestRateThreshold();
  }

  /**
   * Method returns the number of permitted calls when the circuit breaker is half open.
   * 
   * @return int Number of permitted calls when the circuit breaker is half open.
   */
  @Override
  public int getPermittedCallsInHalfOpenState( ) {
    return circuitBreakerConfiguration.getPermittedCallsInHalfOpenState();
  }

  /**
   * Method returns the size of the sliding window in seconds which is used to record the outcome of calls when the
   * circuit breaker is closed.
   * 
   * The value must be greater than 0.
   * 
   * @return int Size of the sliding window in seconds which is used to record the outcome of calls.
   */
  @Override
  public int getSlidingWindowSizeSeconds( ) {
    return circuitBreakerConfiguration.getSlidingWindowSizeSeconds();
  }

  public void setExternalServiceURL( String pExternalServiceURL ) {
    externalServiceURL = pExternalServiceURL;
  }

  public void setCookieDomain( String pCookieDomain ) {
    cookieDomain = pCookieDomain;
  }

  public void setCookiePath( String pCookiePath ) {
    cookiePath = pCookiePath;
  }

  public void setSensitiveHeaders( List<String> pSensitiveHeaders ) {
    sensitiveHeaders = pSensitiveHeaders;
  }

  public void setTraceRequests( boolean pTraceRequests ) {
    traceRequests = pTraceRequests;
  }

  public void setTraceResponses( boolean pTraceResponses ) {
    traceResponses = pTraceResponses;
  }

  public void setHttpClientConfiguration( ApacheHttpClientConfiguration pHttpClientConfiguration ) {
    httpClientConfiguration = pHttpClientConfiguration;
  }

  public void setCircuitBreakerConfiguration( CircuitBreakerConfiguration pCircuitBreakerConfiguration ) {
    circuitBreakerConfiguration = pCircuitBreakerConfiguration;
  }
}
