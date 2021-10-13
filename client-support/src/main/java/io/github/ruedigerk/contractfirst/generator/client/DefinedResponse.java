package io.github.ruedigerk.contractfirst.generator.client;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * Represents a response that is in accordance with the API specification of the REST-API.
 */
public class DefinedResponse implements GenericResponse {

  private final RequestDescription request;
  private final int statusCode;
  private final String httpStatusMessage;
  private final String contentType;
  private final Type javaType;
  private final List<Header> headers;
  private final Object entity;

  public DefinedResponse(
      RequestDescription request,
      int statusCode,
      String httpStatusMessage,
      String contentType,
      Type javaType,
      List<Header> headers,
      Object entity
  ) {
    this.request = request;
    this.statusCode = statusCode;
    this.httpStatusMessage = httpStatusMessage;
    this.contentType = contentType;
    this.javaType = javaType;
    this.headers = headers;
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

  @Override
  public List<Header> getHeaders() {
    return headers;
  }

  /**
   * Returns the Java type of the response entity or null, when the response contains no entity. This is usually the class of the entity, e.g. BigDecimal.class,
   * but can also be a TypeToken, when the type is generic, e.g., {@code TypeToken<List<BigDecimal>>}.
   */
  public Type getJavaType() {
    return javaType;
  }

  /**
   * Returns the parsed response entity sent by the server or null, when the response is empty.
   */
  public Object getEntity() {
    return entity;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DefinedResponse that = (DefinedResponse) o;
    return statusCode == that.statusCode
        && Objects.equals(request, that.request)
        && Objects.equals(httpStatusMessage, that.httpStatusMessage)
        && Objects.equals(contentType, that.contentType)
        && Objects.equals(javaType, that.javaType)
        && Objects.equals(headers, that.headers)
        && Objects.equals(entity, that.entity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(request, statusCode, httpStatusMessage, contentType, javaType, headers, entity);
  }

  @Override
  public String toString() {
    return "DefinedResponse(" +
        "request=" + request +
        ",status=" + statusCode + " " + httpStatusMessage + 
        ",contentType='" + contentType + '\'' +
        ",headers=" + headers +
        ",javaType=" + javaType +
        ",entity=" + entity +
        ')';
  }
}
