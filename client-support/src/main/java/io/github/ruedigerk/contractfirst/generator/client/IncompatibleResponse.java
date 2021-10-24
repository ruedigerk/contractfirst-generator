package io.github.ruedigerk.contractfirst.generator.client;

import java.util.List;
import java.util.Objects;

/**
 * Represents a response that is not conforming to the API specification of the operation called. Also used for responses that the API client can not process,
 * e.g., when the server returns an entity in an unsupported format, like XML.
 */
public class IncompatibleResponse {

  private final ApiRequest request;
  private final int statusCode;
  private final String httpStatusMessage;
  private final String contentType;
  private final List<Header> headers;
  private final String body;

  public IncompatibleResponse(
      ApiRequest request,
      int statusCode,
      String httpStatusMessage,
      String contentType,
      List<Header> headers,
      String body
  ) {
    this.request = request;
    this.statusCode = statusCode;
    this.httpStatusMessage = httpStatusMessage;
    this.contentType = contentType;
    this.body = body;
    this.headers = headers;
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
   * The content of the response as a String.
   */
  public String getBody() {
    return body;
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
    IncompatibleResponse that = (IncompatibleResponse) o;
    return statusCode == that.statusCode
        && Objects.equals(request, that.request)
        && Objects.equals(httpStatusMessage, that.httpStatusMessage)
        && Objects.equals(contentType, that.contentType)
        && Objects.equals(headers, that.headers)
        && Objects.equals(body, that.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(request, statusCode, httpStatusMessage, contentType, headers, body);
  }

  @Override
  public String toString() {
    return "IncompatibleResponse(" +
        "request=" + request +
        ",status=" + statusCode + " " + httpStatusMessage +
        ",contentType='" + contentType + '\'' +
        ",headers=" + headers +
        ",body='" + body + '\'' +
        ')';
  }
}
