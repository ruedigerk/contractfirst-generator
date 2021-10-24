package combinations_client.api;

import combinations_client.model.Book;
import combinations_client.model.CtcError;
import combinations_client.model.SevereCtcError;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.RequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.ParameterLocation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains methods for all API operations tagged "ContentTypeCombinations".
 */
public class ContentTypeCombinationsApiClient {
  private final RequestExecutor requestExecutor;

  private final ReturningAnyResponse returningAnyResponse;

  private final ReturningSuccessfulResponse returningSuccessfulResponse;

  public ContentTypeCombinationsApiClient(RequestExecutor requestExecutor) {
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
   * Test case for only having a response with status code "default".
   */
  public Book getDefaultOnly(String testCaseSelector) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithBookEntityException {

    GetDefaultOnlySuccessfulResponse response = returningSuccessfulResponse.getDefaultOnly(testCaseSelector);

    return response.getEntity();
  }

  /**
   * Test case for only having a single successful response.
   */
  public Book getSuccessOnly() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    GetSuccessOnlySuccessfulResponse response = returningSuccessfulResponse.getSuccessOnly();

    return response.getEntity();
  }

  /**
   * Test case for only having a single failure response.
   */
  public void getFailureOnly() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException, ApiClientErrorWithCtcErrorEntityException {

    GetFailureOnlySuccessfulResponse response = returningSuccessfulResponse.getFailureOnly();
  }

  /**
   * Test case for having one successful response with an entity and a default for all errors.
   */
  public Book getSuccessEntityAndErrorDefault(String testCaseSelector) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithCtcErrorEntityException {

    GetSuccessEntityAndErrorDefaultSuccessfulResponse response = returningSuccessfulResponse.getSuccessEntityAndErrorDefault(testCaseSelector);

    return response.getEntity();
  }

  /**
   * Test case for having multiple success entity types.
   */
  public GetMultipleSuccessEntitiesSuccessfulResponse getMultipleSuccessEntities(
      String testCaseSelector) throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    GetMultipleSuccessEntitiesSuccessfulResponse response = returningSuccessfulResponse.getMultipleSuccessEntities(testCaseSelector);

