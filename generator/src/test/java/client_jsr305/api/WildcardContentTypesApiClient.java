package client_jsr305.api;

import client_jsr305.model.Failure;
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
 * Contains methods for all API operations tagged "WildcardContentTypes".
 */
public class WildcardContentTypesApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningResult returningResult;

  public WildcardContentTypesApiClient(ApiRequestExecutor requestExecutor) {
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
   * Test wildcard response content types.
   *
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  public GetWildcardContentTypesResult getWildcardContentTypes(String testCaseSelector) throws
      ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    GetWildcardContentTypesResult result = returningResult.getWildcardContentTypes(testCaseSelector);

    if (!result.isSuccessful()) {
      throw new ApiClientErrorWithFailureEntityException(result.getResponse());
    }

    return result;
  }

  /**
   * Contains methods returning operation specific result classes, allowing inspection of the operations' responses.
   */
  public class ReturningResult {
    /**
     * Test wildcard response content types.
     *
     * @param testCaseSelector Used to select the desired behaviour of the server in the test.
     */
    public GetWildcardContentTypesResult getWildcardContentTypes(String testCaseSelector) throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/wildcardContentTypes", "GET");

      builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

      builder.response(StatusCode.of(200), "text/*", String.class);
      builder.response(StatusCode.of(200), "application/*", InputStream.class);
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new GetWildcardContentTypesResult(response);
    }
  }

  /**
   * Represents the result of calling operation getWildcardContentTypes.
   */
  public static class GetWildcardContentTypesResult {
    private final ApiResponse response;

    public GetWildcardContentTypesResult(ApiResponse response) {
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
     * Returns whether the response's entity is of type {@code Failure}.
     */
    public boolean isReturningFailure() {
      return response.getEntityType() == Failure.class;
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
      GetWildcardContentTypesResult o = (GetWildcardContentTypesResult) other;
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
      return builder.replace(0, 2, "GetWildcardContentTypesResult{").append('}').toString();
    }
  }
}
