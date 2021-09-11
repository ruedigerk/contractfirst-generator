package client.api;

import client.model.Error;
import org.contractfirst.generator.client.ApiClientEntityException;

/**
 * Exception for the error entity of type Error.
 */
public class RestClientErrorEntityException extends ApiClientEntityException {
  public RestClientErrorEntityException(int httpStatusCode, Error entity) {
    super(httpStatusCode, entity);
  }

  @Override
  public Error getEntity() {
    return (Error) super.getEntity();
  }
}
