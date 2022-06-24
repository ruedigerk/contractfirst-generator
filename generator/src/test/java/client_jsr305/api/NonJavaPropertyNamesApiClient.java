package client_jsr305.api;

import client_jsr305.model.Failure;
import client_jsr305.model.ProblematicName;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains methods for all API operations tagged "NonJavaPropertyNames".
 */
public class NonJavaPropertyNamesApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningResult returningResult;

  public NonJavaPropertyNamesApiClient(ApiRequestExecutor requestExecutor) {
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
   * A test case for a JSON model with properties that are not legal Java identifiers.
   */
  public ProblematicName postNonJavaPropertyNames(ProblematicName requestBody) throws
      ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    PostNonJavaPropertyNamesResult result = returningResult.postNonJavaPropertyNames(requestBody);

    if (!result.isSuccessful()) {
      throw new ApiClientErrorWithFailureEntityException(result.getResponse());
    }

    return result.getEntityAsProblematicName();
  }

  /**
   * Contains methods returning operation specific result classes, allowing inspection of the operations' responses.
   */
  public class ReturningResult {
    /**
     * A test case for a JSON model with properties that are not legal Java identifiers.
     */
    public PostNonJavaPropertyNamesResult postNonJavaPropertyNames(ProblematicName requestBody)
        throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/nonJavaPropertyNames", "POST");

      builder.requestBody("application/json", true, requestBody);

      builder.response(StatusCode.of(200), "application/json", ProblematicName.class);
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new PostNonJavaPropertyNamesResult(response);
    }
  }

  /**
   * Represents the result of calling operation postNonJavaPropertyNames.
   */
  public static class PostNonJavaPropertyNamesResult {
    private final ApiResponse response;

    public PostNonJavaPropertyNamesResult(ApiResponse response) {
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
     * Returns whether the response's status code is 200, while the response's entity is of type {@code ProblematicName}.
     */
    public boolean isStatus200ReturningProblematicName() {
      return response.getStatusCode() == 200 && response.getEntityType() == ProblematicName.class;
    }

    /**
     * Returns whether the response's entity is of type {@code Failure}.
     */
    public boolean isReturningFailure() {
      return response.getEntityType() == Failure.class;
    }

    /**
     * Returns the response's entity wrapped in {@code java.lang.Optional.of()} if it is of type {@code ProblematicName}. Otherwise, returns {@code Optional.empty()}.
     */
    public Optional<ProblematicName> getEntityIfProblematicName() {
      return Optional.ofNullable(getEntityAsProblematicName());
    }

    /**
     * Returns the response's entity if it is of type {@code ProblematicName}. Otherwise, returns null.
     */
    public ProblematicName getEntityAsProblematicName() {
      if (response.getEntityType() == ProblematicName.class) {
        return (ProblematicName) response.getEntity();
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
      PostNonJavaPropertyNamesResult o = (PostNonJavaPropertyNamesResult) other;
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
      return builder.replace(0, 2, "PostNonJavaPropertyNamesResult{").append('}').toString();
    }
  }
}
