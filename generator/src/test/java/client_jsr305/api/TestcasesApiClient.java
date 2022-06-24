package client_jsr305.api;

import client_jsr305.model.Failure;
import client_jsr305.model.GetInlineObjectInArray200;
import com.google.gson.reflect.TypeToken;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains methods for all API operations tagged "testcases".
 */
public class TestcasesApiClient {
  public static final Type LIST_OF_GET_INLINE_OBJECT_IN_ARRAY200 = new TypeToken<List<GetInlineObjectInArray200>>(){}.getType();

  private final ApiRequestExecutor requestExecutor;

  private final ReturningResult returningResult;

  public TestcasesApiClient(ApiRequestExecutor requestExecutor) {
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
   * A test case for the SchemaToJavaTypeTransformer.
   */
  public List<GetInlineObjectInArray200> getInlineObjectInArray() throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    GetInlineObjectInArrayResult result = returningResult.getInlineObjectInArray();

    if (!result.isSuccessful()) {
      throw new ApiClientErrorWithFailureEntityException(result.getResponse());
    }

    return result.getEntityAsListOfGetInlineObjectInArray200();
  }

  /**
   * Contains methods returning operation specific result classes, allowing inspection of the operations' responses.
   */
  public class ReturningResult {
    /**
     * A test case for the SchemaToJavaTypeTransformer.
     */
    public GetInlineObjectInArrayResult getInlineObjectInArray() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/testcases", "GET");

      builder.response(StatusCode.of(200), "application/json", LIST_OF_GET_INLINE_OBJECT_IN_ARRAY200);
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new GetInlineObjectInArrayResult(response);
    }
  }

  /**
   * Represents the result of calling operation getInlineObjectInArray.
   */
  public static class GetInlineObjectInArrayResult {
    private final ApiResponse response;

    public GetInlineObjectInArrayResult(ApiResponse response) {
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
     * Returns whether the response's status code is 200, while the response's entity is of type {@code List<GetInlineObjectInArray200>}.
     */
    public boolean isStatus200ReturningListOfGetInlineObjectInArray200() {
      return response.getStatusCode() == 200 && response.getEntityType() == LIST_OF_GET_INLINE_OBJECT_IN_ARRAY200;
    }

    /**
     * Returns whether the response's entity is of type {@code Failure}.
     */
    public boolean isReturningFailure() {
      return response.getEntityType() == Failure.class;
    }

    /**
     * Returns the response's entity wrapped in {@code java.lang.Optional.of()} if it is of type {@code List<GetInlineObjectInArray200>}. Otherwise, returns {@code Optional.empty()}.
     */
    public Optional<List<GetInlineObjectInArray200>> getEntityIfListOfGetInlineObjectInArray200() {
      return Optional.ofNullable(getEntityAsListOfGetInlineObjectInArray200());
    }

    /**
     * Returns the response's entity if it is of type {@code List<GetInlineObjectInArray200>}. Otherwise, returns null.
     */
    @SuppressWarnings("unchecked")
    public List<GetInlineObjectInArray200> getEntityAsListOfGetInlineObjectInArray200() {
      if (response.getEntityType() == LIST_OF_GET_INLINE_OBJECT_IN_ARRAY200) {
        return (List<GetInlineObjectInArray200>) response.getEntity();
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
      GetInlineObjectInArrayResult o = (GetInlineObjectInArrayResult) other;
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
      return builder.replace(0, 2, "GetInlineObjectInArrayResult{").append('}').toString();
    }
  }
}
