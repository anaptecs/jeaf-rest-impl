/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.rest.executor.impl.apache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

import com.anaptecs.jeaf.rest.executor.api.HttpMethod;
import com.anaptecs.jeaf.rest.executor.api.ObjectType;
import com.anaptecs.jeaf.rest.executor.api.ObjectType.GenericsObjectType;
import com.anaptecs.jeaf.rest.executor.api.ObjectType.SingleObjectType;
import com.anaptecs.jeaf.rest.executor.api.ObjectType.TypeReferenceObjectType;
import com.anaptecs.jeaf.rest.executor.api.RESTRequest;
import com.anaptecs.jeaf.rest.executor.api.RESTRequestExecutor;
import com.anaptecs.jeaf.rest.executor.impl.config.RESTClientConfiguration;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

/**
 * Class implements a {@link RESTRequestExecutor} base class that is based on Apache HTTP Client and Resilience4J
 * circuit breaker.
 * 
 * This implementation can be used to build up your own {@link RESTRequestExecutor}. Therefore you need to implement the
 * following aspects:
 * </ul>
 * <li>Loading of configuration for each service ({@link #getConfiguration(Class)}</li>
 * <li>Writing of request and response log. String representation is already provided ({@link #traceRequest(String)},
 * {@link #traceResponse(String)})</li>
 * <li>Conversion of exceptions into problems (){@link #processInternalServerError(URI, Exception, String)},
 * {@link #processErrorResponse(URI, CloseableHttpResponse)}</li>
 * <li>Lookup for matching JSON Object Mapper ({@link #getObjectMapper()})</li>
 * </ul>
 * 
 * @author JEAF Development Team
 */
public abstract class AbstractApacheHttpClientRESTRequestExecutorBase implements RESTRequestExecutor {
  /**
   * Map contains all http client instances that are already created. Implementation of this class assumes that there
   * are independent instances for each REST service.
   */
  private Map<Class<?>, CloseableHttpClient> httpClients = new HashMap<>();

  /**
   * Map contains all circuit breakers that are already created. Implementation of this class assumes that there are
   * independent instances for each REST service.
   */
  private Map<Class<?>, CircuitBreaker> circuitBreakers = new HashMap<>();

  /**
   * Method returns the JSON Object Mapper that should be used to serialize from Java to JSON and vice versa.
   * 
   * @return {@link ObjectMapper} Jackson object mapper that should be used. The method mmust not return null.
   */
  protected abstract ObjectMapper getObjectMapper( );

  /**
   * Method checks if request tracing for the REST service with the passed configuration is enabled.
   * 
   * @param pConfiguration Configuration of the REST service. The parameter is never null.
   * @return boolean Method returns true if request tracing is enabled and otherwise false.
   */
  protected abstract boolean isRequestTracingEnabled( RESTClientConfiguration pConfiguration );

  /**
   * Method checks if response tracing for the REST service with the passed configuration is enabled.
   * 
   * @param pConfiguration Configuration of the REST service. The parameter is never null.
   * @return boolean Method returns true if response tracing is enabled and otherwise false.
   */
  protected abstract boolean isResponseTracingEnabled( RESTClientConfiguration pConfiguration );

  /**
   * Method traces the passed request. The request itself was already converted into a String representation.
   * 
   * @param pRequestLog String representation of the request that should be traced. The parameter must not be null.
   */
  protected abstract void traceRequest( String pRequestLog );

  /**
   * Method traces the passed response. The response itself was already converted into a String representation.
   * 
   * @param pRequestLog String representation of the request that should be traced. The parameter must not be null.
   */
  protected abstract void traceResponse( String pResponseLog );

  /**
   * Method is called during request execution in case that an exception occurs. This method is responsible to trace the
   * passed exception together with the error message.
   * 
   * @param pErrorMessage Error message describing the occurred problem. The parameter must not be null.
   * @param pException Exception that occurred during request execution.
   */
  protected abstract void traceException( String pErrorMessage, Exception pException );

  /**
   * Method will be called in case that an REST resource returned an error. It is expected that this method creates a
   * description of the Problem and returns it as {@link RuntimeException} e.g. using Zalando Problem library.
   * 
   * @param pRequestURI URI of the request. The parameter must not be null.
   * @param pResponse Apache HTTP Client response that was received from the REST resource.
   * @return {@link RuntimeException} Runtime exception representing the occurred problem. The method must not return
   * null.
   */
  protected abstract RuntimeException processErrorResponse( URI pRequestURI, CloseableHttpResponse pResponse );

