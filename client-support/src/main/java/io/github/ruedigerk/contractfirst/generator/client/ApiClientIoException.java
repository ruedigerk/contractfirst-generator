package io.github.ruedigerk.contractfirst.generator.client;

import java.io.IOException;
import java.util.Optional;

/**
 * Thrown when an IOException occurs during a request of the API client.
 */
public class ApiClientIoException extends ApiClientException {

  private final ApiRequest request;
  private final IncompleteResponse incompleteResponse;

  public ApiClientIoException(String message, ApiRequest request, IOException cause) {
    super(toMessage(message, request), cause);
    this.request = request;
    incompleteResponse = null;
  }

  public ApiClientIoException(String message, IncompleteResponse incompleteResponse, IOException cause) {
    super(toMessage(message, incompleteResponse), cause);
    request = incompleteResponse.getRequest();
    this.incompleteResponse = incompleteResponse;
  }

  private static String toMessage(String message, ApiRequest request) {
    return message + ", for: " + request.getMethod() + " " + request.getUrl();
  }

  private static String toMessage(String message, IncompleteResponse incompleteResponse) {
    ApiRequest request = incompleteResponse.getRequest();
    return message + 
        ", for " + request.getMethod() + " " + request.getUrl() + 
        ", status=" + incompleteResponse.getStatusCode() + " " + incompleteResponse.getHttpStatusMessage();
  }

  /**
   * Returns a description of the request that lead to the IOException.
   */
  public ApiRequest getRequest() {
    return request;
  }

  /**
   * Return the incomplete response from the server, if available.
   */
  public Optional<IncompleteResponse> getIncompleteResponse() {
    return Optional.ofNullable(incompleteResponse);
  }
}
