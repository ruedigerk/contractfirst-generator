package de.rk42.openapi.codegen.client;

import java.io.IOException;
import java.util.Optional;

/**
 * Thrown when an IOException occurs during a request of the REST-API client.
 */
public class ApiClientIoException extends ApiClientException {

  private final RequestDescription request;
  private final IncompleteResponse incompleteResponse;

  public ApiClientIoException(String message, RequestDescription request, IOException cause) {
    super(toMessage(message, request, cause), cause);
    this.request = request;
    this.incompleteResponse = null;
  }

  public ApiClientIoException(String message, IOException cause) {
    super(message, cause);
    this.request = null;
    this.incompleteResponse = null;
  }

  private static String toMessage(String message, RequestDescription requestDescription, IOException cause) {
    return message + ", request: " + requestDescription + ": " + cause;
  }

  public ApiClientIoException(String message, IncompleteResponse incompleteResponse, IOException cause) {
    super(toMessage(message, incompleteResponse, cause), cause);
    this.request = incompleteResponse.getRequest();
    this.incompleteResponse = incompleteResponse;
  }

  private static String toMessage(String message, IncompleteResponse incompleteResponse, IOException cause) {
    return message + ", request: " + incompleteResponse.getRequest() + ", response: " + incompleteResponse + ": " + cause;
  }

  public Optional<RequestDescription> getRequestData() {
    return Optional.ofNullable(request);
  }

  public Optional<IncompleteResponse> getIncompleteResponse() {
    return Optional.ofNullable(incompleteResponse);
  }
}
