package io.github.ruedigerk.contractfirst.generator.client;

/**
 * Thrown when the server responds with a response that is not defined in the contract for the operation.
 */
public class ApiClientUndefinedResponseException extends ApiClientException {

  private final UndefinedResponse response;

  public ApiClientUndefinedResponseException(String msg, UndefinedResponse response) {
    super(msg);
    this.response = response;
  }

  public ApiClientUndefinedResponseException(String message, UndefinedResponse response, Throwable cause) {
    super(message, cause);
    this.response = response;
  }

  public UndefinedResponse getResponse() {
    return response;
  }
}
