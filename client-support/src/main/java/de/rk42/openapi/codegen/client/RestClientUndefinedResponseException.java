package de.rk42.openapi.codegen.client;

/**
 * Thrown when the server responds with a response that is not defined in the contract for the operation.
 */
public class RestClientUndefinedResponseException extends RestClientException {

  private final UndefinedResponse response;

  public RestClientUndefinedResponseException(String msg, UndefinedResponse response) {
    super(msg);
    this.response = response;
  }

  public RestClientUndefinedResponseException(String message, UndefinedResponse response, Throwable cause) {
    super(message, cause);
    this.response = response;
  }

  public UndefinedResponse getResponse() {
    return response;
  }
}
