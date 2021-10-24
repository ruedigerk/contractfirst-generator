package io.github.ruedigerk.contractfirst.generator.client;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * Represents a response that is conforming to the API specification of the operation called, regardless of the response being successful.
 */
public class ApiResponse {

  private final ApiRequest request;
  private final int statusCode;
  private final String httpStatusMessage;
  private final String contentType;
  private final List<Header> headers;
  private final Object entity;
  private final Type entityType;

  public ApiResponse(
      ApiRequest request,
      int statusCode,
      String httpStatusMessage,
      String contentType,
      List<Header> headers,
      Object entity,
      Type entityType
  ) {
    this.request = request;
    this.statusCode = statusCode;
    this.httpStatusMessage = httpStatusMessage;
    this.contentType = contentType;
    this.entityType = entityType;
    this.headers = headers;
    this.entity = entity;
  }

  /**
   * Returns the request for this response.
   */
  public ApiRequest getRequest() {
    return request;
  }

  /**
   * Return the HTTP staus code of this response.
   */
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * Returns the HTTP status message of the response sent by the server.
   */
  public String getHttpStatusMessage() {
    return httpStatusMessage;
  }

  /**
   * Returns the Content-Type header of the response or null, if none was sent.
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Returns the headers of the response.
   */
  public List<Header> getHeaders() {
    return headers;
  }

  /**
   * Returns the parsed response entity or null, when the response body is empty.
   */
  public Object getEntity() {
    return entity;
  }

  /**
   * Returns the Java type of the response entity, or {@code Void.TYPE} when the response contains no entity. This is usually the class of the entity, e.g.,
   * BigDecimal.class, but can also be a TypeToken, when the type is generic, e.g., {@code new TypeToken<List<BigDecimal>>(){}.getType()}.
   */
  public Type getEntityType() {
    return entityType;
  }

  /**
   * Returns whether the response has a status code in the range 200 to 299.
   */
  public boolean isSuccessful() {
    return getStatusCode() >= 200 && getStatusCode() < 300;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApiResponse that = (ApiResponse) o;
    return statusCode == that.statusCode
        && Objects.equals(request, that.request)
        && Objects.equals(httpStatusMessage, that.httpStatusMessage)
        && Objects.equals(contentType, that.contentType)
        && Objects.equals(entityType, that.entityType)
        && Objects.equals(headers, that.headers)
        && Objects.equals(entity, that.entity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(request, statusCode, httpStatusMessage, contentType, entityType, headers, entity);
  }

  @Override
  public String toString() {
    return "ApiResponse(" +
        "request=" + request +
        ",status=" + statusCode + " " + httpStatusMessage +
        ",contentType='" + contentType + '\'' +
        ",headers=" + headers +
        ",javaType=" + entityType +
        ",entity=" + entity +
        ')';
  }
}