  /**
   * This method will be called in case that an internal error occurs during REST request execution. It is expected that
   * this method creates a description of the Problem and returns it as {@link RuntimeException} e.g. using Zalando
   * Problem library.
   * 
   * @param pRequestURI URI of the request. The parameter must not be null.
   * @param pException Exception that occurred during internal processing. The parameter must not be null.
   * @param pContext Description of the context in which the problem occurred. The context may be used for tracing e.g.
   * The parameter must not be null.
   * @return {@link RuntimeException} Runtime exception representing the occurred problem. The method must not return
   * null.
   */
  protected abstract RuntimeException processInternalServerError( URI pRequestURI, Exception pException,
      String pContext );

  /**
   * Method returns the REST client configuration that belongs to the passed service. From where the configuration is
   * read is up to the implementation ;-)
   * 
   * @param pServiceClass Class representing the service whose configuration should be returned.
   * @return {@link RESTClientConfiguration} Configuration object for the passed service. The method must not return
   * null.
   */
  protected abstract RESTClientConfiguration getConfiguration( Class<?> pServiceClass );

  @Override
  public final void executeNoResultRequest( RESTRequest pRequest, int pSuccessfulStatusCode ) {
    // Execute request.
    Class<?> lServiceClass = pRequest.getServiceClass();
    RESTClientConfiguration lConfiguration = this.getConfiguration(lServiceClass);
    ClassicHttpRequest lHttpClientRequest = this.createHttpClientRequest(pRequest, lConfiguration);
    HttpContext lHttpContext = this.createHttpContext(pRequest, lConfiguration);
    this.executeRequest(lServiceClass, lHttpClientRequest, lHttpContext, pSuccessfulStatusCode, null);
  }

  @Override
  public <T> T executeSingleObjectResultRequest( RESTRequest pRequest, int pSuccessfulStatusCode,
      ObjectType pObjectType ) {

    // Create matching response type as defined by the passed parameters
    JavaType lResponseType = this.getJavaType(pObjectType);

    // Execute request and return result.
    Class<?> lServiceClass = pRequest.getServiceClass();
    RESTClientConfiguration lConfiguration = this.getConfiguration(lServiceClass);
    ClassicHttpRequest lHttpClientRequest = this.createHttpClientRequest(pRequest, lConfiguration);
    HttpContext lHttpContext = this.createHttpContext(pRequest, lConfiguration);
    return this.executeRequest(lServiceClass, lHttpClientRequest, lHttpContext, pSuccessfulStatusCode, lResponseType);
  }

  @Override
  public <T> T executeCollectionResultRequest( RESTRequest pRequest, int pSuccessfulStatusCode,
      @SuppressWarnings("rawtypes")
      Class<? extends Collection> pCollectionClass, ObjectType pObjectType ) {

    // Create matching response type for collections as defined by the passed parameters
    JavaType lResponseType = this.getObjectMapper().getTypeFactory().constructCollectionType(pCollectionClass,
        this.getJavaType(pObjectType));

    // Execute request and return result.
    Class<?> lServiceClass = pRequest.getServiceClass();
    RESTClientConfiguration lConfiguration = this.getConfiguration(lServiceClass);
    ClassicHttpRequest lHttpClientRequest = this.createHttpClientRequest(pRequest, lConfiguration);
    HttpContext lHttpContext = this.createHttpContext(pRequest, lConfiguration);
    return this.executeRequest(lServiceClass, lHttpClientRequest, lHttpContext, pSuccessfulStatusCode, lResponseType);
  }

