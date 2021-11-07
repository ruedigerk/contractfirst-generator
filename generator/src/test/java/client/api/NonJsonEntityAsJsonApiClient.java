package client.api;

import client.model.Item;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.util.Objects;

/**
 * Contains methods for all API operations tagged "NonJsonEntityAsJson".
 */
public class NonJsonEntityAsJsonApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningResult returningResult;

  public NonJsonEntityAsJsonApiClient(ApiRequestExecutor requestExecutor) {
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
   * Test case for an operation that declares a non-JSON entity and the server nevertheless sending the entity as JSON.
   */
  public Item getNonJsonEntityAsJson() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    GetNonJsonEntityAsJsonResult result = returningResult.getNonJsonEntityAsJson();

    return result.getEntity();
  }

  /**
   * Contains methods returning operation specific result classes, allowing inspection of the operations' responses.
   */
  public class ReturningResult {
    /**
     * Test case for an operation that declares a non-JSON entity and the server nevertheless sending the entity as JSON.
     */
    public GetNonJsonEntityAsJsonResult getNonJsonEntityAsJson() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/nonJsonEntityAsJson", "GET");

      builder.response(StatusCode.of(200), "application/xml", Item.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new GetNonJsonEntityAsJsonResult(response);
    }
  }

  /**
   * Represents the result of calling operation getNonJsonEntityAsJson.
   */
  public static class GetNonJsonEntityAsJsonResult {
    private final ApiResponse response;

    public GetNonJsonEntityAsJsonResult(ApiResponse response) {
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
     * Returns the response's entity of type {@code Item}.
     */
    public Item getEntity() {
      return (Item) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetNonJsonEntityAsJsonResult o = (GetNonJsonEntityAsJsonResult) other;
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
      return builder.replace(0, 2, "GetNonJsonEntityAsJsonResult{").append('}').toString();
    }
  }
}
