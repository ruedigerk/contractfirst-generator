package client.resources;

import client.model.Error;
import de.rk42.openapi.codegen.client.RestClientEntityException;

/**
 * Exception for the error entity of type Error.
 */
public class RestClientErrorEntityException extends RestClientEntityException {
  public RestClientErrorEntityException(int httpStatusCode, Error entity) {
    super(httpStatusCode, entity);
  }

  @Override
  public Error getEntity() {
    return (Error) super.getEntity();
  }
}
