package org.contractfirst.generator.client;

/**
 * Thrown when a required request parameter, or a required request body is missing.
 */
public class ApiClientValidationException extends ApiClientException {

  public ApiClientValidationException(String message) {
    super(message);
  }
}
