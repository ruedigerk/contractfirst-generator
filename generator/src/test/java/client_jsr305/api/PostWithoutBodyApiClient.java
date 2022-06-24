package client_jsr305.api;

import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.util.Objects;

/**
 * Contains methods for all API operations tagged "PostWithoutBody".
 */
public class PostWithoutBodyApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningResult returningResult;

  public PostWithoutBodyApiClient(ApiRequestExecutor requestExecutor) {
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
   * Testing HTTP method POST without a request body.
   */
  public void postWithoutBody() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    PostWithoutBodyResult result = returningResult.postWithoutBody();
  }

  /**
   * Testing HTTP method PUT without a request body.
   */
  public void putWithoutBody() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    PutWithoutBodyResult result = returningResult.putWithoutBody();
  }

  /**
   * Testing HTTP method PATCH without a request body.
   */
  public void patchWithoutBody() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    PatchWithoutBodyResult result = returningResult.patchWithoutBody();
  }

  /**
   * Contains methods returning operation specific result classes, allowing inspection of the operations' responses.
   */
  public class ReturningResult {
    /**
     * Testing HTTP method POST without a request body.
     */
    public PostWithoutBodyResult postWithoutBody() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/postWithoutBody/post", "POST");

      builder.response(StatusCode.of(204));

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new PostWithoutBodyResult(response);
    }

    /**
     * Testing HTTP method PUT without a request body.
     */
    public PutWithoutBodyResult putWithoutBody() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/postWithoutBody/put", "PUT");

      builder.response(StatusCode.of(204));

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new PutWithoutBodyResult(response);
    }

    /**
     * Testing HTTP method PATCH without a request body.
     */
    public PatchWithoutBodyResult patchWithoutBody() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/postWithoutBody/patch", "POST");

      builder.response(StatusCode.of(204));

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new PatchWithoutBodyResult(response);
    }
  }

  /**
   * Represents the result of calling operation postWithoutBody.
   */
  public static class PostWithoutBodyResult {
    private final ApiResponse response;

    public PostWithoutBodyResult(ApiResponse response) {
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
      PostWithoutBodyResult o = (PostWithoutBodyResult) other;
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
      return builder.replace(0, 2, "PostWithoutBodyResult{").append('}').toString();
    }
  }

  /**
   * Represents the result of calling operation putWithoutBody.
   */
  public static class PutWithoutBodyResult {
    private final ApiResponse response;

    public PutWithoutBodyResult(ApiResponse response) {
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
      PutWithoutBodyResult o = (PutWithoutBodyResult) other;
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
      return builder.replace(0, 2, "PutWithoutBodyResult{").append('}').toString();
    }
  }

  /**
   * Represents the result of calling operation patchWithoutBody.
   */
  public static class PatchWithoutBodyResult {
    private final ApiResponse response;

    public PatchWithoutBodyResult(ApiResponse response) {
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
      PatchWithoutBodyResult o = (PatchWithoutBodyResult) other;
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
      return builder.replace(0, 2, "PatchWithoutBodyResult{").append('}').toString();
    }
  }
}
