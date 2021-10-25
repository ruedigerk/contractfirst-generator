package selfreferential.api;

import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.RequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.util.Objects;
import selfreferential.model.Model;

/**
 * Contains methods for all API operations tagged "V1Components".
 */
public class V1ComponentsApiClient {
  private final RequestExecutor requestExecutor;

  private final ReturningAnyResponse returningAnyResponse;

  private final ReturningSuccessfulResponse returningSuccessfulResponse;

  public V1ComponentsApiClient(RequestExecutor requestExecutor) {
    this.requestExecutor = requestExecutor;
    this.returningAnyResponse = new ReturningAnyResponse();
    this.returningSuccessfulResponse = new ReturningSuccessfulResponse();
  }

  /**
   * Selects methods returning instances of ApiResponse and not throwing exceptions for unsuccessful status codes.
   */
  public ReturningAnyResponse returningAnyResponse() {
    return returningAnyResponse;
  }

  /**
   * Selects methods returning instances of operation specific success classes and throwing exceptions for unsuccessful status codes.
   */
  public ReturningSuccessfulResponse returningSuccessfulResponse() {
    return returningSuccessfulResponse;
  }

  /**
   * Get component. Also, test escaping of JavaPoet placeholders: $L $1N $%.
   */
  public Model getComponent() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    GetComponentSuccessfulResponse response = returningSuccessfulResponse.getComponent();

    return response.getEntity();
  }

  /**
   * Contains methods for all operations returning instances of operation specific success classes and throwing exceptions for unsuccessful status codes.
   */
  public class ReturningSuccessfulResponse {
    /**
     * Get component. Also, test escaping of JavaPoet placeholders: $L $1N $%.
     */
    public GetComponentSuccessfulResponse getComponent() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      ApiResponse response = returningAnyResponse.getComponent();

      return new GetComponentSuccessfulResponse(response);
    }
  }

  /**
   * Contains methods for all operations returning instances of ApiResponse and not throwing exceptions for unsuccessful status codes.
   */
  public class ReturningAnyResponse {
    /**
     * Get component. Also, test escaping of JavaPoet placeholders: $L $1N $%.
     */
    public ApiResponse getComponent() throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/v1/components", "GET");

      builder.response(StatusCode.of(200), "application/json", Model.class);

      return requestExecutor.executeRequest(builder.build());
    }
  }

  /**
   * Represents a successful response of operation getComponent, i.e., the status code being in range 200 to 299.
   */
  public static class GetComponentSuccessfulResponse {
    private final ApiResponse response;

    public GetComponentSuccessfulResponse(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getApiResponse() {
      return response;
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
      GetComponentSuccessfulResponse o = (GetComponentSuccessfulResponse) other;
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
      return builder.replace(0, 2, "GetComponentSuccessfulResponse{").append('}').toString();
    }
  }
}