  /**
   * Method executes the passed HTTP request using the configured HTTP client and circuit breaker.
   * 
   * @param pRequest Request that should b executed. The parameter must not be null.
   * @param pSuccessfulStatusCode Status code that defines that the call was successful.
   * @param pResponseType Object describing the response type of the call. The parameter may be null in case that
   * operation does not return any content e.g. void operations.
   * @return T Object of defined response type. If the called REST resource returns no content as response then null
   * will be returned.
   */
  private <T> T executeRequest( Class<?> pServiceClass, ClassicHttpRequest pRequest, HttpContext pHttpContext,
      int pSuccessfulStatusCode, JavaType pResponseType ) {
    // Try to execute call to REST resource
    CloseableHttpResponse lResponse = null;
    URI lRequestURI = null;

    // Resolve http client, circuit breaker and configuration for service that will be called.
    CloseableHttpClient lHttpClient = this.getHttpClient(pServiceClass);
    CircuitBreaker lCircuitBreaker = this.getCircuitBreaker(pServiceClass);
    RESTClientConfiguration lConfiguration = this.getConfiguration(pServiceClass);

    try {
      // For reasons of proper error handling we need to find out the request URI.
      lRequestURI = pRequest.getUri();
      // Trace request. Actually request logging is only done if log level is set to DEBUG.
      this.traceRequest(pRequest, lConfiguration);
      // Decorate call to proxy with circuit breaker.
      Callable<CloseableHttpResponse> lCallable =
          CircuitBreaker.decorateCallable(lCircuitBreaker, new Callable<CloseableHttpResponse>() {
            @Override
            public CloseableHttpResponse call( ) throws IOException {
              return lHttpClient.execute(pRequest, pHttpContext);
            }
          });
      // Execute request to REST resource
      lResponse = lCircuitBreaker.executeCallable(lCallable);
      // If call was successful then we have to convert response into real objects.
      int lStatusCode = lResponse.getCode();
      if (lStatusCode == pSuccessfulStatusCode) {
        T lResultObject;
        HttpEntity lEntity = lResponse.getEntity();

        // Check if there is a response. Due to behavior of Spring that in case of stream does not return the actual
        // size we just check for zero length.
        if (pResponseType != null && lEntity.getContentLength() != 0) {
          // Check if response logging is active.
          if (this.isResponseTracingEnabled(lConfiguration)) {
            String lResponseBody = this.getContent(lEntity.getContent());
            this.traceResponse(lResponse, lRequestURI, lResponseBody, lConfiguration);
            lResultObject = this.getObjectMapper().readValue(lResponseBody, pResponseType);
          }
          else {
            lResultObject = this.getObjectMapper().readValue(lEntity.getContent(), pResponseType);
          }
        }
        else {
          lResultObject = null;
        }
        return lResultObject;
      }
      // Error when trying to execute REST call.
      else {
        // If server provided problem JSON then we will return this information.
        throw this.processErrorResponse(lRequestURI, lResponse);
      }
    }
    //
    // In all the cases below we will use status code 500 INTERNAL_SERVER error as it is not the clients fault that the
    // request could not be processed
    //
    // Thanks to circuit breaker interface definition of Resilience4J we have to handle RuntimeExceptions
    catch (RuntimeException e) {
      throw e;
    }
    // IOException can result from communication or serialization problems. Thanks to circuit breaker interface
    // definition of Resilience4J we also have to catch java.lang.Exception ;-(
    catch (Exception e) {
      throw this.processInternalServerError(lRequestURI, e,
          "Exception occurred when try to call REST Service " + pRequest.toString());
    }
    // No matter what happened we have at least close the http response if possible.
    finally {
      if (lResponse != null) {
        try {
          lResponse.close();
        }
        catch (IOException e) {
          this.traceException("Unable to close http client response from REST Service "
              + this.getConfiguration(pServiceClass).getExternalServiceURL(), e);
        }
      }
    }
  }

  private ClassicHttpRequest createHttpClientRequest( RESTRequest pRequest, RESTClientConfiguration pConfiguration ) {
    URI lRequestURI = null;
    try {
      // Create builder for POST request
      ClassicRequestBuilder lRequestBuilder = this.createRequestBuilder(pRequest.getHttpMethod());

      // Build URI of request
      StringBuilder lURIBuilder = new StringBuilder();
      lURIBuilder.append(pConfiguration.getExternalServiceURL());
      lURIBuilder.append(pRequest.getPath());
      lRequestBuilder.setUri(lURIBuilder.toString());
      lRequestURI = lRequestBuilder.getUri();

      // Set query params
      for (Entry<String, List<String>> lNextQueryParam : pRequest.getQueryParameters().entrySet()) {
        // As the might be multiple values for one query param we have to lopp over all passed values.
        for (String lNextValue : lNextQueryParam.getValue()) {
          lRequestBuilder.addParameter(lNextQueryParam.getKey(), lNextValue);
        }
      }

      // Set HTTP header(s)
      for (Entry<String, List<String>> lNextHeader : pRequest.getHeaderFields().entrySet()) {
        String lHeaderName = lNextHeader.getKey();
        List<String> lHeaderValues = lNextHeader.getValue();
        if (lHeaderValues != null && lHeaderValues.size() > 0) {
          for (String lNextValue : lHeaderValues) {
            lRequestBuilder.addHeader(lHeaderName, lNextValue);
          }
        }
        else {
          lRequestBuilder.addHeader(lHeaderName, null);
        }
      }

      // Resolve content type and add it to http header as well
      ContentType lContentType = this.getHttpClientContentType(pRequest.getContentType());
      lRequestBuilder.setHeader(HttpHeaders.ACCEPT, lContentType.getMimeType());

      // Convert body object into body.
      if (org.apache.hc.core5.http.ContentType.APPLICATION_JSON.equals(lContentType)) {
        String lRequestBody = this.getObjectMapper().writeValueAsString(pRequest.getBody());
        lRequestBuilder.setEntity(lRequestBody, lContentType);
      }
      // Content type other than JSON is currently not supported.
      else {
        throw new IllegalArgumentException("Content type other than 'application/json' is currently not supported.");
      }

      // Create http client request and return it..
      return lRequestBuilder.build();
    }
    catch (IOException e) {
      throw this.processInternalServerError(lRequestURI, e, "Unable to serialize object(s) to JSON.");
    }
  }

