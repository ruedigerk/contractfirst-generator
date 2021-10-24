package combinations_client.api;

import combinations_client.model.SevereCtcError;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientErrorWithEntityException;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;

/**
 * Exception for errors where the API returned an entity of type {@code SevereCtcError}.
 */
public class ApiClientErrorWithSevereCtcErrorEntityException extends ApiClientErrorWithEntityException {
  public ApiClientErrorWithSevereCtcErrorEntityException(ApiResponse response) {
    super(response);
  }

  @Override
  public SevereCtcError getEntity() {
    return (SevereCtcError) super.getEntity();
  }
}
