package client.api;

import client.model.Failure;
import client.model.Manual;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.ParameterLocation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains methods for all API operations tagged "MultipleContentTypes".
 */
public class MultipleContentTypesApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningAnyResponse returningAnyResponse;

  private final ReturningSuccessfulResult returningSuccessfulResult;

  public MultipleContentTypesApiClient(ApiRequestExecutor requestExecutor) {
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
   * Test case for multiple response content types with different schemas.
   *
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  public GetManualSuccessfulResult getManual(String testCaseSelector) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    GetManualSuccessfulResult response = returningSuccessfulResult.getManual(testCaseSelector);

    return response;
  }

  /**
   * Contains methods for all operations returning instances of operation specific success classes and throwing exceptions for unsuccessful status codes.
   */
  public class ReturningSuccessfulResult {
    /**
     * Test case for multiple response content types with different schemas.
     *
     * @param testCaseSelector Used to select the desired behaviour of the server in the test.
     */
    public GetManualSuccessfulResult getManual(String testCaseSelector) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException,
        ApiClientErrorWithFailureEntityException {

      ApiResponse response = returningAnyResponse.getManual(testCaseSelector);

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithFailureEntityException(response);
      }

      return new GetManualSuccessfulResult(response);
    }
  }

  /**
   * Contains methods for all operations returning instances of ApiResponse and not throwing exceptions for unsuccessful status codes.
   */
  public class ReturningAnyResponse {
    /**
     * Test case for multiple response content types with different schemas.
     *
     * @param testCaseSelector Used to select the desired behaviour of the server in the test.
     */
    public ApiResponse getManual(String testCaseSelector) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/manuals", "GET");

      builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

      builder.response(StatusCode.of(200), "application/json", Manual.class);
      builder.response(StatusCode.of(200), "application/pdf", InputStream.class);
      builder.response(StatusCode.of(202), "text/plain", String.class);
      builder.response(StatusCode.of(204));
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      return requestExecutor.executeRequest(builder.build());
    }
  }

  /**
   * Represents a successful response of operation getManual, i.e., the status code being in range 200 to 299.
   */
  public static class GetManualSuccessfulResult {
    private final ApiResponse response;

    public GetManualSuccessfulResult(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getResponse() {
      return response;
    }

    /**
     * Returns whether the response's status code is 200, while the response's entity is of type {@code Manual}.
     */
    public boolean isStatus200ReturningManual() {
      return response.getStatusCode() == 200 && response.getEntityType() == Manual.class;
    }

    /**
     * Returns whether the response's status code is 200, while the response's entity is of type {@code InputStream}.
     */
    public boolean isStatus200ReturningInputStream() {
      return response.getStatusCode() == 200 && response.getEntityType() == InputStream.class;
    }

    /**
     * Returns whether the response's status code is 202, while the response's entity is of type {@code String}.
     */
    public boolean isStatus202ReturningString() {
      return response.getStatusCode() == 202 && response.getEntityType() == String.class;
    }

    /**
     * Returns whether the response's status code is 204, while the response has no entity.
     */
    public boolean isStatus204WithoutEntity() {
      return response.getStatusCode() == 204;
    }

    /**
     * Returns the response's entity wrapped in {@code java.lang.Optional.of()} if it is of type {@code Manual}. Otherwise, returns {@code Optional.empty()}.
     */
    public Optional<Manual> getEntityIfManual() {
      return Optional.ofNullable(getEntityAsManual());
    }

    /**
     * Returns the response's entity if it is of type {@code Manual}. Otherwise, returns null.
     */
    public Manual getEntityAsManual() {
      if (response.getEntityType() == Manual.class) {
        return (Manual) response.getEntity();
      } else {
        return null;
      }
    }

    /**
     * Returns the response's entity wrapped in {@code java.lang.Optional.of()} if it is of type {@code InputStream}. Otherwise, returns {@code Optional.empty()}.
     */
    public Optional<InputStream> getEntityIfInputStream() {
      return Optional.ofNullable(getEntityAsInputStream());
    }

    /**
     * Returns the response's entity if it is of type {@code InputStream}. Otherwise, returns null.
     */
    public InputStream getEntityAsInputStream() {
      if (response.getEntityType() == InputStream.class) {
        return (InputStream) response.getEntity();
      } else {
        return null;
      }
    }

    /**
     * Returns the response's entity wrapped in {@code java.lang.Optional.of()} if it is of type {@code String}. Otherwise, returns {@code Optional.empty()}.
     */
    public Optional<String> getEntityIfString() {
      return Optional.ofNullable(getEntityAsString());
    }

    /**
     * Returns the response's entity if it is of type {@code String}. Otherwise, returns null.
     */
    public String getEntityAsString() {
      if (response.getEntityType() == String.class) {
        return (String) response.getEntity();
      } else {
        return null;
      }
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetManualSuccessfulResult o = (GetManualSuccessfulResult) other;
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
      return builder.replace(0, 2, "GetManualSuccessfulResult{").append('}').toString();
    }
  }
}
