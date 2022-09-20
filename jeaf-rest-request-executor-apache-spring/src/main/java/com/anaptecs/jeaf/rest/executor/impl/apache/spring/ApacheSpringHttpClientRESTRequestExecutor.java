/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.rest.executor.impl.apache.spring;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.HttpStatusAdapter;

import com.anaptecs.jeaf.rest.executor.api.RESTRequestExecutor;
import com.anaptecs.jeaf.rest.executor.impl.apache.AbstractApacheHttpClientRESTRequestExecutorBase;
import com.anaptecs.jeaf.rest.executor.impl.apache.RESTClientConfiguration;
import com.anaptecs.jeaf.rest.executor.impl.apache.YAMLBasedRESTClientConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class implements a {@link RESTRequestExecutor} that is based on Apache HTTP Client and Resilience4J circuit breaker.
 * 
 * @author JEAF Development Team
 */
@Component
public class ApacheSpringHttpClientRESTRequestExecutor extends AbstractApacheHttpClientRESTRequestExecutorBase {
  /**
   * Logger for this class.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ApacheSpringHttpClientRESTRequestExecutor.class);

  /**
   * Map contains all loaded configurations. Configurations will only be loaded on demand.
   */
  private Map<Class<?>, RESTClientConfiguration> configurations = new HashMap<>();

  /**
   * Object mapper is used for serialization and deserialization of objects from Java to JSON and vice versa.
   */
  @Autowired
  private ObjectMapper objectMapper;

  @Override
  protected ObjectMapper getObjectMapper( ) {
    return objectMapper;
  }

  @Override
  protected boolean isRequestTracingEnabled( RESTClientConfiguration pConfiguration ) {
    return pConfiguration.traceRequests() && LOGGER.isInfoEnabled();
  }

  @Override
  protected boolean isResponseTracingEnabled( RESTClientConfiguration pConfiguration ) {
    return pConfiguration.traceResponses() && LOGGER.isInfoEnabled();
  }

  @Override
  protected void traceRequest( String pRequestLog ) {
    LOGGER.info(pRequestLog);
  }

  @Override
  protected void traceResponse( String pResponseLog ) {
    LOGGER.info(pResponseLog);
  }

  @Override
  protected void traceException( String pErrorMessage, Exception pException ) {
    LOGGER.error(pErrorMessage, pException);
  }

  @Override
  protected synchronized RESTClientConfiguration getConfiguration( Class<?> pServiceClass ) {
    return configurations.computeIfAbsent(pServiceClass, s -> this.loadConfiguration(pServiceClass));
  }

  private RESTClientConfiguration loadConfiguration( Class<?> pServiceClass ) {
    // We expect a YAML file with same name as service to be located in the class path.
    String lResourceFileName = pServiceClass.getSimpleName().toLowerCase() + ".yml";
    return YAMLBasedRESTClientConfiguration.loadConfiguration(lResourceFileName);
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
    ThrowableProblem lProblem;
    if (lResponseBody != null && ContentType.APPLICATION_PROBLEM_JSON.getMimeType().equals(lEntity.getContentType())) {
      try {
        lProblem = this.getObjectMapper().readValue(lResponseBody, ThrowableProblem.class);
      }
      catch (IOException e) {
        lProblem = null;
        throw this.processInternalServerError(pRequestURI, e, "Unable to process problem JSON");
      }
    }
    else {
      lProblem = null;
    }

    // For whatever reason we were not yet able to build a problem. So let's try to build it up from what we have.
    if (lProblem == null) {
      // Try to resolve some details.
      ProblemBuilder lProblemBuilder = Problem.builder();
      HttpStatusAdapter lStatus = new HttpStatusAdapter(HttpStatus.valueOf(pResponse.getCode()));
      lProblemBuilder.withStatus(lStatus);
      lProblemBuilder.withTitle(lStatus.getReasonPhrase());
      lProblemBuilder.withType(pRequestURI);
      lProblemBuilder.withDetail(lResponseBody);
      lProblem = lProblemBuilder.build();
    }

    // Finally throw Problem
    throw lProblem;
  }

  @Override
  protected RuntimeException processInternalServerError( URI pRequestURI, Exception pException,
      String pContext ) {
    LOGGER.error(pContext, pException);
    ProblemBuilder lProblemBuilder = Problem.builder();
    Status lStatus = Status.INTERNAL_SERVER_ERROR;
    lProblemBuilder.withStatus(lStatus);
    lProblemBuilder.withTitle(lStatus.getReasonPhrase());
    lProblemBuilder.withType(pRequestURI);
    lProblemBuilder.withDetail(pException.getMessage());
    throw lProblemBuilder.build();
  }

}
