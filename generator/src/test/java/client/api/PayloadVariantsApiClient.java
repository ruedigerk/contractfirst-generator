package client.api;

import client.model.Failure;
import client.model.Item;
import com.google.gson.reflect.TypeToken;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.RequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * Contains methods for all API operations tagged "PayloadVariants".
 */
public class PayloadVariantsApiClient {
  public static final Type LIST_OF_ITEM = new TypeToken<List<Item>>(){}.getType();

  private final RequestExecutor requestExecutor;

  private final ReturningAnyResponse returningAnyResponse;

  private final ReturningSuccessfulResponse returningSuccessfulResponse;

  public PayloadVariantsApiClient(RequestExecutor requestExecutor) {
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
   * Test operation for generating generic types, e.g. List of Item.
   */
  public List<Item> filterItems(List<Item> requestBody) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    FilterItemsSuccessfulResponse response = returningSuccessfulResponse.filterItems(requestBody);

    return response.getEntity();
  }

  /**
   * Test binary input and output.
   */
  public InputStream uploadAndReturnBinary(InputStream requestBody) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    UploadAndReturnBinarySuccessfulResponse response = returningSuccessfulResponse.uploadAndReturnBinary(requestBody);

    return response.getEntity();
  }

  /**
   * Test for 204 response.
   */
  public void changeItem(Item requestBody) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    ChangeItemSuccessfulResponse response = returningSuccessfulResponse.changeItem(requestBody);
  }

  /**
   * Contains methods for all operations returning instances of operation specific success classes and throwing exceptions for unsuccessful status codes.
   */
  public class ReturningSuccessfulResponse {
    /**
     * Test operation for generating generic types, e.g. List of Item.
     */
    public FilterItemsSuccessfulResponse filterItems(List<Item> requestBody) throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException,
        ApiClientErrorWithFailureEntityException {

      ApiResponse response = returningAnyResponse.filterItems(requestBody);

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithFailureEntityException(response);
      }

      return new FilterItemsSuccessfulResponse(response);
    }

    /**
     * Test binary input and output.
     */
    public UploadAndReturnBinarySuccessfulResponse uploadAndReturnBinary(InputStream requestBody)
        throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException, ApiClientErrorWithFailureEntityException {

      ApiResponse response = returningAnyResponse.uploadAndReturnBinary(requestBody);

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithFailureEntityException(response);
      }

      return new UploadAndReturnBinarySuccessfulResponse(response);
    }

    /**
     * Test for 204 response.
     */
    public ChangeItemSuccessfulResponse changeItem(Item requestBody) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException,
        ApiClientErrorWithFailureEntityException {

      ApiResponse response = returningAnyResponse.changeItem(requestBody);

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithFailureEntityException(response);
      }

      return new ChangeItemSuccessfulResponse(response);
    }
  }

  /**
   * Contains methods for all operations returning instances of ApiResponse and not throwing exceptions for unsuccessful status codes.
   */
  public class ReturningAnyResponse {
    /**
     * Test operation for generating generic types, e.g. List of Item.
     */
    public ApiResponse filterItems(List<Item> requestBody) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/items", "POST");

      builder.requestBody("application/json", true, requestBody);

      builder.response(StatusCode.of(200), "application/json", LIST_OF_ITEM);
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      return requestExecutor.executeRequest(builder.build());
    }

    /**
     * Test binary input and output.
     */
    public ApiResponse uploadAndReturnBinary(InputStream requestBody) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/itemBinaries", "PUT");

      builder.requestBody("application/octet-stream", true, requestBody);

      builder.response(StatusCode.of(200), "application/octet-stream", InputStream.class);
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      return requestExecutor.executeRequest(builder.build());
    }

    /**
     * Test for 204 response.
     */
    public ApiResponse changeItem(Item requestBody) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/itemBinaries", "POST");

      builder.requestBody("application/json", true, requestBody);

      builder.response(StatusCode.of(204));
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      return requestExecutor.executeRequest(builder.build());
    }
  }

  /**
   * Represents a successful response of operation filterItems, i.e., the status code being in range 200 to 299.
   */
  public static class FilterItemsSuccessfulResponse {
    private final ApiResponse response;

    public FilterItemsSuccessfulResponse(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getApiResponse() {
      return response;
    }

    /**
     * Returns whether the response's status code is 200, while the response's entity is of type {@code List<Item>}.
     */
    public boolean isStatus200ReturningListOfItem() {
      return response.getStatusCode() == 200 && response.getEntityType() == LIST_OF_ITEM;
    }

    /**
     * Returns the response's entity of type {@code List<Item>}.
     */
    @SuppressWarnings("unchecked")
    public List<Item> getEntity() {
      return (List<Item>) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      FilterItemsSuccessfulResponse o = (FilterItemsSuccessfulResponse) other;
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
      return builder.replace(0, 2, "FilterItemsSuccessfulResponse{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation uploadAndReturnBinary, i.e., the status code being in range 200 to 299.
   */
  public static class UploadAndReturnBinarySuccessfulResponse {
    private final ApiResponse response;

    public UploadAndReturnBinarySuccessfulResponse(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getApiResponse() {
      return response;
    }

    /**
     * Returns whether the response's status code is 200, while the response's entity is of type {@code InputStream}.
     */
    public boolean isStatus200ReturningInputStream() {
      return response.getStatusCode() == 200 && response.getEntityType() == InputStream.class;
    }

    /**
     * Returns the response's entity of type {@code InputStream}.
     */
    public InputStream getEntity() {
      return (InputStream) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      UploadAndReturnBinarySuccessfulResponse o = (UploadAndReturnBinarySuccessfulResponse) other;
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
      return builder.replace(0, 2, "UploadAndReturnBinarySuccessfulResponse{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation changeItem, i.e., the status code being in range 200 to 299.
   */
  public static class ChangeItemSuccessfulResponse {
    private final ApiResponse response;

    public ChangeItemSuccessfulResponse(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getApiResponse() {
      return response;
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
      ChangeItemSuccessfulResponse o = (ChangeItemSuccessfulResponse) other;
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
      return builder.replace(0, 2, "ChangeItemSuccessfulResponse{").append('}').toString();
    }
  }
}
