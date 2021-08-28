package de.rk42.openapi.codegen.client;

/**
 * Abstract superclass of all exceptions that are thrown by the REST client.
 */
public abstract class RestClientException extends RuntimeException {

  protected RestClientException(String message) {
    super(message);
  }

  protected RestClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
