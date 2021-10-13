package io.github.ruedigerk.contractfirst.generator.client;

import java.util.List;
import java.util.Objects;

/**
 * Represents an incomplete response, where an IOException occurred reading the response body.
 */
public class IncompleteResponse {

  private final RequestDescription request;
  private final int statusCode;
  private final String httpStatusMessage;
  private final String contentType;
  private final List<Header> headers;

  public IncompleteResponse(
      RequestDescription request,
      int statusCode,
      String httpStatusMessage,
      String contentType,
      List<Header> headers
  ) {
    this.request = request;
    this.statusCode = statusCode;
    this.httpStatusMessage = httpStatusMessage;
    this.contentType = contentType;
    this.headers = headers;
  }

  /**
   * Returns a description of the request for this response.
   */
  public RequestDescription getRequest() {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IncompleteResponse that = (IncompleteResponse) o;
    return statusCode == that.statusCode
        && Objects.equals(request, that.request)
        && Objects.equals(httpStatusMessage, that.httpStatusMessage)
        && Objects.equals(contentType, that.contentType)
        && Objects.equals(headers, that.headers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(request, statusCode, httpStatusMessage, contentType, headers);
  }

  @Override
  public String toString() {
    return "IncompleteResponse(" +
        "request=" + request +
        ",status=" + statusCode + " " + httpStatusMessage +
        ",contentType='" + contentType + '\'' +
        ",headers=" + headers +
        ')';
  }
}
