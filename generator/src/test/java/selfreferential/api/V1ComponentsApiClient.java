package selfreferential.api;

import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.util.Objects;
import selfreferential.model.Model;

/**
 * Contains methods for all API operations tagged "V1Components".
 */
public class V1ComponentsApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningResult returningResult;

  public V1ComponentsApiClient(ApiRequestExecutor requestExecutor) {
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
   * Get component. Also, test escaping of JavaPoet placeholders: $L $1N $%.
   */
  public Model getComponent() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    GetComponentResult result = returningResult.getComponent();

    return result.getEntity();
  }

  /**
   * Contains methods returning operation specific result classes, allowing inspection of the operations' responses.
   */
  public class ReturningResult {
    /**
     * Get component. Also, test escaping of JavaPoet placeholders: $L $1N $%.
     */
    public GetComponentResult getComponent() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/v1/components", "GET");

      builder.response(StatusCode.of(200), "application/json", Model.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new GetComponentResult(response);
    }
  }

  /**
   * Represents the result of calling operation getComponent.
   */
  public static class GetComponentResult {
    private final ApiResponse response;

    public GetComponentResult(ApiResponse response) {
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
     * Returns whether the response's status code is 200, while the response's entity is of type {@code Model}.
     */
    public boolean isStatus200ReturningModel() {
      return response.getStatusCode() == 200 && response.getEntityType() == Model.class;
    }

    /**
     * Returns the response's entity of type {@code Model}.
     */
    public Model getEntity() {
      return (Model) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetComponentResult o = (GetComponentResult) other;
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
      return builder.replace(0, 2, "GetComponentResult{").append('}').toString();
    }
  }
}
