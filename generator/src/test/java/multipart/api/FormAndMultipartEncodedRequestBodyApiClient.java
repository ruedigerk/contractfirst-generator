package multipart.api;

import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.FileBodyPart;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.util.List;
import java.util.Objects;
import multipart.model.MultipartRequestBodyRequestBodyMultipartFormDataObjectProperty;

/**
 * Contains methods for all API operations tagged "FormAndMultipartEncodedRequestBody".
 */
public class FormAndMultipartEncodedRequestBodyApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningResult returningResult;

  public FormAndMultipartEncodedRequestBodyApiClient(ApiRequestExecutor requestExecutor) {
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
   * A test case for a multipart/form-data encoded request body.
   */
  public void multipartRequestBody(String stringProperty, Long integerProperty,
      MultipartRequestBodyRequestBodyMultipartFormDataObjectProperty objectProperty,
      FileBodyPart firstBinary, List<FileBodyPart> additionalBinaries) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException {

    MultipartRequestBodyResult result = returningResult.multipartRequestBody(stringProperty, integerProperty, objectProperty, firstBinary, additionalBinaries);
  }

  /**
   * Contains methods returning operation specific result classes, allowing inspection of the operations' responses.
   */
  public class ReturningResult {
    /**
     * A test case for a multipart/form-data encoded request body.
     */
    public MultipartRequestBodyResult multipartRequestBody(String stringProperty,
        Long integerProperty,
        MultipartRequestBodyRequestBodyMultipartFormDataObjectProperty objectProperty,
        FileBodyPart firstBinary, List<FileBodyPart> additionalBinaries) throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/multipartRequestBody", "POST");

      builder.requestBodyPart("stringProperty", stringProperty);
      builder.requestBodyPart("integerProperty", integerProperty);
      builder.requestBodyPart("objectProperty", objectProperty);
      builder.requestBodyPart("firstBinary", firstBinary);
      builder.requestBodyPart("additionalBinaries", additionalBinaries);
      builder.multipartRequestBody("multipart/form-data");

      builder.response(StatusCode.of(204));

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new MultipartRequestBodyResult(response);
    }
  }

  /**
   * Represents the result of calling operation multipartRequestBody.
   */
  public static class MultipartRequestBodyResult {
    private final ApiResponse response;

    public MultipartRequestBodyResult(ApiResponse response) {
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
      MultipartRequestBodyResult o = (MultipartRequestBodyResult) other;
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
      return builder.replace(0, 2, "MultipartRequestBodyResult{").append('}').toString();
    }
  }
}
