package parameters_client.api;

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
 * Contains methods for all API operations tagged "EquallyNamedParameters".
 */
public class EquallyNamedParametersApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningResult returningResult;

  public EquallyNamedParametersApiClient(ApiRequestExecutor requestExecutor) {
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
   * Test for multiple equally named parameters.
   */
  public void getEquallyNamedParameters(String theParameterInHeader, String theParameterInPath,
      String theParameterInQuery, String theParameterInBody, String other) throws
      ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

    GetEquallyNamedParametersResult result = returningResult.getEquallyNamedParameters(theParameterInHeader, theParameterInPath, theParameterInQuery, theParameterInBody, other);
  }

  /**
   * Contains methods returning operation specific result classes, allowing inspection of the operations' responses.
   */
  public class ReturningResult {
    /**
     * Test for multiple equally named parameters.
     */
    public GetEquallyNamedParametersResult getEquallyNamedParameters(String theParameterInHeader,
        String theParameterInPath, String theParameterInQuery, String theParameterInBody,
        String other) throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/getEquallyNamedParameters/{theParameter}", "GET");

      builder.parameter("theParameter", ParameterLocation.HEADER, false, theParameterInHeader);
      builder.parameter("theParameter", ParameterLocation.PATH, true, theParameterInPath);
      builder.parameter("theParameter", ParameterLocation.QUERY, false, theParameterInQuery);
      builder.requestBodyPart("theParameter", theParameterInBody);
      builder.requestBodyPart("other", other);
      builder.multipartRequestBody("application/x-www-form-urlencoded");

      builder.response(StatusCode.of(204));

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new GetEquallyNamedParametersResult(response);
    }
  }

  /**
   * Represents the result of calling operation getEquallyNamedParameters.
   */
  public static class GetEquallyNamedParametersResult {
    private final ApiResponse response;

    public GetEquallyNamedParametersResult(ApiResponse response) {
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

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetEquallyNamedParametersResult o = (GetEquallyNamedParametersResult) other;
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
      return builder.replace(0, 2, "GetEquallyNamedParametersResult{").append('}').toString();
    }
  }
}
