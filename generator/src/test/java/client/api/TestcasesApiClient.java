package client.api;

import client.model.Failure;
import client.model.GetInlineObjectInArray200;
import com.google.gson.reflect.TypeToken;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.RequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * Contains methods for all API operations tagged "testcases".
 */
public class TestcasesApiClient {
  public static final Type LIST_OF_GET_INLINE_OBJECT_IN_ARRAY200 = new TypeToken<List<GetInlineObjectInArray200>>(){}.getType();

  private final RequestExecutor requestExecutor;

  private final ReturningAnyResponse returningAnyResponse;

  private final ReturningSuccessfulResponse returningSuccessfulResponse;

  public TestcasesApiClient(RequestExecutor requestExecutor) {
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
   * A test case for the SchemaToJavaTypeTransformer.
   */
  public List<GetInlineObjectInArray200> getInlineObjectInArray() throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    GetInlineObjectInArraySuccessfulResponse response = returningSuccessfulResponse.getInlineObjectInArray();

    return response.getEntity();
  }

  /**
   * Contains methods for all operations returning instances of operation specific success classes and throwing exceptions for unsuccessful status codes.
   */
  public class ReturningSuccessfulResponse {
    /**
     * A test case for the SchemaToJavaTypeTransformer.
     */
    public GetInlineObjectInArraySuccessfulResponse getInlineObjectInArray() throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException,
        ApiClientErrorWithFailureEntityException {

      ApiResponse response = returningAnyResponse.getInlineObjectInArray();

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithFailureEntityException(response);
      }

      return new GetInlineObjectInArraySuccessfulResponse(response);
    }
  }

  /**
   * Contains methods for all operations returning instances of ApiResponse and not throwing exceptions for unsuccessful status codes.
   */
  public class ReturningAnyResponse {
    /**
     * A test case for the SchemaToJavaTypeTransformer.
     */
    public ApiResponse getInlineObjectInArray() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/testcases", "GET");

      builder.response(StatusCode.of(200), "application/json", LIST_OF_GET_INLINE_OBJECT_IN_ARRAY200);
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      return requestExecutor.executeRequest(builder.build());
    }
  }

  /**
   * Represents a successful response of operation getInlineObjectInArray, i.e., the status code being in range 200 to 299.
   */
  public static class GetInlineObjectInArraySuccessfulResponse {
    private final ApiResponse response;

    public GetInlineObjectInArraySuccessfulResponse(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getApiResponse() {
      return response;
    }

    /**
     * Returns whether the response's status code is 200, while the response's entity is of type {@code List<GetInlineObjectInArray200>}.
     */
    public boolean isStatus200ReturningListOfGetInlineObjectInArray200() {
      return response.getStatusCode() == 200 && response.getEntityType() == LIST_OF_GET_INLINE_OBJECT_IN_ARRAY200;
    }

    /**
     * Returns the response's entity of type {@code List<GetInlineObjectInArray200>}.
     */
    @SuppressWarnings("unchecked")
    public List<GetInlineObjectInArray200> getEntity() {
      return (List<GetInlineObjectInArray200>) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetInlineObjectInArraySuccessfulResponse o = (GetInlineObjectInArraySuccessfulResponse) other;
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
      return builder.replace(0, 2, "GetInlineObjectInArraySuccessfulResponse{").append('}').toString();
    }
  }
}
