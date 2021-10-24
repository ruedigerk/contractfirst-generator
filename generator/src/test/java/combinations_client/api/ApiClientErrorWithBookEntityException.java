package combinations_client.api;

import combinations_client.model.Book;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientErrorWithEntityException;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;

/**
 * Exception for errors where the API returned an entity of type {@code Book}.
 */
public class ApiClientErrorWithBookEntityException extends ApiClientErrorWithEntityException {
  public ApiClientErrorWithBookEntityException(ApiResponse response) {
    super(response);
  }

  @Override
  public Book getEntity() {
    return (Book) super.getEntity();
  }
}
