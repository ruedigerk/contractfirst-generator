package de.rk42.openapi.codegen.client;

import java.lang.reflect.Type;

/**
 * Represents a response that is defined in the contract of the REST-API.
 */
public class DefinedResponse implements GenericResponse {

  private final RequestDescription request;
  private final int statusCode;
  private final String httpStatusMessage;
  private final String contentType;
  private final Type javaType;
  private final Object entity;

  public DefinedResponse(RequestDescription request, int statusCode, String httpStatusMessage, String contentType, Type javaType, Object entity) {
    this.request = request;
    this.statusCode = statusCode;
    this.httpStatusMessage = httpStatusMessage;
    this.contentType = contentType;
    this.javaType = javaType;
    this.entity = entity;
  }

  @Override
  public boolean isExpectedResponse() {
    return true;
  }

  @Override
  public DefinedResponse asDefinedResponse() {
    return this;
  }

  @Override
  public RequestDescription getRequest() {
    return request;
  }

  @Override
  public int getStatusCode() {
    return statusCode;
  }

  @Override
  public String getHttpStatusMessage() {
    return httpStatusMessage;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  public Type getJavaType() {
    return javaType;
  }

  public Object getEntity() {
    return entity;
  }
}
