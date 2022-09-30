/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.rest.executor.impl.apache.jeaf;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;

import com.anaptecs.jeaf.core.spi.ServiceProviderImplementation;
import com.anaptecs.jeaf.json.api.JSON;
import com.anaptecs.jeaf.json.problem.Problem;
import com.anaptecs.jeaf.json.problem.Problem.Builder;
import com.anaptecs.jeaf.json.problem.RESTProblemException;
import com.anaptecs.jeaf.rest.executor.api.RESTRequestExecutor;
import com.anaptecs.jeaf.rest.executor.api.jeaf.RESTRequestExecutorServiceProvider;
import com.anaptecs.jeaf.rest.executor.impl.apache.AbstractApacheHttpClientRESTRequestExecutorBase;
import com.anaptecs.jeaf.rest.executor.impl.config.RESTClientConfiguration;
import com.anaptecs.jeaf.rest.executor.impl.config.yaml.YAMLBasedRESTClientConfigurationLoader;
import com.anaptecs.jeaf.tools.api.http.HTTPStatusCode;
import com.anaptecs.jeaf.xfun.api.XFun;
import com.anaptecs.jeaf.xfun.api.common.ComponentID;
import com.anaptecs.jeaf.xfun.api.errorhandling.SystemException;
import com.anaptecs.jeaf.xfun.api.health.CheckLevel;
import com.anaptecs.jeaf.xfun.api.health.HealthCheckResult;
import com.anaptecs.jeaf.xfun.api.trace.Trace;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class implements a {@link RESTRequestExecutor} that is based on Apache HTTP Client and Resilience4J circuit breaker.
 * Configurations are read from YAML files via the classpath. The configuration file must have the lower case simple
 * name of the service.
 * 
 * @author JEAF Development Team
 */
public class RESTRequestExecutorServiceProviderImpl extends AbstractApacheHttpClientRESTRequestExecutorBase implements
    RESTRequestExecutorServiceProvider, ServiceProviderImplementation {

  /**
   * Reference to the object that identifies this component. The reference is never null.
   */
  private static final ComponentID COMPONENT_ID;

  private static final Trace TRACE;

  /**
   * Static initializer is used to create the components ComponentID object and its trace object.
   */
  static {
    // Create Component ID and trace object.
    Package lBasePackage = RESTRequestExecutorServiceProviderImpl.class.getPackage();
    COMPONENT_ID = new ComponentID("RESTRequestExecutorServiceProviderImpl", lBasePackage.getName());

    TRACE = XFun.getTraceProvider().getTrace(COMPONENT_ID);
  }

  /**
   * Map contains all loaded configurations. Configurations will only be loaded on demand.
   */
  private Map<Class<?>, RESTClientConfiguration> configurations = new HashMap<>();

  @Override
  public HealthCheckResult check( CheckLevel pLevel ) {
    return null;
  }

  @Override
  public void initialize( ) throws SystemException {
    // Nothing to do.
  }

  @Override
  protected ObjectMapper getObjectMapper( ) {
    return JSON.getJSONTools().getDefaultObjectMapper();
  }

  @Override
  protected boolean isRequestTracingEnabled( RESTClientConfiguration pConfiguration ) {
    return pConfiguration.traceRequests() && TRACE.isInfoEnabled();
  }

  @Override
  protected boolean isResponseTracingEnabled( RESTClientConfiguration pConfiguration ) {
    return pConfiguration.traceResponses() && TRACE.isInfoEnabled();
  }

  @Override
  protected void traceRequest( String pRequestLog ) {
    TRACE.info(pRequestLog);
  }

  @Override
  protected void traceResponse( String pResponseLog ) {
    TRACE.info(pResponseLog);
  }

  @Override
  protected void traceException( String pErrorMessage, Exception pException ) {
    TRACE.error(pErrorMessage, pException);
  }

  @Override
  protected synchronized RESTClientConfiguration getConfiguration( Class<?> pServiceClass ) {
    return configurations.computeIfAbsent(pServiceClass, s -> this.loadConfiguration(pServiceClass));
  }

  private RESTClientConfiguration loadConfiguration( Class<?> pServiceClass ) {
    // We expect a YAML file with same name as service to be located in the class path.
    String lResourceFileName = pServiceClass.getSimpleName().toLowerCase() + "-rest-client.yaml";
    return YAMLBasedRESTClientConfigurationLoader.loadConfiguration(lResourceFileName);
  }

  @Override
  protected RuntimeException processErrorResponse( URI pRequestURI, CloseableHttpResponse pResponse ) {
    // Try to read error response from body
    String lResponseBody;
    HttpEntity lEntity = pResponse.getEntity();
    if (lEntity.getContentLength() > 0) {
      try {
        lResponseBody = this.getContent(pResponse.getEntity().getContent());
      }
      catch (IOException e) {
        lResponseBody = "Unable to read error response body. " + e.getMessage();
        this.traceException(lResponseBody, e);
      }
    }
    else {
      lResponseBody = null;
    }

    // REST resource returned problem JSON.
    Problem lProblem;
    if (lResponseBody != null && ContentType.APPLICATION_PROBLEM_JSON.getMimeType().equals(lEntity.getContentType())) {
      try {
        lProblem = this.getObjectMapper().readValue(lResponseBody, Problem.class);
      }
      catch (IOException e) {
        throw this.processInternalServerError(pRequestURI, e, "Unable to process problem JSON");
      }
    }
    else {
      lProblem = null;
    }

    // For whatever reason we were not yet able to build a problem. So let's try to build it up from what we have.
    if (lProblem == null) {
      // Try to resolve some details.
      Builder lProblemBuilder = Problem.builder();
      HTTPStatusCode lStatusCode = HTTPStatusCode.fromStatusCode(pResponse.getCode());
      lProblemBuilder.setStatus(lStatusCode.getCode());
      lProblemBuilder.setTitle(lStatusCode.getName());
      lProblemBuilder.setType(pRequestURI.toString());
      lProblemBuilder.setDetail(lResponseBody);
      lProblem = lProblemBuilder.build();
    }

    // Finally throw Problem
    throw new RESTProblemException(lProblem);
  }

  @Override
  protected RuntimeException processInternalServerError( URI pRequestURI, Exception pException,
      String pContext ) {
    TRACE.error(pContext, pException);
    Builder lProblemBuilder = Problem.builder();
    HTTPStatusCode lStatusCode = HTTPStatusCode.INTERNAL_SERVER_ERROR;
    lProblemBuilder.setStatus(lStatusCode.getCode());
    lProblemBuilder.setTitle(lStatusCode.getName());
    lProblemBuilder.setType(pRequestURI.toString());
    lProblemBuilder.setDetail(pException.getMessage());
    return new RESTProblemException(lProblemBuilder.build());
  }

}
