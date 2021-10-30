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

  private final ReturningResult returningResult;

  public ContentTypeCombinationsApiClient(ApiRequestExecutor requestExecutor) {
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
   * Test case for only having a response with status code "default".
   */
  public Book getDefaultOnly(String testCaseSelector) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithBookEntityException {

    GetDefaultOnlyResult result = returningResult.getDefaultOnly(testCaseSelector);

    if (!result.isSuccessful()) {
      throw new ApiClientErrorWithBookEntityException(result.getResponse());
    }

    return result.getEntity();
  }

  /**
   * Test case for only having a single successful response.
   */
  public Book getSuccessOnly() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    GetSuccessOnlyResult result = returningResult.getSuccessOnly();

    return result.getEntity();
  }

  /**
   * Test case for only having a single failure response.
   */
  public void getFailureOnly() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException, ApiClientErrorWithCtcErrorEntityException {

    GetFailureOnlyResult result = returningResult.getFailureOnly();

    if (!result.isSuccessful()) {
      throw new ApiClientErrorWithCtcErrorEntityException(result.getResponse());
    }
  }

  /**
   * Test case for having one successful response with an entity and a default for all errors.
   */
  public Book getSuccessEntityAndErrorDefault(String testCaseSelector) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithCtcErrorEntityException {

    GetSuccessEntityAndErrorDefaultResult result = returningResult.getSuccessEntityAndErrorDefault(testCaseSelector);

    if (!result.isSuccessful()) {
      throw new ApiClientErrorWithCtcErrorEntityException(result.getResponse());
    }

    return result.getEntityAsBook();
  }

  /**
   * Test case for having multiple success entity types.
   */
  public GetMultipleSuccessEntitiesResult getMultipleSuccessEntities(String testCaseSelector) throws
      ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

    GetMultipleSuccessEntitiesResult result = returningResult.getMultipleSuccessEntities(testCaseSelector);

    return result;
  }

  /**
   * Test case for having multiple successful responses without content.
   */
  public void getMultipleSuccessResponsesWithoutContent(String testCaseSelector) throws
      ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

    GetMultipleSuccessResponsesWithoutContentResult result = returningResult.getMultipleSuccessResponsesWithoutContent(testCaseSelector);
  }

  /**
   * Test case for having multiple error entity types.
   */
  public Book getMultipleErrorEntities(String testCaseSelector) throws ApiClientIoException,
      ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithCtcErrorEntityException, ApiClientErrorWithSevereCtcErrorEntityException {

    GetMultipleErrorEntitiesResult result = returningResult.getMultipleErrorEntities(testCaseSelector);

    if (!result.isSuccessful()) {
      if (result.getResponse().getEntityType() == CtcError.class) {
        throw new ApiClientErrorWithCtcErrorEntityException(result.getResponse());
      }
      throw new ApiClientErrorWithSevereCtcErrorEntityException(result.getResponse());
    }

    return result.getEntityAsBook();
  }

  /**
   * Test case for returning content with status code 204.
   */
  public void getContentFor204() throws ApiClientIoException, ApiClientValidationException,
      ApiClientIncompatibleResponseException {

    GetContentFor204Result result = returningResult.getContentFor204();
  }

  /**
   * Contains methods returning operation specific result classes, allowing inspection of the operations' responses.
   */
  public class ReturningResult {
    /**
     * Test case for only having a response with status code "default".
     */
    public GetDefaultOnlyResult getDefaultOnly(String testCaseSelector) throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/defaultOnly", "GET");

      builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

      builder.response(StatusCode.DEFAULT, "application/json", Book.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new GetDefaultOnlyResult(response);
    }

    /**
     * Test case for only having a single successful response.
     */
    public GetSuccessOnlyResult getSuccessOnly() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/successOnly", "GET");

      builder.response(StatusCode.of(200), "application/json", Book.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new GetSuccessOnlyResult(response);
    }

    /**
     * Test case for only having a single failure response.
     */
    public GetFailureOnlyResult getFailureOnly() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/failureOnly", "GET");

      builder.response(StatusCode.of(400), "application/json", CtcError.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new GetFailureOnlyResult(response);
    }

    /**
     * Test case for having one successful response with an entity and a default for all errors.
     */
    public GetSuccessEntityAndErrorDefaultResult getSuccessEntityAndErrorDefault(
        String testCaseSelector) throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/successEntityAndErrorDefault", "GET");

      builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

      builder.response(StatusCode.of(200), "application/json", Book.class);
      builder.response(StatusCode.DEFAULT, "application/json", CtcError.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new GetSuccessEntityAndErrorDefaultResult(response);
    }

    /**
     * Test case for having multiple success entity types.
     */
    public GetMultipleSuccessEntitiesResult getMultipleSuccessEntities(String testCaseSelector)
        throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/multipleSuccessEntities", "GET");

      builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

      builder.response(StatusCode.of(200), "application/json", Book.class);
      builder.response(StatusCode.of(201), "application/json", CtcError.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new GetMultipleSuccessEntitiesResult(response);
    }

    /**
     * Test case for having multiple successful responses without content.
     */
    public GetMultipleSuccessResponsesWithoutContentResult getMultipleSuccessResponsesWithoutContent(
        String testCaseSelector) throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/multipleSuccessResponsesWithoutContent", "GET");

      builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

      builder.response(StatusCode.of(200));
      builder.response(StatusCode.of(204));

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new GetMultipleSuccessResponsesWithoutContentResult(response);
    }

    /**
     * Test case for having multiple error entity types.
     */
    public GetMultipleErrorEntitiesResult getMultipleErrorEntities(String testCaseSelector) throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/multipleErrorEntities", "GET");

      builder.parameter("testCaseSelector", ParameterLocation.HEADER, false, testCaseSelector);

      builder.response(StatusCode.of(200), "application/json", Book.class);
      builder.response(StatusCode.of(400), "application/json", CtcError.class);
      builder.response(StatusCode.of(500), "application/json", SevereCtcError.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new GetMultipleErrorEntitiesResult(response);
    }

    /**
     * Test case for returning content with status code 204.
     */
    public GetContentFor204Result getContentFor204() throws ApiClientIoException,
        ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/contentFor204", "GET");

      builder.response(StatusCode.of(204));

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new GetContentFor204Result(response);
    }
  }

  /**
   * Represents the result of calling operation getDefaultOnly.
   */
  public static class GetDefaultOnlyResult {
    private final ApiResponse response;

    public GetDefaultOnlyResult(ApiResponse response) {
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
      GetDefaultOnlyResult o = (GetDefaultOnlyResult) other;
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
      return builder.replace(0, 2, "GetDefaultOnlyResult{").append('}').toString();
    }
  }

  /**
   * Represents the result of calling operation getSuccessOnly.
   */
  public static class GetSuccessOnlyResult {
    private final ApiResponse response;

    public GetSuccessOnlyResult(ApiResponse response) {
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
      GetSuccessOnlyResult o = (GetSuccessOnlyResult) other;
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
      return builder.replace(0, 2, "GetSuccessOnlyResult{").append('}').toString();
    }
  }

  /**
   * Represents the result of calling operation getFailureOnly.
   */
  public static class GetFailureOnlyResult {
    private final ApiResponse response;

    public GetFailureOnlyResult(ApiResponse response) {
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
     * Returns whether the response's status code is 400, while the response's entity is of type {@code CtcError}.
     */
    public boolean isStatus400ReturningCtcError() {
      return response.getStatusCode() == 400 && response.getEntityType() == CtcError.class;
    }

    /**
     * Returns the response's entity of type {@code CtcError}.
     */
    public CtcError getEntity() {
      return (CtcError) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetFailureOnlyResult o = (GetFailureOnlyResult) other;
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
      return builder.replace(0, 2, "GetFailureOnlyResult{").append('}').toString();
    }
  }

  /**
   * Represents the result of calling operation getSuccessEntityAndErrorDefault.
   */
  public static class GetSuccessEntityAndErrorDefaultResult {
    private final ApiResponse response;

    public GetSuccessEntityAndErrorDefaultResult(ApiResponse response) {
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
     * Returns whether the response's status code is 200, while the response's entity is of type {@code Book}.
     */
    public boolean isStatus200ReturningBook() {
      return response.getStatusCode() == 200 && response.getEntityType() == Book.class;
    }

    /**
     * Returns whether the response's entity is of type {@code CtcError}.
     */
    public boolean isReturningCtcError() {
      return response.getEntityType() == CtcError.class;
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
      GetSuccessEntityAndErrorDefaultResult o = (GetSuccessEntityAndErrorDefaultResult) other;
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
      return builder.replace(0, 2, "GetSuccessEntityAndErrorDefaultResult{").append('}').toString();
    }
  }

  /**
   * Represents the result of calling operation getMultipleSuccessEntities.
   */
  public static class GetMultipleSuccessEntitiesResult {
    private final ApiResponse response;

    public GetMultipleSuccessEntitiesResult(ApiResponse response) {
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
      GetMultipleSuccessEntitiesResult o = (GetMultipleSuccessEntitiesResult) other;
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
      return builder.replace(0, 2, "GetMultipleSuccessEntitiesResult{").append('}').toString();
    }
  }

  /**
   * Represents the result of calling operation getMultipleSuccessResponsesWithoutContent.
   */
  public static class GetMultipleSuccessResponsesWithoutContentResult {
    private final ApiResponse response;

    public GetMultipleSuccessResponsesWithoutContentResult(ApiResponse response) {
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
      GetMultipleSuccessResponsesWithoutContentResult o = (GetMultipleSuccessResponsesWithoutContentResult) other;
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
      return builder.replace(0, 2, "GetMultipleSuccessResponsesWithoutContentResult{").append('}').toString();
    }
  }

  /**
   * Represents the result of calling operation getMultipleErrorEntities.
   */
  public static class GetMultipleErrorEntitiesResult {
    private final ApiResponse response;

    public GetMultipleErrorEntitiesResult(ApiResponse response) {
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
     * Returns whether the response's status code is 200, while the response's entity is of type {@code Book}.
     */
    public boolean isStatus200ReturningBook() {
      return response.getStatusCode() == 200 && response.getEntityType() == Book.class;
    }

    /**
     * Returns whether the response's status code is 400, while the response's entity is of type {@code CtcError}.
     */
    public boolean isStatus400ReturningCtcError() {
      return response.getStatusCode() == 400 && response.getEntityType() == CtcError.class;
    }

    /**
     * Returns whether the response's status code is 500, while the response's entity is of type {@code SevereCtcError}.
     */
    public boolean isStatus500ReturningSevereCtcError() {
      return response.getStatusCode() == 500 && response.getEntityType() == SevereCtcError.class;
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

    /**
     * Returns the response's entity wrapped in {@code java.lang.Optional.of()} if it is of type {@code SevereCtcError}. Otherwise, returns {@code Optional.empty()}.
     */
    public Optional<SevereCtcError> getEntityIfSevereCtcError() {
      return Optional.ofNullable(getEntityAsSevereCtcError());
    }

    /**
     * Returns the response's entity if it is of type {@code SevereCtcError}. Otherwise, returns null.
     */
    public SevereCtcError getEntityAsSevereCtcError() {
      if (response.getEntityType() == SevereCtcError.class) {
        return (SevereCtcError) response.getEntity();
      } else {
        return null;
      }
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      GetMultipleErrorEntitiesResult o = (GetMultipleErrorEntitiesResult) other;
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
      return builder.replace(0, 2, "GetMultipleErrorEntitiesResult{").append('}').toString();
    }
  }

  /**
   * Represents the result of calling operation getContentFor204.
   */
  public static class GetContentFor204Result {
    private final ApiResponse response;

    public GetContentFor204Result(ApiResponse response) {
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
      GetContentFor204Result o = (GetContentFor204Result) other;
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
      return builder.replace(0, 2, "GetContentFor204Result{").append('}').toString();
    }
  }
}
