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

/**
 * Contains methods for all API operations tagged "ResponseVariants".
 */
public class ResponseVariantsApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningAnyResponse returningAnyResponse;

  private final ReturningSuccessfulResult returningSuccessfulResult;

  public ResponseVariantsApiClient(ApiRequestExecutor requestExecutor) {
    this.requestExecutor = requestExecutor;
    this.returningAnyResponse = new ReturningAnyResponse();
    this.returningSuccessfulResult = new ReturningSuccessfulResult();
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
  public ReturningSuccessfulResult returningSuccessfulResult() {
    return returningSuccessfulResult;
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

    CreateItemSuccessfulResult response = returningSuccessfulResult.createItem(systemId, dryRun, partNumber, testCaseSelector, requestBody);

    return response.getEntity();
  }

  /**
   * Contains methods for all operations returning instances of operation specific success classes and throwing exceptions for unsuccessful status codes.
   */
  public class ReturningSuccessfulResult {
    /**
     * Test for the various parameter locations and for serializing request and response body entities.
     *
     * @param systemId ID of the system to create the item in.
     * @param dryRun Do a dry run?
     * @param partNumber Optional part number
     * @param testCaseSelector Used to select the desired behaviour of the server in the test.
     */
    public CreateItemSuccessfulResult createItem(String systemId, Boolean dryRun, Long partNumber,
        String testCaseSelector, Item requestBody) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException,
        ApiClientErrorWithFailureEntityException {

      ApiResponse response = returningAnyResponse.createItem(systemId, dryRun, partNumber, testCaseSelector, requestBody);

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithFailureEntityException(response);
      }

      return new CreateItemSuccessfulResult(response);
    }
  }

  /**
   * Contains methods for all operations returning instances of ApiResponse and not throwing exceptions for unsuccessful status codes.
   */
  public class ReturningAnyResponse {
    /**
     * Test for the various parameter locations and for serializing request and response body entities.
     *
     * @param systemId ID of the system to create the item in.
     * @param dryRun Do a dry run?
     * @param partNumber Optional part number
     * @param testCaseSelector Used to select the desired behaviour of the server in the test.
     */
    public ApiResponse createItem(String systemId, Boolean dryRun, Long partNumber,
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

      return requestExecutor.executeRequest(builder.build());
    }
  }

  /**
   * Represents a successful response of operation createItem, i.e., the status code being in range 200 to 299.
   */
  public static class CreateItemSuccessfulResult {
    private final ApiResponse response;

    public CreateItemSuccessfulResult(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getResponse() {
      return response;
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
     * Returns the response's entity of type {@code Item}.
     */
    public Item getEntity() {
      return (Item) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      CreateItemSuccessfulResult o = (CreateItemSuccessfulResult) other;
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
      return builder.replace(0, 2, "CreateItemSuccessfulResult{").append('}').toString();
    }
  }
}
