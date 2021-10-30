package client.api;

import client.model.Failure;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.ParameterLocation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Contains methods for all API operations tagged "BigDecimals".
 */
public class BigDecimalsApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningAnyResponse returningAnyResponse;

  private final ReturningSuccessfulResult returningSuccessfulResult;

  public BigDecimalsApiClient(ApiRequestExecutor requestExecutor) {
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
   * Test serialization of schema type number as BigDecimal.
   *
   * @param decimalNumber Test BigDecimal
   */
  public BigDecimal getNumber(BigDecimal decimalNumber) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    GetNumberSuccessfulResult response = returningSuccessfulResult.getNumber(decimalNumber);

    return response.getEntity();
  }

  /**
   * Contains methods for all operations returning instances of operation specific success classes and throwing exceptions for unsuccessful status codes.
   */
  public class ReturningSuccessfulResult {
    /**
     * Test serialization of schema type number as BigDecimal.
     *
     * @param decimalNumber Test BigDecimal
     */
    public GetNumberSuccessfulResult getNumber(BigDecimal decimalNumber) throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException,
        ApiClientErrorWithFailureEntityException {

      ApiResponse response = returningAnyResponse.getNumber(decimalNumber);

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithFailureEntityException(response);
      }

      return new GetNumberSuccessfulResult(response);
    }
  }

  /**
   * Contains methods for all operations returning instances of ApiResponse and not throwing exceptions for unsuccessful status codes.
   */
  public class ReturningAnyResponse {
    /**
     * Test serialization of schema type number as BigDecimal.
     *
     * @param decimalNumber Test BigDecimal
     */
    public ApiResponse getNumber(BigDecimal decimalNumber) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/bigDecimals", "GET");

      builder.parameter("decimalNumber", ParameterLocation.QUERY, true, decimalNumber);

      builder.response(StatusCode.of(200), "application/json", BigDecimal.class);
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      return requestExecutor.executeRequest(builder.build());
    }
  }

  /**
   * Represents a successful response of operation getNumber, i.e., the status code being in range 200 to 299.
   */
  public static class GetNumberSuccessfulResult {
    private final ApiResponse response;

    public GetNumberSuccessfulResult(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getResponse() {
      return response;
    }

    /**
     * Returns whether the response's status code is 200, while the response's entity is of type {@code BigDecimal}.
     */
    public boolean isStatus200ReturningBigDecimal() {
      return response.getStatusCode() == 200 && response.getEntityType() == BigDecimal.class;
    }

    /**
     * Returns the response's entity of type {@code BigDecimal}.
     */
    public BigDecimal getEntity() {
      return (BigDecimal) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetNumberSuccessfulResult o = (GetNumberSuccessfulResult) other;
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
      return builder.replace(0, 2, "GetNumberSuccessfulResult{").append('}').toString();
    }
  }
}
