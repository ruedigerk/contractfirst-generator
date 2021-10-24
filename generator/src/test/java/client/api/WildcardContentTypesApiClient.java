package client.api;

import client.model.Failure;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.RequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.ParameterLocation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains methods for all API operations tagged "WildcardContentTypes".
 */
public class WildcardContentTypesApiClient {
  private final RequestExecutor requestExecutor;

  private final ReturningAnyResponse returningAnyResponse;

  private final ReturningSuccessfulResponse returningSuccessfulResponse;

  public WildcardContentTypesApiClient(RequestExecutor requestExecutor) {
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
   * Test wildcard response content types.
   *
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  public GetWildcardContentTypesSuccessfulResponse getWildcardContentTypes(String testCaseSelector)
      throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException, ApiClientErrorWithFailureEntityException {

    GetWildcardContentTypesSuccessfulResponse response = returningSuccessfulResponse.getWildcardContentTypes(testCaseSelector);

    return response;
  }

  /**
   * Contains methods for all operations returning instances of operation specific success classes and throwing exceptions for unsuccessful status codes.
   */
  public class ReturningSuccessfulResponse {
    /**
     * Test wildcard response content types.
     *
     * @param testCaseSelector Used to select the desired behaviour of the server in the test.
     */
    public GetWildcardContentTypesSuccessfulResponse getWildcardContentTypes(
        String testCaseSelector) throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException, ApiClientErrorWithFailureEntityException {

      ApiResponse response = returningAnyResponse.getWildcardContentTypes(testCaseSelector);

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithFailureEntityException(response);
      }

      return new GetWildcardContentTypesSuccessfulResponse(response);
    }
  }

  /**
   * Contains methods for all operations returning instances of ApiResponse and not throwing exceptions for unsuccessful status codes.
   */
  public class ReturningAnyResponse {
    /**
     * Test wildcard response content types.
     *
     * @param testCaseSelector Used to select the desired behaviour of the server in the test.
     */
    public ApiResponse getWildcardContentTypes(String testCaseSelector) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/wildcardContentTypes", "GET");

      builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

      builder.response(StatusCode.of(200), "text/*", String.class);
      builder.response(StatusCode.of(200), "application/*", InputStream.class);
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      return requestExecutor.executeRequest(builder.build());
    }
  }

  /**
   * Represents a successful response of operation getWildcardContentTypes, i.e., the status code being in range 200 to 299.
   */
  public static class GetWildcardContentTypesSuccessfulResponse {
    private final ApiResponse response;

    public GetWildcardContentTypesSuccessfulResponse(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getApiResponse() {
      return response;
    }

    /**
     * Returns whether the response's status code is 200, while the response's entity is of type {@code String}.
     */
    public boolean isStatus200ReturningString() {
      return response.getStatusCode() == 200 && response.getEntityType() == String.class;
    }

    /**
     * Returns whether the response's status code is 200, while the response's entity is of type {@code InputStream}.
     */
    public boolean isStatus200ReturningInputStream() {
      return response.getStatusCode() == 200 && response.getEntityType() == InputStream.class;
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

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetWildcardContentTypesSuccessfulResponse o = (GetWildcardContentTypesSuccessfulResponse) other;
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
      return builder.replace(0, 2, "GetWildcardContentTypesSuccessfulResponse{").append('}').toString();
    }
  }
}