  private HttpContext createHttpContext( RESTRequest pRequest, RESTClientConfiguration pConfiguration ) {
    // HttpContext is only need in case that request has cookies.
    Map<String, String> lCookies = pRequest.getCookies();
    HttpContext lLocalContext;
    if (lCookies.size() > 0) {
      // Handle cookie parameters
      BasicCookieStore lCookieStore = new BasicCookieStore();
      lLocalContext = new BasicHttpContext();
      lLocalContext.setAttribute(HttpClientContext.COOKIE_STORE, lCookieStore);

      // Process all passed cookies
      for (Entry<String, String> lNextCookie : lCookies.entrySet()) {
        BasicClientCookie lResellerCookie = new BasicClientCookie(lNextCookie.getKey(), lNextCookie.getValue());
        String lCookieDomain = pConfiguration.getCookieDomain();
        if (lCookieDomain != null) {
          lResellerCookie.setDomain(lCookieDomain);
        }
        String lCookiePath = pConfiguration.getCookiePath();
        if (lCookiePath != null) {
          lResellerCookie.setPath(lCookiePath);
        }
        lCookieStore.addCookie(lResellerCookie);
      }
    }
    else {
      lLocalContext = null;
    }
    return lLocalContext;
  }

  private ClassicRequestBuilder createRequestBuilder( HttpMethod pHttpMethod ) {
    ClassicRequestBuilder lBuilder;
    switch (pHttpMethod) {
      case DELETE:
        lBuilder = ClassicRequestBuilder.delete();
        break;

      case GET:
        lBuilder = ClassicRequestBuilder.get();
        break;

      case HEAD:
        lBuilder = ClassicRequestBuilder.head();
        break;

      case OPTIONS:
        lBuilder = ClassicRequestBuilder.options();
        break;

      case PATCH:
        lBuilder = ClassicRequestBuilder.patch();
        break;

      case POST:
        lBuilder = ClassicRequestBuilder.post();
        break;

      case PUT:
        lBuilder = ClassicRequestBuilder.put();
        break;

      case TRACE:
        lBuilder = ClassicRequestBuilder.trace();
        break;

      default:
        throw new IllegalArgumentException("Unsupported http method '" + pHttpMethod.name() + "'.");
    }
    return lBuilder;
  }

  private synchronized CloseableHttpClient getHttpClient( Class<?> pServiceClass ) {
    return httpClients.computeIfAbsent(pServiceClass, s -> this.createHttpClient(this.getConfiguration(pServiceClass)));
  }

