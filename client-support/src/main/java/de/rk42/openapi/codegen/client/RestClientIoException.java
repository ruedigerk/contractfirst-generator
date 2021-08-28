package de.rk42.openapi.codegen.client;

import java.io.IOException;
import java.util.Optional;

/**
 * Thrown when an IOException occurs during a request of the REST-API client.
 */
public class RestClientIoException extends RestClientException {

  private final CorrespondingRequest request;
  private final IncompleteResponse incompleteResponse;

  public RestClientIoException(String message, CorrespondingRequest request, IOException cause) {
    super(toMessage(message, request, cause), cause);
    this.request = request;
    this.incompleteResponse = null;
  }

  public RestClientIoException(String message, IOException cause) {
    super(message, cause);
    this.request = null;
    this.incompleteResponse = null;
  }

  private static String toMessage(String message, CorrespondingRequest correspondingRequest, IOException cause) {
    return message + ", request: " + correspondingRequest + ": " + cause;
  }

  public RestClientIoException(String message, IncompleteResponse incompleteResponse, IOException cause) {
    super(toMessage(message, incompleteResponse, cause), cause);
    this.request = incompleteResponse.getRequest();
    this.incompleteResponse = incompleteResponse;
  }

  private static String toMessage(String message, IncompleteResponse incompleteResponse, IOException cause) {
    return message + ", request: " + incompleteResponse.getRequest() + ", response: " + incompleteResponse + ": " + cause;
  }

  public Optional<CorrespondingRequest> getRequestData() {
    return Optional.ofNullable(request);
  }

  public Optional<IncompleteResponse> getIncompleteResponse() {
    return Optional.ofNullable(incompleteResponse);
  }
}
