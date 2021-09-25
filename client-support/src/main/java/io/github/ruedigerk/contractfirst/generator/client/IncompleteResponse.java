package io.github.ruedigerk.contractfirst.generator.client;

/**
 * Represents an incomplete response, where an IOException occurred reading the response body.
 */
public class IncompleteResponse {

  private final RequestDescription request;
  private final int statusCode;
  private final String httpStatusMessage;
  private final String contentType;

  public IncompleteResponse(RequestDescription request, int statusCode, String httpStatusMessage, String contentType) {
    this.request = request;
    this.statusCode = statusCode;
    this.httpStatusMessage = httpStatusMessage;
    this.contentType = contentType;
  }

  public RequestDescription getRequest() {
    return request;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getHttpStatusMessage() {
    return httpStatusMessage;
  }

  public String getContentType() {
    return contentType;
  }

  @Override
  public String toString() {
    return statusCode + " " + httpStatusMessage + ", Content-Type=" + contentType;
  }
}
