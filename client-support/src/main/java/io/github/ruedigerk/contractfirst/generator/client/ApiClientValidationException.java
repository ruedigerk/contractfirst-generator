package io.github.ruedigerk.contractfirst.generator.client;

/**
 * Thrown by the API client when a required request parameter or a required request body is missing.
 */
public class ApiClientValidationException extends ApiClientException {

  public ApiClientValidationException(String message) {
    super(message);
  }
}
