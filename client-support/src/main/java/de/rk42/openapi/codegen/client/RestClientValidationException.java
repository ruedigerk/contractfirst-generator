package de.rk42.openapi.codegen.client;

/**
 * Thrown when a required request parameter, or a required request body is missing.
 */
public class RestClientValidationException extends RestClientException {

  public RestClientValidationException(String message) {
    super(message);
  }
}
