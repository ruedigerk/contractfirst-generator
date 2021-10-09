package io.github.ruedigerk.contractfirst.generator.client;

/**
 * Represents all responses of the client, either ones defined in the contract or ones undefined therein.
 */
public interface GenericResponse {

  /**
   * Returns whether this response conforms to the responses defined in the contract for the REST-API.
   */
  boolean isExpectedResponse();

  /**
   * Returns this response as an DefinedResponse, i.e., a response that is defined in the contract for this operation.
   * Throws a ApiClientUndefinedResponseException when this Response is not an DefinedResponse.
   *
   * @return this cast to DefinedResponse
   * @throws ApiClientUndefinedResponseException when this does not represent an DefinedResponse.
   */
  DefinedResponse asDefinedResponse() throws ApiClientUndefinedResponseException;

  RequestDescription getRequest();

  int getStatusCode();

  String getHttpStatusMessage();

  String getContentType();

  default boolean isSuccessful() {
    return getStatusCode() >= 200 && getStatusCode() < 300;
  }
}