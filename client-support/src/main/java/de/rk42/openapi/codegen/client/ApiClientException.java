package de.rk42.openapi.codegen.client;

/**
 * Abstract superclass of all exceptions that are thrown by the REST client.
 */
public abstract class ApiClientException extends RuntimeException {

  protected ApiClientException(String message) {
    super(message);
  }

  protected ApiClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
