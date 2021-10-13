package io.github.ruedigerk.contractfirst.generator.client;

import java.util.List;
import java.util.Objects;

/**
 * Represents a response that is not in accordance with the API specification of the called operation. Also used for responses that the API client can not
 * process, e.g., when the server returns an entity in XML-encoding.
 */
public class UndefinedResponse implements GenericResponse {

  private final RequestDescription request;
  private final int statusCode;
  private final String httpStatusMessage;
  private final String contentType;
  private final String body;
  private final String reason;
  private final List<Header> headers;
  private final Throwable cause;

  public UndefinedResponse(
      RequestDescription request,
      int statusCode,
      String httpStatusMessage,
      String contentType,
      String body,
      String reason,
      List<Header> headers,
      Throwable cause
  ) {
    this.request = request;
    this.statusCode = statusCode;
    this.httpStatusMessage = httpStatusMessage;
    this.contentType = contentType;
    this.body = body;
    this.reason = reason;
    this.headers = headers;
    this.cause = cause;
  }

  @Override
  public boolean isExpectedResponse() {
    return false;
  }

  @Override
  public DefinedResponse asDefinedResponse() throws ApiClientUndefinedResponseException {
    throw new ApiClientUndefinedResponseException(this);
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

  /**
   * The content of the response as a String.
   */
  public String getBody() {
    return body;
  }

  /**
   * Description why the response is incompatible with the specification of the operation.
   */
  public String getReason() {
    return reason;
  }

  @Override
  public List<Header> getHeaders() {
    return headers;
  }

  /**
   * Returns the Throwable that occurred processing the response, e.g., a parsing Exception. Is null, when the response is unexpected due to another reason,
   * e.g., an unknown Content-Type.
   */
  public Throwable getCause() {
    return cause;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UndefinedResponse that = (UndefinedResponse) o;
    return statusCode == that.statusCode
        && Objects.equals(request, that.request)
        && Objects.equals(httpStatusMessage, that.httpStatusMessage)
        && Objects.equals(contentType, that.contentType)
        && Objects.equals(body, that.body)
        && Objects.equals(reason, that.reason)
        && Objects.equals(headers, that.headers)
        && Objects.equals(cause, that.cause);
  }

  @Override
  public int hashCode() {
    return Objects.hash(request, statusCode, httpStatusMessage, contentType, body, reason, headers, cause);
  }

  @Override
  public String toString() {
    return "UndefinedResponse(" +
        "reason='" + reason + '\'' +
        ",cause=" + cause +
        ",request=" + request +
        ",status=" + statusCode + " " + httpStatusMessage +
        ",contentType='" + contentType + '\'' +
        ",headers=" + headers +
        ",body='" + body + '\'' +
        ')';
  }
}
