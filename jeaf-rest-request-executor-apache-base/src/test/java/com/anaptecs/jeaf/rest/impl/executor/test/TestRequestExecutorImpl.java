/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.rest.impl.executor.test;

import java.net.URI;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;

import com.anaptecs.jeaf.rest.executor.impl.apache.AbstractApacheHttpClientRESTRequestExecutorBase;
import com.anaptecs.jeaf.rest.executor.impl.config.RESTClientConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestRequestExecutorImpl extends AbstractApacheHttpClientRESTRequestExecutorBase {
  public String tracedRequest;

  public String tracedResponse;

  public String tracedErrorMessage;

  public Exception tracedException;

  public boolean requestTracingEnabled;

  public boolean responseTracingEnabled;

  @Override
  protected ObjectMapper getObjectMapper( ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected boolean isRequestTracingEnabled( RESTClientConfiguration pConfiguration ) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  protected boolean isResponseTracingEnabled( RESTClientConfiguration pConfiguration ) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  protected void traceRequest( String pRequestLog ) {
    // TODO Auto-generated method stub

  }

  @Override
  protected void traceResponse( String pResponseLog ) {
    // TODO Auto-generated method stub

  }

  @Override
  protected void traceException( String pErrorMessage, Exception pException ) {
    // TODO Auto-generated method stub

  }

  @Override
  protected RuntimeException processErrorResponse( URI pRequestURI, CloseableHttpResponse pResponse ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected RuntimeException processInternalServerError( URI pRequestURI, Exception pException, String pContext ) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected RESTClientConfiguration getConfiguration( Class<?> pServiceClass ) {
    // TODO Auto-generated method stub
    return null;
  }

}
