package de.rk42.openapi.codegen.client;

/**
 * Represents all responses of the client, either ones defined in the contract or ones undefined therein.
 */
public interface GenericResponse {

  /**
   * Returns whether this response conforms to the responses defined in the contract for the REST-API.
   */
  boolean isExpectedResponse();

  /**
   * Returns this response as an DefinedResponse or throws a RestClientUndefinedResponseException when this Response is not an DefinedResponse.
   *
   * @return this cast to DefinedResponse
   * @throws RestClientUndefinedResponseException when this does not represent an DefinedResponse.
   */
  DefinedResponse asExpectedResponse() throws RestClientUndefinedResponseException;

  CorrespondingRequest getRequest();

  int getStatusCode();

  String getHttpStatusMessage();

  String getContentType();

  default boolean isSuccessful() {
    return getStatusCode() >= 200 && getStatusCode() < 300;
  }
}
