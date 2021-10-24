package client.api;

import client.model.Clock;
import client.model.ClockResponse;
import client.model.Failure;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.RequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.ParameterLocation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Contains methods for all API operations tagged "time".
 */
public class TimeApiClient {
  private final RequestExecutor requestExecutor;

  private final ReturningAnyResponse returningAnyResponse;

  private final ReturningSuccessfulResponse returningSuccessfulResponse;

  public TimeApiClient(RequestExecutor requestExecutor) {
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
   * For testing handling of date and date-time formats.
   */
  public ClockResponse updateTime(LocalDate timeId, LocalDate queryTimeA, OffsetDateTime queryTimeB,
      LocalDate headerTimeA, OffsetDateTime headerTimeB, Clock requestBody) throws
      ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    UpdateTimeSuccessfulResponse response = returningSuccessfulResponse.updateTime(timeId, queryTimeA, queryTimeB, headerTimeA, headerTimeB, requestBody);

    return response.getEntity();
  }

  /**
   * Contains methods for all operations returning instances of operation specific success classes and throwing exceptions for unsuccessful status codes.
   */
  public class ReturningSuccessfulResponse {
    /**
     * For testing handling of date and date-time formats.
     */
    public UpdateTimeSuccessfulResponse updateTime(LocalDate timeId, LocalDate queryTimeA,
        OffsetDateTime queryTimeB, LocalDate headerTimeA, OffsetDateTime headerTimeB,
        Clock requestBody) throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException, ApiClientErrorWithFailureEntityException {

      ApiResponse response = returningAnyResponse.updateTime(timeId, queryTimeA, queryTimeB, headerTimeA, headerTimeB, requestBody);

      if (!response.isSuccessful()) {
        throw new ApiClientErrorWithFailureEntityException(response);
      }

      return new UpdateTimeSuccessfulResponse(response);
    }
  }

  /**
   * Contains methods for all operations returning instances of ApiResponse and not throwing exceptions for unsuccessful status codes.
   */
  public class ReturningAnyResponse {
    /**
     * For testing handling of date and date-time formats.
     */
    public ApiResponse updateTime(LocalDate timeId, LocalDate queryTimeA, OffsetDateTime queryTimeB,
        LocalDate headerTimeA, OffsetDateTime headerTimeB, Clock requestBody) throws
        ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/time/{timeId}", "POST");

      builder.parameter("timeId", ParameterLocation.PATH, true, timeId);
      builder.parameter("queryTimeA", ParameterLocation.QUERY, true, queryTimeA);
      builder.parameter("queryTimeB", ParameterLocation.QUERY, true, queryTimeB);
      builder.parameter("headerTimeA", ParameterLocation.HEADER, true, headerTimeA);
      builder.parameter("headerTimeB", ParameterLocation.HEADER, true, headerTimeB);
      builder.requestBody("application/json", true, requestBody);

      builder.response(StatusCode.of(200), "application/json", ClockResponse.class);
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      return requestExecutor.executeRequest(builder.build());
    }
  }

  /**
   * Represents a successful response of operation updateTime, i.e., the status code being in range 200 to 299.
   */
  public static class UpdateTimeSuccessfulResponse {
    private final ApiResponse response;

    public UpdateTimeSuccessfulResponse(ApiResponse response) {
      this.response = response;
    }

    /**
     * Returns the ApiResponse instance with the details of the operation's HTTP response.
     */
    public ApiResponse getApiResponse() {
      return response;
    }

    /**
     * Returns whether the response's status code is 200, while the response's entity is of type {@code ClockResponse}.
     */
    public boolean isStatus200ReturningClockResponse() {
      return response.getStatusCode() == 200 && response.getEntityType() == ClockResponse.class;
    }

    /**
     * Returns the response's entity of type {@code ClockResponse}.
     */
    public ClockResponse getEntity() {
      return (ClockResponse) response.getEntity();
    }

    @Override
    public boolean equals(Object other) {
      if (other == this) return true;
      if (other == null || getClass() != other.getClass()) return false;
      UpdateTimeSuccessfulResponse o = (UpdateTimeSuccessfulResponse) other;
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
      return builder.replace(0, 2, "UpdateTimeSuccessfulResponse{").append('}').toString();
    }
  }
}
