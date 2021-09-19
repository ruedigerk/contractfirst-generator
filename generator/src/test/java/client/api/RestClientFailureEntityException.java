package client.api;

import client.model.Failure;
import org.contractfirst.generator.client.ApiClientEntityException;

/**
 * Exception for the error entity of type Failure.
 */
public class RestClientFailureEntityException extends ApiClientEntityException {
  public RestClientFailureEntityException(int httpStatusCode, Failure entity) {
    super(httpStatusCode, entity);
  }

  @Override
  public Failure getEntity() {
    return (Failure) super.getEntity();
  }
}
