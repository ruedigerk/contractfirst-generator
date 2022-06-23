package client.api;

import client.model.Failure;
import client.model.Item;
import com.google.gson.reflect.TypeToken;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains methods for all API operations tagged "PayloadVariants".
 */
public class PayloadVariantsApiClient {
  public static final Type LIST_OF_ITEM = new TypeToken<List<Item>>(){}.getType();

  private final ApiRequestExecutor requestExecutor;

  private final ReturningResult returningResult;

  public PayloadVariantsApiClient(ApiRequestExecutor requestExecutor) {
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
   * Test operation for generating generic types, e.g. List of Item.
   */
  public List<Item> filterItems(List<Item> requestBody) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    FilterItemsResult result = returningResult.filterItems(requestBody);

    if (!result.isSuccessful()) {
      throw new ApiClientErrorWithFailureEntityException(result.getResponse());
    }

    return result.getEntityAsListOfItem();
  }

  /**
   * Second test operation for generic types, using the same generic return type as the other operation (test for bug in the generator).
   */
  public List<Item> filterItems2(List<Item> requestBody) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    FilterItems2Result result = returningResult.filterItems2(requestBody);

    if (!result.isSuccessful()) {
      throw new ApiClientErrorWithFailureEntityException(result.getResponse());
    }

    return result.getEntityAsListOfItem();
  }

  /**
   * Test binary input and output.
   */
  public InputStream uploadAndReturnBinary(InputStream requestBody) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    UploadAndReturnBinaryResult result = returningResult.uploadAndReturnBinary(requestBody);

    if (!result.isSuccessful()) {
      throw new ApiClientErrorWithFailureEntityException(result.getResponse());
    }

    return result.getEntityAsInputStream();
  }

  /**
   * Test for 204 response.
   */
  public void changeItem(Item requestBody) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    ChangeItemResult result = returningResult.changeItem(requestBody);

    if (!result.isSuccessful()) {
      throw new ApiClientErrorWithFailureEntityException(result.getResponse());
    }
  }

  /**
   * Contains methods returning operation specific result classes, allowing inspection of the operations' responses.
   */
  public class ReturningResult {
    /**
     * Test operation for generating generic types, e.g. List of Item.
     */
    public FilterItemsResult filterItems(List<Item> requestBody) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/items", "POST");

      builder.requestBody("application/json", true, requestBody);

      builder.response(StatusCode.of(200), "application/json", LIST_OF_ITEM);
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new FilterItemsResult(response);
    }

    /**
     * Second test operation for generic types, using the same generic return type as the other operation (test for bug in the generator).
     */
    public FilterItems2Result filterItems2(List<Item> requestBody) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/items2", "POST");

      builder.requestBody("application/json", true, requestBody);

      builder.response(StatusCode.of(200), "application/json", LIST_OF_ITEM);
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new FilterItems2Result(response);
    }

    /**
     * Test binary input and output.
     */
    public UploadAndReturnBinaryResult uploadAndReturnBinary(InputStream requestBody) throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/itemBinaries", "PUT");

      builder.requestBody("application/octet-stream", true, requestBody);

      builder.response(StatusCode.of(200), "application/octet-stream", InputStream.class);
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new UploadAndReturnBinaryResult(response);
    }

    /**
     * Test for 204 response.
     */
    public ChangeItemResult changeItem(Item requestBody) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/itemBinaries", "POST");

      builder.requestBody("application/json", true, requestBody);

      builder.response(StatusCode.of(204));
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new ChangeItemResult(response);
    }
  }

  /**
   * Represents the result of calling operation filterItems.
   */
  public static class FilterItemsResult {
    private final ApiResponse response;

    public FilterItemsResult(ApiResponse response) {
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
     * Returns whether the response's status code is 200, while the response's entity is of type {@code List<Item>}.
     */
    public boolean isStatus200ReturningListOfItem() {
      return response.getStatusCode() == 200 && response.getEntityType() == LIST_OF_ITEM;
    }

    /**
     * Returns whether the response's entity is of type {@code Failure}.
     */
    public boolean isReturningFailure() {
      return response.getEntityType() == Failure.class;
    }

    /**
     * Returns the response's entity wrapped in {@code java.lang.Optional.of()} if it is of type {@code List<Item>}. Otherwise, returns {@code Optional.empty()}.
     */
    public Optional<List<Item>> getEntityIfListOfItem() {
      return Optional.ofNullable(getEntityAsListOfItem());
    }

    /**
     * Returns the response's entity if it is of type {@code List<Item>}. Otherwise, returns null.
     */
    @SuppressWarnings("unchecked")
    public List<Item> getEntityAsListOfItem() {
      if (response.getEntityType() == LIST_OF_ITEM) {
        return (List<Item>) response.getEntity();
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
      FilterItemsResult o = (FilterItemsResult) other;
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
      return builder.replace(0, 2, "FilterItemsResult{").append('}').toString();
    }
  }

  /**
   * Represents the result of calling operation filterItems2.
   */
  public static class FilterItems2Result {
    private final ApiResponse response;

    public FilterItems2Result(ApiResponse response) {
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
     * Returns whether the response's status code is 200, while the response's entity is of type {@code List<Item>}.
     */
    public boolean isStatus200ReturningListOfItem() {
      return response.getStatusCode() == 200 && response.getEntityType() == LIST_OF_ITEM;
    }

    /**
     * Returns whether the response's entity is of type {@code Failure}.
     */
    public boolean isReturningFailure() {
      return response.getEntityType() == Failure.class;
    }

    /**
     * Returns the response's entity wrapped in {@code java.lang.Optional.of()} if it is of type {@code List<Item>}. Otherwise, returns {@code Optional.empty()}.
     */
    public Optional<List<Item>> getEntityIfListOfItem() {
      return Optional.ofNullable(getEntityAsListOfItem());
    }

    /**
     * Returns the response's entity if it is of type {@code List<Item>}. Otherwise, returns null.
     */
    @SuppressWarnings("unchecked")
    public List<Item> getEntityAsListOfItem() {
      if (response.getEntityType() == LIST_OF_ITEM) {
        return (List<Item>) response.getEntity();
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
      FilterItems2Result o = (FilterItems2Result) other;
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
      return builder.replace(0, 2, "FilterItems2Result{").append('}').toString();
    }
  }

  /**
   * Represents the result of calling operation uploadAndReturnBinary.
   */
  public static class UploadAndReturnBinaryResult {
    private final ApiResponse response;

    public UploadAndReturnBinaryResult(ApiResponse response) {
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
      UploadAndReturnBinaryResult o = (UploadAndReturnBinaryResult) other;
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
      return builder.replace(0, 2, "UploadAndReturnBinaryResult{").append('}').toString();
    }
  }

  /**
   * Represents the result of calling operation changeItem.
   */
  public static class ChangeItemResult {
    private final ApiResponse response;

    public ChangeItemResult(ApiResponse response) {
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

    /**
     * Returns whether the response's entity is of type {@code Failure}.
     */
    public boolean isReturningFailure() {
      return response.getEntityType() == Failure.class;
    }

    /**
     * Returns the response's entity of type {@code Failure}.
     */
    public Failure getEntity() {
      return (Failure) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      ChangeItemResult o = (ChangeItemResult) other;
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
      return builder.replace(0, 2, "ChangeItemResult{").append('}').toString();
    }
  }
}