    return response;
  }

  /**
   * Test case for having multiple successful responses without content.
   */
  public void getMultipleSuccessResponsesWithoutContent(String testCaseSelector) throws
      ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

    GetMultipleSuccessResponsesWithoutContentSuccessfulResponse response = returningSuccessfulResponse.getMultipleSuccessResponsesWithoutContent(testCaseSelector);
  }

  /**
   * Test case for having multiple error entity types.
   */
  public Book getMultipleErrorEntities(String testCaseSelector) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithCtcErrorEntityException, ApiClientErrorWithSevereCtcErrorEntityException {

    GetMultipleErrorEntitiesSuccessfulResponse response = returningSuccessfulResponse.getMultipleErrorEntities(testCaseSelector);

    return response.getEntity();
  }

  /**
   * Test case for returning content with status code 204.
   */
  public void getContentFor204() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    GetContentFor204SuccessfulResponse response = returningSuccessfulResponse.getContentFor204();
  }

  /**
   * Contains methods for all operations returning instances of operation specific success classes and throwing exceptions for unsuccessful status codes.
   */
  public class ReturningSuccessfulResponse {
    /**
     * Test case for only having a response with status code "default".
     */
    public GetDefaultOnlySuccessfulResponse getDefaultOnly(String testCaseSelector) throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException,
        ApiClientErrorWithBookEntityException {

      ApiResponse response = returningAnyResponse.getDefaultOnly(testCaseSelector);

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithBookEntityException(response);
      }

      return new GetDefaultOnlySuccessfulResponse(response);
    }

    /**
     * Test case for only having a single successful response.
     */
    public GetSuccessOnlySuccessfulResponse getSuccessOnly() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      ApiResponse response = returningAnyResponse.getSuccessOnly();

      return new GetSuccessOnlySuccessfulResponse(response);
    }

    /**
     * Test case for only having a single failure response.
     */
    public GetFailureOnlySuccessfulResponse getFailureOnly() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException,
        ApiClientErrorWithCtcErrorEntityException {

      ApiResponse response = returningAnyResponse.getFailureOnly();

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithCtcErrorEntityException(response);
      }

      return new GetFailureOnlySuccessfulResponse(response);
    }

    /**
     * Test case for having one successful response with an entity and a default for all errors.
     */
    public GetSuccessEntityAndErrorDefaultSuccessfulResponse getSuccessEntityAndErrorDefault(
        String testCaseSelector) throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException, ApiClientErrorWithCtcErrorEntityException {

      ApiResponse response = returningAnyResponse.getSuccessEntityAndErrorDefault(testCaseSelector);

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithCtcErrorEntityException(response);
      }

      return new GetSuccessEntityAndErrorDefaultSuccessfulResponse(response);
    }

    /**
     * Test case for having multiple success entity types.
     */
    public GetMultipleSuccessEntitiesSuccessfulResponse getMultipleSuccessEntities(
        String testCaseSelector) throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      ApiResponse response = returningAnyResponse.getMultipleSuccessEntities(testCaseSelector);

      return new GetMultipleSuccessEntitiesSuccessfulResponse(response);
    }

    /**
     * Test case for having multiple successful responses without content.
     */
    public GetMultipleSuccessResponsesWithoutContentSuccessfulResponse getMultipleSuccessResponsesWithoutContent(
        String testCaseSelector) throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      ApiResponse response = returningAnyResponse.getMultipleSuccessResponsesWithoutContent(testCaseSelector);

      return new GetMultipleSuccessResponsesWithoutContentSuccessfulResponse(response);
    }

    /**
     * Test case for having multiple error entity types.
     */
    public GetMultipleErrorEntitiesSuccessfulResponse getMultipleErrorEntities(
        String testCaseSelector) throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException, ApiClientErrorWithCtcErrorEntityException,
        ApiClientErrorWithSevereCtcErrorEntityException {

      ApiResponse response = returningAnyResponse.getMultipleErrorEntities(testCaseSelector);

      if (!response.isSuccessful()) {
        if (response.getEntityType() == CtcError.class) {
          throw new ApiClientErrorWithCtcErrorEntityException(response);
        }
        throw new ApiClientErrorWithSevereCtcErrorEntityException(response);
      }

      return new GetMultipleErrorEntitiesSuccessfulResponse(response);
    }

    /**
     * Test case for returning content with status code 204.
     */
    public GetContentFor204SuccessfulResponse getContentFor204() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      ApiResponse response = returningAnyResponse.getContentFor204();

      return new GetContentFor204SuccessfulResponse(response);
    }
  }

  /**
   * Contains methods for all operations returning instances of ApiResponse and not throwing exceptions for unsuccessful status codes.
   */
  public class ReturningAnyResponse {
    /**
     * Test case for only having a response with status code "default".
     */
    public ApiResponse getDefaultOnly(String testCaseSelector) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/defaultOnly", "GET");

      builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

      builder.response(StatusCode.DEFAULT, "application/json", Book.class);

      return requestExecutor.executeRequest(builder.build());
    }

    /**
     * Test case for only having a single successful response.
     */
    public ApiResponse getSuccessOnly() throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/successOnly", "GET");

      builder.response(StatusCode.of(200), "application/json", Book.class);

      return requestExecutor.executeRequest(builder.build());
    }

    /**
     * Test case for only having a single failure response.
     */
    public ApiResponse getFailureOnly() throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/failureOnly", "GET");

      builder.response(StatusCode.of(400), "application/json", CtcError.class);

      return requestExecutor.executeRequest(builder.build());
    }

    /**
     * Test case for having one successful response with an entity and a default for all errors.
     */
    public ApiResponse getSuccessEntityAndErrorDefault(String testCaseSelector) throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/successEntityAndErrorDefault", "GET");

      builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

      builder.response(StatusCode.of(200), "application/json", Book.class);
      builder.response(StatusCode.DEFAULT, "application/json", CtcError.class);

      return requestExecutor.executeRequest(builder.build());
    }

    /**
     * Test case for having multiple success entity types.
     */
    public ApiResponse getMultipleSuccessEntities(String testCaseSelector) throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/multipleSuccessEntities", "GET");

      builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

      builder.response(StatusCode.of(200), "application/json", Book.class);
      builder.response(StatusCode.of(201), "application/json", CtcError.class);

      return requestExecutor.executeRequest(builder.build());
    }

    /**
     * Test case for having multiple successful responses without content.
     */
    public ApiResponse getMultipleSuccessResponsesWithoutContent(String testCaseSelector) throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/multipleSuccessResponsesWithoutContent", "GET");

      builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

      builder.response(StatusCode.of(200));
      builder.response(StatusCode.of(204));

      return requestExecutor.executeRequest(builder.build());
    }

    /**
     * Test case for having multiple error entity types.
     */
    public ApiResponse getMultipleErrorEntities(String testCaseSelector) throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/multipleErrorEntities", "GET");

      builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

      builder.response(StatusCode.of(200), "application/json", Book.class);
      builder.response(StatusCode.of(400), "application/json", CtcError.class);
      builder.response(StatusCode.of(500), "application/json", SevereCtcError.class);

      return requestExecutor.executeRequest(builder.build());
    }

    /**
     * Test case for returning content with status code 204.
     */
    public ApiResponse getContentFor204() throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/contentFor204", "GET");

      builder.response(StatusCode.of(204));

      return requestExecutor.executeRequest(builder.build());
    }
  }

  /**
   * Represents a successful response of operation getDefaultOnly, i.e., the status code being in range 200 to 299.
   */
  public static class GetDefaultOnlySuccessfulResponse {
    private final ApiResponse response;

    public GetDefaultOnlySuccessfulResponse(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getApiResponse() {
      return response;
    }

    /**
     * Returns whether the response's entity is of type {@code Book}.
     */
    public boolean isReturningBook() {
      return response.getEntityType() == Book.class;
    }

    /**
     * Returns the response's entity of type {@code Book}.
     */
    public Book getEntity() {
      return (Book) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetDefaultOnlySuccessfulResponse o = (GetDefaultOnlySuccessfulResponse) other;
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
      return builder.replace(0, 2, "GetDefaultOnlySuccessfulResponse{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation getSuccessOnly, i.e., the status code being in range 200 to 299.
   */
  public static class GetSuccessOnlySuccessfulResponse {
    private final ApiResponse response;

    public GetSuccessOnlySuccessfulResponse(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getApiResponse() {
      return response;
    }

    /**
     * Returns whether the response's status code is 200, while the response's entity is of type {@code Book}.
     */
    public boolean isStatus200ReturningBook() {
      return response.getStatusCode() == 200 && response.getEntityType() == Book.class;
    }

    /**
     * Returns the response's entity of type {@code Book}.
     */
    public Book getEntity() {
      return (Book) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetSuccessOnlySuccessfulResponse o = (GetSuccessOnlySuccessfulResponse) other;
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
      return builder.replace(0, 2, "GetSuccessOnlySuccessfulResponse{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation getFailureOnly, i.e., the status code being in range 200 to 299.
   */
  public static class GetFailureOnlySuccessfulResponse {
    private final ApiResponse response;

    public GetFailureOnlySuccessfulResponse(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getApiResponse() {
      return response;
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetFailureOnlySuccessfulResponse o = (GetFailureOnlySuccessfulResponse) other;
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
      return builder.replace(0, 2, "GetFailureOnlySuccessfulResponse{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation getSuccessEntityAndErrorDefault, i.e., the status code being in range 200 to 299.
   */
  public static class GetSuccessEntityAndErrorDefaultSuccessfulResponse {
    private final ApiResponse response;

    public GetSuccessEntityAndErrorDefaultSuccessfulResponse(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getApiResponse() {
      return response;
    }

    /**
     * Returns whether the response's status code is 200, while the response's entity is of type {@code Book}.
     */
    public boolean isStatus200ReturningBook() {
      return response.getStatusCode() == 200 && response.getEntityType() == Book.class;
    }

    /**
     * Returns the response's entity of type {@code Book}.
     */
    public Book getEntity() {
      return (Book) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetSuccessEntityAndErrorDefaultSuccessfulResponse o = (GetSuccessEntityAndErrorDefaultSuccessfulResponse) other;
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
      return builder.replace(0, 2, "GetSuccessEntityAndErrorDefaultSuccessfulResponse{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation getMultipleSuccessEntities, i.e., the status code being in range 200 to 299.
   */
  public static class GetMultipleSuccessEntitiesSuccessfulResponse {
    private final ApiResponse response;

    public GetMultipleSuccessEntitiesSuccessfulResponse(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getApiResponse() {
      return response;
    }

    /**
     * Returns whether the response's status code is 200, while the response's entity is of type {@code Book}.
     */
    public boolean isStatus200ReturningBook() {
      return response.getStatusCode() == 200 && response.getEntityType() == Book.class;
    }

    /**
     * Returns whether the response's status code is 201, while the response's entity is of type {@code CtcError}.
     */
    public boolean isStatus201ReturningCtcError() {
      return response.getStatusCode() == 201 && response.getEntityType() == CtcError.class;
    }

    /**
     * Returns the response's entity wrapped in {@code java.lang.Optional.of()} if it is of type {@code Book}. Otherwise, returns {@code Optional.empty()}.
     */
    public Optional<Book> getEntityIfBook() {
      return Optional.ofNullable(getEntityAsBook());
    }

    /**
     * Returns the response's entity if it is of type {@code Book}. Otherwise, returns null.
     */
    public Book getEntityAsBook() {
      if (response.getEntityType() == Book.class) {
        return (Book) response.getEntity();
      } else {
        return null;
      }
    }

    /**
     * Returns the response's entity wrapped in {@code java.lang.Optional.of()} if it is of type {@code CtcError}. Otherwise, returns {@code Optional.empty()}.
     */
    public Optional<CtcError> getEntityIfCtcError() {
      return Optional.ofNullable(getEntityAsCtcError());
    }

    /**
     * Returns the response's entity if it is of type {@code CtcError}. Otherwise, returns null.
     */
    public CtcError getEntityAsCtcError() {
      if (response.getEntityType() == CtcError.class) {
        return (CtcError) response.getEntity();
      } else {
        return null;
      }
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetMultipleSuccessEntitiesSuccessfulResponse o = (GetMultipleSuccessEntitiesSuccessfulResponse) other;
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
      return builder.replace(0, 2, "GetMultipleSuccessEntitiesSuccessfulResponse{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation getMultipleSuccessResponsesWithoutContent, i.e., the status code being in range 200 to 299.
   */
  public static class GetMultipleSuccessResponsesWithoutContentSuccessfulResponse {
    private final ApiResponse response;

    public GetMultipleSuccessResponsesWithoutContentSuccessfulResponse(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getApiResponse() {
      return response;
    }

    /**
     * Returns whether the response's status code is 200, while the response has no entity.
     */
    public boolean isStatus200WithoutEntity() {
      return response.getStatusCode() == 200;
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
      GetMultipleSuccessResponsesWithoutContentSuccessfulResponse o = (GetMultipleSuccessResponsesWithoutContentSuccessfulResponse) other;
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
      return builder.replace(0, 2, "GetMultipleSuccessResponsesWithoutContentSuccessfulResponse{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation getMultipleErrorEntities, i.e., the status code being in range 200 to 299.
   */
  public static class GetMultipleErrorEntitiesSuccessfulResponse {
    private final ApiResponse response;

    public GetMultipleErrorEntitiesSuccessfulResponse(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getApiResponse() {
      return response;
    }

    /**
     * Returns whether the response's status code is 200, while the response's entity is of type {@code Book}.
     */
    public boolean isStatus200ReturningBook() {
      return response.getStatusCode() == 200 && response.getEntityType() == Book.class;
    }

    /**
     * Returns the response's entity of type {@code Book}.
     */
    public Book getEntity() {
      return (Book) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetMultipleErrorEntitiesSuccessfulResponse o = (GetMultipleErrorEntitiesSuccessfulResponse) other;
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
      return builder.replace(0, 2, "GetMultipleErrorEntitiesSuccessfulResponse{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation getContentFor204, i.e., the status code being in range 200 to 299.
   */
  public static class GetContentFor204SuccessfulResponse {
    private final ApiResponse response;

    public GetContentFor204SuccessfulResponse(ApiResponse response) {
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
      GetContentFor204SuccessfulResponse o = (GetContentFor204SuccessfulResponse) other;
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
      return builder.replace(0, 2, "GetContentFor204SuccessfulResponse{").append('}').toString();
    }
  }
}
