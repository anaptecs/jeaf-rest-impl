/*
 * anaptecs GmbH, Ricarda-Huch-Str. 71, 72760 Reutlingen, Germany
 * 
 * Copyright 2004 - 2019. All rights reserved.
 */
package com.anaptecs.jeaf.rest.executor.impl.apache;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;

/**
 * Class provided a {@link RESTClientConfiguration} that reads all required configuration parameters from a YAML file.
 * 
 * @author JEAF Development Team
 */
public class YAMLBasedRESTClientConfiguration implements RESTClientConfiguration {
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
  private List<String> sensitiveHeaders;

  private List<String> sensitiveHeaderNames;

  /**
   * Maximum size of the connection pool.
   */
  private int maxPoolSize;

  /**
   * Maximum amount of idle connections in the connection pool.
   */
  private int maxIdleConnections;

  /**
   * Keep alive duration for connection to REST service (in milliseconds).
   */
  private int keepAliveDuration;

  /**
   * Parameter configures the time period in milliseconds after which a connection is validated before it is taken from
   * the pool again.
   */
  private int validateAfterInactivityDuration;

  /**
   * Maximum amount of retries before a call to the REST service is considered to be failed.
   */
  private int maxRetries;

  /**
   * Interval in milliseconds after which the REST service is called again in case that retries are configured.
   */
  private int retryInterval;

  /**
   * Response timeout in milliseconds for calls to REST service. Please be aware that this is a very sensitive parameter
   * and needs to be fine-tuned for your purposes.
   */
  private int responseTimeout;

  /**
   * Timeout in milliseconds to establish connections to the REST service. As connections are pooled this parameter
   * should not have a too strong influence on the overall behavior. However please ensure that it fits to your
   * environment.
   */
  private int connectTimeout;

  /**
   * Timeout in milliseconds when requesting a connection from the pool of http connections. This parameter especially
   * becomes important in cases where a connection pool is configured too small or in cases of unexpected high load.
   */
  private int connectionRequestTimeout;

  /**
   * Attribute defines if http request should be traced.
   */
  private boolean traceRequests;

  /**
   * Attribute defies if http responses should be traced.
   */
  private boolean traceResponses;

  /**
   * Failure rate threshold (percent of requests) defines which amount of failed request must be exceeded due to
   * technical problems that the circuit breaker opens and no further request will be sent to the REST service.
   * 
   * Value must between 0 and 100.
   */
  private int failureRateThreshold;

  /**
   * Duration in milliseconds that the circuit breaker stays open until request will be sent to the REST service again.
   * 
   * The value must be zero or greater.
   */
  private int durationInOpenState;

  /**
   * Configures the duration in milliseconds above which calls are considered as slow and increase the slow calls
   * percentage.
   * 
   * The value must be zero or greater.
   */
  private int slowRequestDuration;

  /**
   * Configures the slow request threshold in percentage. The circuit breaker considers a call as slow when the call
   * duration is greater than <code>slowCallDuration</code>. When the percentage of slow calls is equal to or greater
   * than the threshold, the circuit breaker transitions to open and starts short-circuiting calls.
   * 
   * Value must between 0 and 100.
   */
  private int slowRequestRateThreshold;

  /**
   * Configures the number of permitted calls when the circuit breaker is half open.
   * 
   * The value must be zero or greater.
   */
  private int permittedCallsInHalfOpenState;

  /**
   * Configures the size of the sliding window in seconds which is used to record the outcome of calls when the circuit
   * breaker is closed.
   * 
   * The value must be greater than 0.
   */
  private int slidingWindowSizeSeconds;

  public static YAMLBasedRESTClientConfiguration loadConfiguration( String pResource ) {
    Yaml lYAML = new Yaml(new Constructor(YAMLBasedRESTClientConfiguration.class));
    lYAML.setBeanAccess(BeanAccess.FIELD);
    try (InputStream lInputStream = YAMLBasedRESTClientConfiguration.class.getClassLoader().getResourceAsStream(
        pResource)) {
      if (lInputStream != null) {
        return (YAMLBasedRESTClientConfiguration) lYAML.load(lInputStream);
      }
      else {
        throw new IllegalArgumentException("Unable to load YAML configuration. '" + pResource
            + "' cloud not be found in the applications classpath.");
      }
    }
    catch (IOException e) {
      throw new IllegalArgumentException("Unable to load YAML configuration '" + pResource
          + "'. " + e.getMessage());
    }
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
   * Method returns the maximum size of the connection pool.
   * 
   * @return int Maximum pool size.
   */
  @Override
  public int getMaxPoolSize( ) {
    return maxPoolSize;
  }

  /**
   * Method returns the maximum amount of idle connections in the connection pool.
   * 
   * @return int Maximum amount of idle connections.
   */
  @Override
  public int getMaxIdleConnections( ) {
    return maxIdleConnections;
  }

  /**
   * Method returns the keep alive duration for connection to REST service (in milliseconds).
   * 
   * @return int Connection keep alive duration.
   */
  @Override
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
  @Override
  public int getValidateAfterInactivityDuration( ) {
    return validateAfterInactivityDuration;
  }

  /**
   * Method returns the maximum amount of retries before a call to the REST service is considered to be failed.
   * 
   * @return int Maximum amount of retries before a call to the REST service is considered to be failed.
   */
  @Override
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
  @Override
  public int getRetryInterval( ) {
    return retryInterval;
  }

  /**
   * Method returns the response timeout in milliseconds for calls to REST service.
   * 
   * @return int Response timeout in milliseconds for calls to REST service.
   */
  @Override
  public int getResponseTimeout( ) {
    return responseTimeout;
  }

  /**
   * Method returns the timeout in milliseconds to establish connections to the REST service. As connections are pooled
   * this parameter should not have a too strong influence on the overall behavior.
   * 
   * @return int Timeout in milliseconds to establish connections to the REST service.
   */
  @Override
  public int getConnectTimeout( ) {
    return connectTimeout;
  }

  /**
   * Method returns the connection request timeout when a http connection is taken from the pool.
   * 
   * @return int Connection request timeout in milliseconds.
   */
  @Override
  public int getConnectionRequestTimeout( ) {
    return connectionRequestTimeout;
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
   * Method returns the failure rate threshold (percent of requests) defines which amount of failed request must be
   * exceeded due to technical problems that the circuit breaker opens and no further request will be sent to the REST
   * service.
   * 
   * @return int Failure rate threshold.
   */
  @Override
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
  @Override
  public int getDurationInOpenState( ) {
    return durationInOpenState;
  }

  /**
   * Method returns the duration in milliseconds above which calls are considered as slow and increase the slow calls
   * percentage.
   * 
   * @return int Duration in milliseconds above which calls are considered as slow.
   */
  @Override
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
  @Override
  public int getSlowRequestRateThreshold( ) {
    return slowRequestRateThreshold;
  }

  /**
   * Method returns the number of permitted calls when the circuit breaker is half open.
   * 
   * @return int Number of permitted calls when the circuit breaker is half open.
   */
  @Override
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
  @Override
  public int getSlidingWindowSizeSeconds( ) {
    return slidingWindowSizeSeconds;
  }
}