  private CloseableHttpClient createHttpClient( RESTClientConfiguration pConfiguration ) {
    // Create connection manager that can be used by multiple threads in parallel.
    SocketConfig lSocketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
    Registry<ConnectionSocketFactory> lRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
        .register(URIScheme.HTTP.id, PlainConnectionSocketFactory.getSocketFactory())
        .register(URIScheme.HTTPS.id, SSLConnectionSocketFactory.getSocketFactory()).build();

    // Configure connection manager according to provided configuration parameters
    PoolingHttpClientConnectionManager lConnectionManager =
        new PoolingHttpClientConnectionManager(lRegistry, PoolConcurrencyPolicy.LAX, PoolReusePolicy.LIFO,
            TimeValue.ofMilliseconds(pConfiguration.getKeepAliveDuration()));
    lConnectionManager.setMaxTotal(pConfiguration.getMaxPoolSize());
    lConnectionManager.setDefaultMaxPerRoute(pConfiguration.getMaxIdleConnections());
    lConnectionManager
        .setValidateAfterInactivity(TimeValue.ofMilliseconds(pConfiguration.getValidateAfterInactivityDuration()));
    lConnectionManager.setDefaultSocketConfig(lSocketConfig);

    // Create pool for http connections that is used for this proxy.
    HttpClientBuilder lBuilder = HttpClientBuilder.create();
    lBuilder.setConnectionManager(lConnectionManager);

    // Configure request specific parameters.
    RequestConfig.Builder lConfigBuilder = RequestConfig.custom();
    lConfigBuilder.setConnectionKeepAlive(TimeValue.ofMilliseconds(pConfiguration.getKeepAliveDuration()));
    lConfigBuilder.setConnectTimeout(Timeout.ofMilliseconds(pConfiguration.getConnectTimeout()));
    lConfigBuilder.setConnectionRequestTimeout(Timeout.ofMilliseconds(pConfiguration.getConnectionRequestTimeout()));
    lConfigBuilder.setResponseTimeout(Timeout.ofMilliseconds(pConfiguration.getResponseTimeout()));
    lConfigBuilder.setExpectContinueEnabled(true);
    lBuilder.setDefaultRequestConfig(lConfigBuilder.build());

    // Define retry behavior.
    lBuilder.setRetryStrategy(new DefaultHttpRequestRetryStrategy(pConfiguration.getMaxRetries(),
        TimeValue.ofMilliseconds(pConfiguration.getRetryInterval())));

    // Finally we have to create the http client.
    return lBuilder.build();
  }

  private synchronized CircuitBreaker getCircuitBreaker( Class<?> pServiceClass ) {
    return circuitBreakers.computeIfAbsent(pServiceClass, s -> this.createCircuitBreaker(pServiceClass));
  }

  /**
   * Method is called after service startup and performs initialization of resilience4J circuit breaker.
   */
  private CircuitBreaker createCircuitBreaker( Class<?> pServiceClass ) {
    // Create circuit break configuration for target.
    RESTClientConfiguration lConfiguration = this.getConfiguration(pServiceClass);
    CircuitBreakerConfig.Builder lConfigBuilder = CircuitBreakerConfig.custom();
    lConfigBuilder.failureRateThreshold(lConfiguration.getFailureRateThreshold());
    lConfigBuilder.waitDurationInOpenState(Duration.ofMillis(lConfiguration.getDurationInOpenState()));
    lConfigBuilder.slowCallDurationThreshold(Duration.ofMillis(lConfiguration.getSlowRequestDuration()));
    lConfigBuilder.slowCallRateThreshold(lConfiguration.getSlowRequestRateThreshold());
    lConfigBuilder.permittedNumberOfCallsInHalfOpenState(lConfiguration.getPermittedCallsInHalfOpenState());
    lConfigBuilder.slidingWindowSize(lConfiguration.getSlidingWindowSizeSeconds());
    lConfigBuilder.recordExceptions(IOException.class, RuntimeException.class);
    CircuitBreakerRegistry lCircuitBreakerRegistry = CircuitBreakerRegistry.of(lConfigBuilder.build());
    return lCircuitBreakerRegistry.circuitBreaker(pServiceClass.getSimpleName() + " Circuit Breaker");
  }

  private ContentType getHttpClientContentType( com.anaptecs.jeaf.rest.executor.api.ContentType pContentType ) {
    ContentType lContentType;
    switch (pContentType) {
      case JSON:
        lContentType = ContentType.APPLICATION_JSON;
        break;

      case XML:
        lContentType = ContentType.APPLICATION_XML;
        break;

      default:
        throw new IllegalArgumentException("Unexpected content type '" + pContentType.name() + "'.");
    }
    return lContentType;
  }

  private void traceRequest( ClassicHttpRequest pRequest, RESTClientConfiguration pConfiguration )
    throws URISyntaxException, IOException {
    if (this.isRequestTracingEnabled(pConfiguration)) {
      StringBuilder lBuilder = new StringBuilder();
      // Add first line with http method and URL
      lBuilder.append("Request: (");
      lBuilder.append(pRequest.getMethod());
      lBuilder.append(") ");
      lBuilder.append(pRequest.getUri());
      lBuilder.append(System.lineSeparator());
      // Add header fields
      List<String> lSensitiveHeaderNames = pConfiguration.getSensitiveHeaderNames();
      lBuilder.append("Request Headers: ");
      for (Header lNextHeader : pRequest.getHeaders()) {
        // For security reasons sensitive headers have to be filtered out from tracing.
        if (lSensitiveHeaderNames.contains(lNextHeader.getName().toLowerCase()) == false) {
          lBuilder.append(lNextHeader.getName());
          lBuilder.append("='");
          lBuilder.append(lNextHeader.getValue());
          lBuilder.append("' ");
        }
      }
      lBuilder.append(System.lineSeparator());
      // Add body if request has one.
      HttpEntity lEntity = pRequest.getEntity();
      if (lEntity != null && lEntity.getContentLength() > 0) {
        lBuilder.append("Body: ");
        lBuilder.append(this.getContent(lEntity.getContent()));
      }
      // Finally really log the request.
      this.traceRequest(lBuilder.toString());
    }
  }

