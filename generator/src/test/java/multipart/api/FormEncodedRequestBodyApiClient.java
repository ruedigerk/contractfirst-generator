package multipart.api;

import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.util.Objects;
import multipart.model.FormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencodedEnumProperty;

/**
 * Contains methods for all API operations tagged "FormEncodedRequestBody".
 */
public class FormEncodedRequestBodyApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningResult returningResult;

  public FormEncodedRequestBodyApiClient(ApiRequestExecutor requestExecutor) {
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
   * A test case for an x-www-form-urlencoded encoded request body.
   */
  public void formEncodedRequestBody(String stringProperty, Long integerProperty,
      FormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencodedEnumProperty enumProperty)
      throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    FormEncodedRequestBodyResult result = returningResult.formEncodedRequestBody(stringProperty, integerProperty, enumProperty);
  }

  /**
   * Contains methods returning operation specific result classes, allowing inspection of the operations' responses.
   */
  public class ReturningResult {
    /**
     * A test case for an x-www-form-urlencoded encoded request body.
     */
    public FormEncodedRequestBodyResult formEncodedRequestBody(String stringProperty,
        Long integerProperty,
        FormEncodedRequestBodyRequestBodyApplicationXWwwFormUrlencodedEnumProperty enumProperty)
        throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/formEncodedRequestBody", "POST");

      builder.requestBodyPart("stringProperty", stringProperty);
      builder.requestBodyPart("integerProperty", integerProperty);
      builder.requestBodyPart("enumProperty", enumProperty);
      builder.multipartRequestBody("application/x-www-form-urlencoded");

      builder.response(StatusCode.of(204));

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new FormEncodedRequestBodyResult(response);
    }
  }

  /**
   * Represents the result of calling operation formEncodedRequestBody.
   */
  public static class FormEncodedRequestBodyResult {
    private final ApiResponse response;

    public FormEncodedRequestBodyResult(ApiResponse response) {
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
      FormEncodedRequestBodyResult o = (FormEncodedRequestBodyResult) other;
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
      return builder.replace(0, 2, "FormEncodedRequestBodyResult{").append('}').toString();
    }
  }
}
