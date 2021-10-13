package client.api;

import client.model.Failure;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientEntityException;
import io.github.ruedigerk.contractfirst.generator.client.DefinedResponse;

/**
 * Exception for the error entity of type Failure.
 */
public class RestClientFailureEntityException extends ApiClientEntityException {
  public RestClientFailureEntityException(DefinedResponse response) {
    super(response);
  }

  @Override
  public Failure getEntity() {
    return (Failure) super.getEntity();
  }
}
