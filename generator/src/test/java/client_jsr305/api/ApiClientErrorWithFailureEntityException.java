package client_jsr305.api;

import client_jsr305.model.Failure;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientErrorWithEntityException;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;

/**
 * Exception for errors where the API returned an entity of type {@code Failure}.
 */
public class ApiClientErrorWithFailureEntityException extends ApiClientErrorWithEntityException {
  public ApiClientErrorWithFailureEntityException(ApiResponse response) {
    super(response);
  }

  @Override
  public Failure getEntity() {
    return (Failure) super.getEntity();
  }
}
