package client.api;

import client.model.Failure;
import client.model.Item;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.ParameterLocation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains methods for all API operations tagged "ResponseVariants".
 */
public class ResponseVariantsApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningResult returningResult;

  public ResponseVariantsApiClient(ApiRequestExecutor requestExecutor) {
    this.requestExecutor = requestExecutor;
    this.returningResult = new ReturningResult();
  }

  /**
   * Returns an API client with methods that return operation specific result classes, allowing inspection of the operations' responses.
   */
  public ReturningResult returningResult() {
    return returningResult;
  }

  /**
   * Test for the various parameter locations and for serializing request and response body entities.
   *
   * @param systemId ID of the system to create the item in.
   * @param dryRun Do a dry run?
   * @param partNumber Optional part number
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  public Item createItem(String systemId, Boolean dryRun, Long partNumber, String testCaseSelector,
      Item requestBody) throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException, ApiClientErrorWithFailureEntityException {

    CreateItemResult result = returningResult.createItem(systemId, dryRun, partNumber, testCaseSelector, requestBody);

    if (!result.isSuccessful()) {
      throw new ApiClientErrorWithFailureEntityException(result.getResponse());
    }

    return result.getEntityAsItem();
  }

  /**
   * Contains methods returning operation specific result classes, allowing inspection of the operations' responses.
   */
  public class ReturningResult {
    /**
     * Test for the various parameter locations and for serializing request and response body entities.
     *
     * @param systemId ID of the system to create the item in.
     * @param dryRun Do a dry run?
     * @param partNumber Optional part number
     * @param testCaseSelector Used to select the desired behaviour of the server in the test.
     */
    public CreateItemResult createItem(String systemId, Boolean dryRun, Long partNumber,
        String testCaseSelector, Item requestBody) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/{systemId}/components", "POST");

      builder.parameter("systemId", ParameterLocation.PATH, true, systemId);
      builder.parameter("dryRun", ParameterLocation.QUERY, false, dryRun);
      builder.parameter("partNumber", ParameterLocation.HEADER, false, partNumber);
      builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);
      builder.requestBody("application/json", true, requestBody);

      builder.response(StatusCode.of(200), "application/json", Item.class);
      builder.response(StatusCode.of(201));
      builder.response(StatusCode.of(204));
      builder.response(StatusCode.of(400), "application/json", Failure.class);
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new CreateItemResult(response);
    }
  }

  /**
   * Represents the result of calling operation createItem.
   */
  public static class CreateItemResult {
    private final ApiResponse response;

    public CreateItemResult(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getResponse() {
      return response;
    }

    /**
     * Returns the HTTP status code of the operation's response.
     */
    public int getStatus() {
      return response.getStatusCode();
    }

    /**
     * Returns whether the response has a status code in the range 200 to 299.
     */
    public boolean isSuccessful() {
      return response.isSuccessful();
    }

    /**
     * Returns whether the response's status code is 200, while the response's entity is of type {@code Item}.
     */
    public boolean isStatus200ReturningItem() {
      return response.getStatusCode() == 200 && response.getEntityType() == Item.class;
    }

    /**
     * Returns whether the response's status code is 201, while the response has no entity.
     */
    public boolean isStatus201WithoutEntity() {
      return response.getStatusCode() == 201;
    }

    /**
     * Returns whether the response's status code is 204, while the response has no entity.
     */
    public boolean isStatus204WithoutEntity() {
      return response.getStatusCode() == 204;
    }

    /**
     * Returns whether the response's status code is 400, while the response's entity is of type {@code Failure}.
     */
    public boolean isStatus400ReturningFailure() {
      return response.getStatusCode() == 400 && response.getEntityType() == Failure.class;
    }

    /**
     * Returns whether the response's entity is of type {@code Failure}.
     */
    public boolean isReturningFailure() {
      return response.getEntityType() == Failure.class;
    }

    /**
     * Returns the response's entity wrapped in {@code java.lang.Optional.of()} if it is of type {@code Item}. Otherwise, returns {@code Optional.empty()}.
     */
    public Optional<Item> getEntityIfItem() {
      return Optional.ofNullable(getEntityAsItem());
    }

    /**
     * Returns the response's entity if it is of type {@code Item}. Otherwise, returns null.
     */
    public Item getEntityAsItem() {
      if (response.getEntityType() == Item.class) {
        return (Item) response.getEntity();
      } else {
        return null;
      }
    }

    /**
     * Returns the response's entity wrapped in {@code java.lang.Optional.of()} if it is of type {@code Failure}. Otherwise, returns {@code Optional.empty()}.
     */
    public Optional<Failure> getEntityIfFailure() {
      return Optional.ofNullable(getEntityAsFailure());
    }

    /**
     * Returns the response's entity if it is of type {@code Failure}. Otherwise, returns null.
     */
    public Failure getEntityAsFailure() {
      if (response.getEntityType() == Failure.class) {
        return (Failure) response.getEntity();
      } else {
        return null;
      }
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      CreateItemResult o = (CreateItemResult) other;
      return Objects.equals(response, o.response);
    }

    @Override
    public int hashCode() {
      return Objects.hash(response);
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(", response=").append(response);
      return builder.replace(0, 2, "CreateItemResult{").append('}').toString();
    }
  }
}
