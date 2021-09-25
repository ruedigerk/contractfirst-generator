package io.github.ruedigerk.contractfirst.generator.client;

/**
 * Represents a response that is not defined in the contract of the REST-API, or a response that the client can not process as defined by the contract,
 * e.g., when the server returns an entity in a non-JSON-encoding.
 */
public class UndefinedResponse implements GenericResponse {

  private final RequestDescription request;
  private final int statusCode;
  private final String httpStatusMessage;
  private final String contentType;
  private final String bodyContent;
  private final String reason;
  private final Throwable cause;

  public UndefinedResponse(
      RequestDescription request,
      int statusCode,
      String httpStatusMessage,
      String contentType,
      String bodyContent,
      String reason,
      Throwable cause
  ) {
    this.request = request;
    this.statusCode = statusCode;
    this.httpStatusMessage = httpStatusMessage;
    this.contentType = contentType;
    this.bodyContent = bodyContent;
    this.reason = reason;
    this.cause = cause;
  }

  @Override
  public boolean isExpectedResponse() {
    return false;
  }

  @Override
  public DefinedResponse asDefinedResponse() throws ApiClientUndefinedResponseException {
    if (cause == null) {
      throw new ApiClientUndefinedResponseException(reason, this);
    } else {
      throw new ApiClientUndefinedResponseException(reason, this, cause);
    }
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

  public String getBodyContent() {
    return bodyContent;
  }

  public String getReason() {
    return reason;
  }

  public Throwable getCause() {
    return cause;
  }
}
