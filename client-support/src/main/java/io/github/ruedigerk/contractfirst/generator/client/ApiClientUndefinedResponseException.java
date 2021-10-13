package io.github.ruedigerk.contractfirst.generator.client;

/**
 * Thrown by the API client when the server sends a response that is not in accordance with the API specification for the operation called.
 */
public class ApiClientUndefinedResponseException extends ApiClientException {

  private final UndefinedResponse response;

  public ApiClientUndefinedResponseException(UndefinedResponse response) {
    super(toMessage(response), response.getCause());
    this.response = response;
  }

  private static String toMessage(UndefinedResponse response) {
    RequestDescription request = response.getRequest();
    return response.getReason() + 
        ", for " + request.getMethod() + " " + request.getUrl() + 
        ", status=" + response.getStatusCode() + " " + response.getHttpStatusMessage() + 
        ", content-type=" + response.getContentType() + 
        ", body=" + response.getBody();
  }

  /**
   * The response sent by the server which is not in accordance with the API specification.
   */
  public UndefinedResponse getResponse() {
    return response;
  }
}