  protected final void traceResponse( CloseableHttpResponse pResponse, URI pRequestURI, String pBody,
      RESTClientConfiguration pConfiguration )
    throws URISyntaxException, IOException {
    if (this.isResponseTracingEnabled(pConfiguration)) {
      StringBuilder lBuilder = new StringBuilder();
      // Add first line with http method and URL
      lBuilder.append("Response: ");
      lBuilder.append(pRequestURI);
      lBuilder.append(System.lineSeparator());
      // Add http status code.
      lBuilder.append("Status Code: ");
      lBuilder.append(pResponse.getCode());
      lBuilder.append(System.lineSeparator());
      // Add header fields
      List<String> lSensitiveHeaderNames = pConfiguration.getSensitiveHeaderNames();
      lBuilder.append("Response Headers: ");
      for (Header lNextHeader : pResponse.getHeaders()) {
        lBuilder.append(lNextHeader.getName());
        lBuilder.append("='");
        // For security reasons sensitive headers must not be written to traces.
        if (lSensitiveHeaderNames.contains(lNextHeader.getName().toLowerCase()) == false) {
          lBuilder.append(lNextHeader.getValue());
        }
        // Write sensitive headers to log but without value e.g. '***'
        else {
          lBuilder.append("***");
        }
        lBuilder.append("'");
      }
      lBuilder.append(System.lineSeparator());
      // Add body if request has one.
      if (pBody != null) {
        lBuilder.append("Body: ");
        lBuilder.append(pBody);
      }
      // Finally really log the response.
      this.traceResponse(lBuilder.toString());
    }
  }

  /**
   * Method returns the content of the passed input stream.
   * 
   * @param pInputStream Stream to access the content. The parameter must not be null.
   * @return String Available content of the stream. The method never returns null.
   */
  protected final String getContent( InputStream pInputStream ) throws IOException {
    int lAvailableBytes = pInputStream.available();
    ByteArrayOutputStream lBytes = new ByteArrayOutputStream(lAvailableBytes);
    // Read as much bytes as possible into the buffer.
    int lBytesRead;
    byte[] lBuffer = new byte[128];
    while ((lBytesRead = pInputStream.read(lBuffer, 0, lBuffer.length)) != -1) {
      lBytes.write(lBuffer, 0, lBytesRead);
    }
    return new String(lBytes.toByteArray());
  }

  /**
   * Method uses the passed {@link ObjectType} to creating a Jackson type definition that can be used for
   * deserialization.
   * 
   * @param pObjectType Response type that should be used to create matching Jackson {@link JavaType}
   * @return {@link JavaType} JavaType that was created using the passed response type. The method never returns null.
   */
  private JavaType getJavaType( ObjectType pObjectType ) {
    TypeFactory lTypeFactory = this.getObjectMapper().getTypeFactory();

    JavaType lJavaType;
    if (pObjectType instanceof SingleObjectType) {
      lJavaType = lTypeFactory.constructType(((SingleObjectType) pObjectType).getObjectType());
    }
    else if (pObjectType instanceof GenericsObjectType) {
      GenericsObjectType lGenericsObjectType = (GenericsObjectType) pObjectType;
      lJavaType = lTypeFactory.constructParametricType(lGenericsObjectType.getGenericType(),
          lGenericsObjectType.getParameterType());
    }
    else if (pObjectType instanceof TypeReferenceObjectType) {
      TypeReferenceObjectType lTypeReferenceObjectType = (TypeReferenceObjectType) pObjectType;
      lJavaType = lTypeFactory.constructType(lTypeReferenceObjectType.getTypeReference().getType());
    }
    else {
      throw new IllegalArgumentException("Unexpected ObjectType implementation " + pObjectType.getClass().getName());
    }
    return lJavaType;
  }
}
