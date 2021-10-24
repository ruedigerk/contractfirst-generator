package io.github.ruedigerk.contractfirst.generator.client;

/**
 * Thrown by the API client when the server sends a response that is not conforming to the API specification of the operation called.
 */
public class ApiClientIncompatibleResponseException extends ApiClientException {

  private final IncompatibleResponse response;

  public ApiClientIncompatibleResponseException(String msg, IncompatibleResponse response) {
    super(toMessage(msg, response));
    this.response = response;
  }

  public ApiClientIncompatibleResponseException(String msg, IncompatibleResponse response, Throwable cause) {
    super(toMessage(msg, response), cause);
    this.response = response;
  }

  private static String toMessage(String msg, IncompatibleResponse response) {
    ApiRequest request = response.getRequest();
    return msg + ", for " + request.getMethod() + " " + request.getUrl() + 
        ", status=" + response.getStatusCode() + " " + response.getHttpStatusMessage() + 
        ", content-type=" + response.getContentType() + 
        ", body=" + response.getBody();
  }

  /**
   * The response sent by the server that was not conforming to the API specification.
   */
  public IncompatibleResponse getResponse() {
    return response;
  }
}
