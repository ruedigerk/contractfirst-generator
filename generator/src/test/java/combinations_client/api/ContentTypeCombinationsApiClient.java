package combinations_client.api;

import combinations_client.model.Book;
import combinations_client.model.CtcError;
import combinations_client.model.SevereCtcError;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.ParameterLocation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains methods for all API operations tagged "ContentTypeCombinations".
 */
public class ContentTypeCombinationsApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningAnyResponse returningAnyResponse;

  private final ReturningSuccessfulResult returningSuccessfulResult;

  public ContentTypeCombinationsApiClient(ApiRequestExecutor requestExecutor) {
    this.requestExecutor = requestExecutor;
    this.returningAnyResponse = new ReturningAnyResponse();
    this.returningSuccessfulResult = new ReturningSuccessfulResult();
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
  public ReturningSuccessfulResult returningSuccessfulResult() {
    return returningSuccessfulResult;
  }

  /**
   * Test case for only having a response with status code "default".
   */
  public Book getDefaultOnly(String testCaseSelector) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithBookEntityException {

    GetDefaultOnlySuccessfulResult response = returningSuccessfulResult.getDefaultOnly(testCaseSelector);

    return response.getEntity();
  }

  /**
   * Test case for only having a single successful response.
   */
  public Book getSuccessOnly() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    GetSuccessOnlySuccessfulResult response = returningSuccessfulResult.getSuccessOnly();

    return response.getEntity();
  }

  /**
   * Test case for only having a single failure response.
   */
  public void getFailureOnly() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException, ApiClientErrorWithCtcErrorEntityException {

    GetFailureOnlySuccessfulResult response = returningSuccessfulResult.getFailureOnly();
  }

  /**
   * Test case for having one successful response with an entity and a default for all errors.
   */
  public Book getSuccessEntityAndErrorDefault(String testCaseSelector) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithCtcErrorEntityException {

    GetSuccessEntityAndErrorDefaultSuccessfulResult response = returningSuccessfulResult.getSuccessEntityAndErrorDefault(testCaseSelector);

    return response.getEntity();
  }

  /**
   * Test case for having multiple success entity types.
   */
  public GetMultipleSuccessEntitiesSuccessfulResult getMultipleSuccessEntities(
      String testCaseSelector) throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    GetMultipleSuccessEntitiesSuccessfulResult response = returningSuccessfulResult.getMultipleSuccessEntities(testCaseSelector);

    return response;
  }

  /**
   * Test case for having multiple successful responses without content.
   */
  public void getMultipleSuccessResponsesWithoutContent(String testCaseSelector) throws
      ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

    GetMultipleSuccessResponsesWithoutContentSuccessfulResult response = returningSuccessfulResult.getMultipleSuccessResponsesWithoutContent(testCaseSelector);
  }

  /**
   * Test case for having multiple error entity types.
   */
  public Book getMultipleErrorEntities(String testCaseSelector) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithCtcErrorEntityException, ApiClientErrorWithSevereCtcErrorEntityException {

    GetMultipleErrorEntitiesSuccessfulResult response = returningSuccessfulResult.getMultipleErrorEntities(testCaseSelector);

    return response.getEntity();
  }

  /**
   * Test case for returning content with status code 204.
   */
  public void getContentFor204() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    GetContentFor204SuccessfulResult response = returningSuccessfulResult.getContentFor204();
  }

  /**
   * Contains methods for all operations returning instances of operation specific success classes and throwing exceptions for unsuccessful status codes.
   */
  public class ReturningSuccessfulResult {
    /**
     * Test case for only having a response with status code "default".
     */
    public GetDefaultOnlySuccessfulResult getDefaultOnly(String testCaseSelector) throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException,
        ApiClientErrorWithBookEntityException {

      ApiResponse response = returningAnyResponse.getDefaultOnly(testCaseSelector);

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithBookEntityException(response);
      }

      return new GetDefaultOnlySuccessfulResult(response);
    }

    /**
     * Test case for only having a single successful response.
     */
    public GetSuccessOnlySuccessfulResult getSuccessOnly() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      ApiResponse response = returningAnyResponse.getSuccessOnly();

      return new GetSuccessOnlySuccessfulResult(response);
    }

    /**
     * Test case for only having a single failure response.
     */
    public GetFailureOnlySuccessfulResult getFailureOnly() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException,
        ApiClientErrorWithCtcErrorEntityException {

      ApiResponse response = returningAnyResponse.getFailureOnly();

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithCtcErrorEntityException(response);
      }

      return new GetFailureOnlySuccessfulResult(response);
    }

    /**
     * Test case for having one successful response with an entity and a default for all errors.
     */
    public GetSuccessEntityAndErrorDefaultSuccessfulResult getSuccessEntityAndErrorDefault(
        String testCaseSelector) throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException, ApiClientErrorWithCtcErrorEntityException {

      ApiResponse response = returningAnyResponse.getSuccessEntityAndErrorDefault(testCaseSelector);

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithCtcErrorEntityException(response);
      }

      return new GetSuccessEntityAndErrorDefaultSuccessfulResult(response);
    }

    /**
     * Test case for having multiple success entity types.
     */
    public GetMultipleSuccessEntitiesSuccessfulResult getMultipleSuccessEntities(
        String testCaseSelector) throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      ApiResponse response = returningAnyResponse.getMultipleSuccessEntities(testCaseSelector);

      return new GetMultipleSuccessEntitiesSuccessfulResult(response);
    }

    /**
     * Test case for having multiple successful responses without content.
     */
    public GetMultipleSuccessResponsesWithoutContentSuccessfulResult getMultipleSuccessResponsesWithoutContent(
        String testCaseSelector) throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      ApiResponse response = returningAnyResponse.getMultipleSuccessResponsesWithoutContent(testCaseSelector);

      return new GetMultipleSuccessResponsesWithoutContentSuccessfulResult(response);
    }

    /**
     * Test case for having multiple error entity types.
     */
    public GetMultipleErrorEntitiesSuccessfulResult getMultipleErrorEntities(
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

      return new GetMultipleErrorEntitiesSuccessfulResult(response);
    }

    /**
     * Test case for returning content with status code 204.
     */
    public GetContentFor204SuccessfulResult getContentFor204() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      ApiResponse response = returningAnyResponse.getContentFor204();

      return new GetContentFor204SuccessfulResult(response);
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
  public static class GetDefaultOnlySuccessfulResult {
    private final ApiResponse response;

    public GetDefaultOnlySuccessfulResult(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getResponse() {
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
      GetDefaultOnlySuccessfulResult o = (GetDefaultOnlySuccessfulResult) other;
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
      return builder.replace(0, 2, "GetDefaultOnlySuccessfulResult{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation getSuccessOnly, i.e., the status code being in range 200 to 299.
   */
  public static class GetSuccessOnlySuccessfulResult {
    private final ApiResponse response;

    public GetSuccessOnlySuccessfulResult(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getResponse() {
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
      GetSuccessOnlySuccessfulResult o = (GetSuccessOnlySuccessfulResult) other;
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
      return builder.replace(0, 2, "GetSuccessOnlySuccessfulResult{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation getFailureOnly, i.e., the status code being in range 200 to 299.
   */
  public static class GetFailureOnlySuccessfulResult {
    private final ApiResponse response;

    public GetFailureOnlySuccessfulResult(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getResponse() {
      return response;
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetFailureOnlySuccessfulResult o = (GetFailureOnlySuccessfulResult) other;
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
      return builder.replace(0, 2, "GetFailureOnlySuccessfulResult{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation getSuccessEntityAndErrorDefault, i.e., the status code being in range 200 to 299.
   */
  public static class GetSuccessEntityAndErrorDefaultSuccessfulResult {
    private final ApiResponse response;

    public GetSuccessEntityAndErrorDefaultSuccessfulResult(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getResponse() {
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
      GetSuccessEntityAndErrorDefaultSuccessfulResult o = (GetSuccessEntityAndErrorDefaultSuccessfulResult) other;
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
      return builder.replace(0, 2, "GetSuccessEntityAndErrorDefaultSuccessfulResult{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation getMultipleSuccessEntities, i.e., the status code being in range 200 to 299.
   */
  public static class GetMultipleSuccessEntitiesSuccessfulResult {
    private final ApiResponse response;

    public GetMultipleSuccessEntitiesSuccessfulResult(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getResponse() {
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
      GetMultipleSuccessEntitiesSuccessfulResult o = (GetMultipleSuccessEntitiesSuccessfulResult) other;
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
      return builder.replace(0, 2, "GetMultipleSuccessEntitiesSuccessfulResult{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation getMultipleSuccessResponsesWithoutContent, i.e., the status code being in range 200 to 299.
   */
  public static class GetMultipleSuccessResponsesWithoutContentSuccessfulResult {
    private final ApiResponse response;

    public GetMultipleSuccessResponsesWithoutContentSuccessfulResult(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getResponse() {
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
      GetMultipleSuccessResponsesWithoutContentSuccessfulResult o = (GetMultipleSuccessResponsesWithoutContentSuccessfulResult) other;
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
      return builder.replace(0, 2, "GetMultipleSuccessResponsesWithoutContentSuccessfulResult{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation getMultipleErrorEntities, i.e., the status code being in range 200 to 299.
   */
  public static class GetMultipleErrorEntitiesSuccessfulResult {
    private final ApiResponse response;

    public GetMultipleErrorEntitiesSuccessfulResult(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getResponse() {
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
      GetMultipleErrorEntitiesSuccessfulResult o = (GetMultipleErrorEntitiesSuccessfulResult) other;
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
      return builder.replace(0, 2, "GetMultipleErrorEntitiesSuccessfulResult{").append('}').toString();
    }
  }

  /**
   * Represents a successful response of operation getContentFor204, i.e., the status code being in range 200 to 299.
   */
  public static class GetContentFor204SuccessfulResult {
    private final ApiResponse response;

    public GetContentFor204SuccessfulResult(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getResponse() {
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
      GetContentFor204SuccessfulResult o = (GetContentFor204SuccessfulResult) other;
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
      return builder.replace(0, 2, "GetContentFor204SuccessfulResult{").append('}').toString();
    }
  }
}
