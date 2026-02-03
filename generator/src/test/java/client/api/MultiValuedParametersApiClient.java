package client.api;

import client.model.Failure;
import client.model.SimpleEnum;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.ParameterLocation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Contains methods for all API operations tagged "MultiValuedParameters".
 */
public class MultiValuedParametersApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningResult returningResult;

  public MultiValuedParametersApiClient(ApiRequestExecutor requestExecutor) {
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
   * Test for handling of multi-valued parameters.
   *
   * @param testSelector Selects the assertions to perform on the server.
   * @param pathParam Multi-valued path parameter.
   * @param queryParam Multi-valued query parameter.
   * @param headerParam Multi-valued header parameter.
   * @param pathSetParam Multi-valued path parameter.
   * @param querySetParam Multi-valued query parameter.
   * @param headerSetParam Multi-valued header parameter.
   */
  public void multiValuedParametersTest(String testSelector, List<String> pathParam,
      List<SimpleEnum> queryParam, List<Integer> headerParam, Set<String> pathSetParam,
      Set<SimpleEnum> querySetParam, Set<Integer> headerSetParam) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    MultiValuedParametersTestResult result = returningResult.multiValuedParametersTest(testSelector, pathParam, queryParam, headerParam, pathSetParam, querySetParam, headerSetParam);

    if (!result.isSuccessful()) {
      throw new ApiClientErrorWithFailureEntityException(result.getResponse());
    }
  }

  /**
   * Contains methods returning operation specific result classes, allowing inspection of the operations' responses.
   */
  public class ReturningResult {
    /**
     * Test for handling of multi-valued parameters.
     *
     * @param testSelector Selects the assertions to perform on the server.
     * @param pathParam Multi-valued path parameter.
     * @param queryParam Multi-valued query parameter.
     * @param headerParam Multi-valued header parameter.
     * @param pathSetParam Multi-valued path parameter.
     * @param querySetParam Multi-valued query parameter.
     * @param headerSetParam Multi-valued header parameter.
     */
    public MultiValuedParametersTestResult multiValuedParametersTest(String testSelector,
        List<String> pathParam, List<SimpleEnum> queryParam, List<Integer> headerParam,
        Set<String> pathSetParam, Set<SimpleEnum> querySetParam, Set<Integer> headerSetParam) throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/multiValuedParametersTest/{pathParam}/{pathSetParam}", "POST");

      builder.parameter("testSelector", ParameterLocation.QUERY, true, testSelector);
      builder.parameter("pathParam", ParameterLocation.PATH, true, pathParam);
      builder.parameter("queryParam", ParameterLocation.QUERY, false, queryParam);
      builder.parameter("headerParam", ParameterLocation.HEADER, false, headerParam);
      builder.parameter("pathSetParam", ParameterLocation.PATH, true, pathSetParam);
      builder.parameter("querySetParam", ParameterLocation.QUERY, false, querySetParam);
      builder.parameter("headerSetParam", ParameterLocation.HEADER, false, headerSetParam);

      builder.response(StatusCode.of(204));
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new MultiValuedParametersTestResult(response);
    }
  }

  /**
   * Represents the result of calling operation multiValuedParametersTest.
   */
  public static class MultiValuedParametersTestResult {
    private final ApiResponse response;

    public MultiValuedParametersTestResult(ApiResponse response) {
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
     * Returns whether the response's status code is 204, while the response has no entity.
     */
    public boolean isStatus204WithoutEntity() {
      return response.getStatusCode() == 204;
    }

    /**
     * Returns whether the response's entity is of type {@code Failure}.
     */
    public boolean isReturningFailure() {
      return response.getEntityType() == Failure.class;
    }

    /**
     * Returns the response's entity of type {@code Failure}.
     */
    public Failure getEntity() {
      return (Failure) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      MultiValuedParametersTestResult o = (MultiValuedParametersTestResult) other;
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
      return builder.replace(0, 2, "MultiValuedParametersTestResult{").append('}').toString();
    }
  }
}
