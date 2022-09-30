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
public class CircuitBreakerConfiguration {
  /**
   * Failure rate threshold (percent of requests) defines which amount of failed request must be exceeded due to
   * technical problems that the circuit breaker opens and no further request will be sent to the REST service.
   * 
   * Value must between 0 and 100.
   */
  private int failureRateThreshold = 5;

  /**
   * Duration in milliseconds that the circuit breaker stays open until request will be sent to the REST service again.
   * 
   * The value must be zero or greater.
   */
  private int durationInOpenState = 20000;

  /**
   * Configures the duration in milliseconds above which calls are considered as slow and increase the slow calls
   * percentage.
   * 
   * The value must be zero or greater.
   */
  private int slowRequestDuration = 5000;

  /**
   * Configures the slow request threshold in percentage. The circuit breaker considers a call as slow when the call
   * duration is greater than <code>slowCallDuration</code>. When the percentage of slow calls is equal to or greater
   * than the threshold, the circuit breaker transitions to open and starts short-circuiting calls.
   * 
   * Value must between 0 and 100.
   */
  private int slowRequestRateThreshold = 30;

  /**
   * Configures the number of permitted calls when the circuit breaker is half open.
   * 
   * The value must be zero or greater.
   */
  private int permittedCallsInHalfOpenState = 2;

  /**
   * Configures the size of the sliding window in seconds which is used to record the outcome of calls when the circuit
   * breaker is closed.
   * 
   * The value must be greater than 0.
   */
  private int slidingWindowSizeSeconds = 5;

  /**
   * Method returns the failure rate threshold (percent of requests) defines which amount of failed request must be
   * exceeded due to technical problems that the circuit breaker opens and no further request will be sent to the REST
   * service.
   * 
   * @return int Failure rate threshold.
   */
  public int getFailureRateThreshold( ) {
    return failureRateThreshold;
  }

  /**
   * Method returns the duration in milliseconds that the circuit breaker stays open until request will be sent to the
   * REST service again.
   * 
   * @return int Duration in milliseconds that the circuit breaker stays open until request will be sent to the REST
   * service again.
   */
  public int getDurationInOpenState( ) {
    return durationInOpenState;
  }

  /**
   * Method returns the duration in milliseconds above which calls are considered as slow and increase the slow calls
   * percentage.
   * 
   * @return int Duration in milliseconds above which calls are considered as slow.
   */
  public int getSlowRequestDuration( ) {
    return slowRequestDuration;
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
  public int getSlowRequestRateThreshold( ) {
    return slowRequestRateThreshold;
  }

  /**
   * Method returns the number of permitted calls when the circuit breaker is half open.
   * 
   * @return int Number of permitted calls when the circuit breaker is half open.
   */
  public int getPermittedCallsInHalfOpenState( ) {
    return permittedCallsInHalfOpenState;
  }

  /**
   * Method returns the size of the sliding window in seconds which is used to record the outcome of calls when the
   * circuit breaker is closed.
   * 
   * The value must be greater than 0.
   * 
   * @return int Size of the sliding window in seconds which is used to record the outcome of calls.
   */
  public int getSlidingWindowSizeSeconds( ) {
    return slidingWindowSizeSeconds;
  }

  public void setFailureRateThreshold( int pFailureRateThreshold ) {
    failureRateThreshold = pFailureRateThreshold;
  }

  public void setDurationInOpenState( int pDurationInOpenState ) {
    durationInOpenState = pDurationInOpenState;
  }

  public void setSlowRequestDuration( int pSlowRequestDuration ) {
    slowRequestDuration = pSlowRequestDuration;
  }

  public void setSlowRequestRateThreshold( int pSlowRequestRateThreshold ) {
    slowRequestRateThreshold = pSlowRequestRateThreshold;
  }

  public void setPermittedCallsInHalfOpenState( int pPermittedCallsInHalfOpenState ) {
    permittedCallsInHalfOpenState = pPermittedCallsInHalfOpenState;
  }

  public void setSlidingWindowSizeSeconds( int pSlidingWindowSizeSeconds ) {
    slidingWindowSizeSeconds = pSlidingWindowSizeSeconds;
  }

}
