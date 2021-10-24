package combinations_client.api;

import combinations_client.model.CtcError;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientErrorWithEntityException;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;

/**
 * Exception for errors where the API returned an entity of type {@code CtcError}.
 */
public class ApiClientErrorWithCtcErrorEntityException extends ApiClientErrorWithEntityException {
  public ApiClientErrorWithCtcErrorEntityException(ApiResponse response) {
    super(response);
  }

  @Override
  public CtcError getEntity() {
    return (CtcError) super.getEntity();
  }
}
