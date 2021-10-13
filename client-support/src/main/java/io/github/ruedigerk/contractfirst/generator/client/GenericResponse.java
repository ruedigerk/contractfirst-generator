package io.github.ruedigerk.contractfirst.generator.client;

import java.util.List;

/**
 * Represents all responses of the client, regardless of them being in accordance with the API specification.
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

  /**
   * Returns a description of the request for this response. 
   */
  RequestDescription getRequest();

  /**
   * Return the HTTP staus code of this response.
   */
  int getStatusCode();

  /**
   * Returns the HTTP status message of the response sent by the server. 
   */
  String getHttpStatusMessage();

  /**
   * Returns the Content-Type header of the response or null, if none was sent.
   */
  String getContentType();

  /**
   * Returns the headers of the response.
   */
  List<Header> getHeaders();

  /**
   * Returns whether the request was successful, e.i. if the status code of the response is in the range 200 to 299.
   */
  default boolean isSuccessful() {
    return getStatusCode() >= 200 && getStatusCode() < 300;
  }
}
