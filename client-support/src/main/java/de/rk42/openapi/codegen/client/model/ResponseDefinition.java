package de.rk42.openapi.codegen.client.model;

import java.lang.reflect.Type;

/**
 * Represents the definition of a response to an API operation. Every combination of status code and content type for an API operation is represented by a
 * different ResponseDefinition.
 */
public class ResponseDefinition {

  private final StatusCode statusCode;
  private final String contentType;
  private final Type javaType;

  public ResponseDefinition(StatusCode statusCode, String contentType, Type javaType) {
    this.statusCode = statusCode;
    this.contentType = contentType;
    this.javaType = javaType;
  }

  public boolean hasNoContent() {
    return contentType == null;
  }

  public StatusCode getStatusCode() {
    return statusCode;
  }

  public String getContentType() {
    return contentType;
  }

  public Type getJavaType() {
    return javaType;
  }
}
