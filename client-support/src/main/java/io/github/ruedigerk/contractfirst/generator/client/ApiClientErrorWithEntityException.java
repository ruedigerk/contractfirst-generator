package io.github.ruedigerk.contractfirst.generator.client;

/**
 * Abstract superclass of all exceptions thrown by the API client that indicate an error defined in the contract. Assumes that the error is completely described
 * by the status code and the entity returned by the server.
 */
public abstract class ApiClientErrorWithEntityException extends ApiClientException {

  private final ApiResponse response;

  protected ApiClientErrorWithEntityException(ApiResponse response) {
    super(toMessage(response));
    this.response = response;
  }

  private static String toMessage(ApiResponse response) {
    ApiRequest request = response.getRequest();
    return "Error with entity for " + request.getMethod() + " " + request.getUrl() +
        ", status=" + response.getStatusCode() + " " + response.getHttpStatusMessage() +
        ", entity=" + response.getEntity();
  }

  /**
   * The response sent by the server. 
   */
  public ApiResponse getResponse() {
    return response;
  }

  /**
   * The HTTP status code sent by the server.
   */
  public int getStatusCode() {
    return response.getStatusCode();
  }

  /**
   * The entity sent by the server.
   */
  public Object getEntity() {
    return response.getEntity();
  }
}
