package client.api;

import client.model.Clock;
import client.model.ClockResponse;
import client.model.Failure;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIncompatibleResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.ApiRequestExecutor;
import io.github.ruedigerk.contractfirst.generator.client.ApiResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.ParameterLocation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Contains methods for all API operations tagged "time".
 */
public class TimeApiClient {
  private final ApiRequestExecutor requestExecutor;

  private final ReturningResult returningResult;

  public TimeApiClient(ApiRequestExecutor requestExecutor) {
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
   * For testing handling of date and date-time formats.
   */
  public ClockResponse updateTime(LocalDate timeId, LocalDate queryTimeA, OffsetDateTime queryTimeB,
      LocalDate headerTimeA, OffsetDateTime headerTimeB, Clock requestBody) throws
      ApiClientIoException, ApiClientValidationException, ApiClientIncompatibleResponseException,
      ApiClientErrorWithFailureEntityException {

    UpdateTimeResult result = returningResult.updateTime(timeId, queryTimeA, queryTimeB, headerTimeA, headerTimeB, requestBody);

    if (!result.isSuccessful()) {
      throw new ApiClientErrorWithFailureEntityException(result.getResponse());
    }

    return result.getEntityAsClockResponse();
  }

  /**
   * Contains methods returning operation specific result classes, allowing inspection of the operations' responses.
   */
  public class ReturningResult {
    /**
     * For testing handling of date and date-time formats.
     */
    public UpdateTimeResult updateTime(LocalDate timeId, LocalDate queryTimeA,
        OffsetDateTime queryTimeB, LocalDate headerTimeA, OffsetDateTime headerTimeB,
        Clock requestBody) throws ApiClientIoException, ApiClientValidationException,
        ApiClientIncompatibleResponseException {

      Operation.Builder builder = new Operation.Builder("/time/{timeId}", "POST");

      builder.parameter("timeId", ParameterLocation.PATH, true, timeId);
      builder.parameter("queryTimeA", ParameterLocation.QUERY, true, queryTimeA);
      builder.parameter("queryTimeB", ParameterLocation.QUERY, true, queryTimeB);
      builder.parameter("headerTimeA", ParameterLocation.HEADER, true, headerTimeA);
      builder.parameter("headerTimeB", ParameterLocation.HEADER, true, headerTimeB);
      builder.requestBody("application/json", true, requestBody);

      builder.response(StatusCode.of(200), "application/json", ClockResponse.class);
      builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

      ApiResponse response = requestExecutor.executeRequest(builder.build());

      return new UpdateTimeResult(response);
    }
  }

  /**
   * Represents the result of calling operation updateTime.
   */
  public static class UpdateTimeResult {
    private final ApiResponse response;

    public UpdateTimeResult(ApiResponse response) {
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
     * Returns whether the response's status code is 200, while the response's entity is of type {@code ClockResponse}.
     */
    public boolean isStatus200ReturningClockResponse() {
      return response.getStatusCode() == 200 && response.getEntityType() == ClockResponse.class;
    }

    /**
     * Returns whether the response's entity is of type {@code Failure}.
     */
    public boolean isReturningFailure() {
      return response.getEntityType() == Failure.class;
    }

    /**
     * Returns the response's entity wrapped in {@code java.lang.Optional.of()} if it is of type {@code ClockResponse}. Otherwise, returns {@code Optional.empty()}.
     */
    public Optional<ClockResponse> getEntityIfClockResponse() {
      return Optional.ofNullable(getEntityAsClockResponse());
    }

    /**
     * Returns the response's entity if it is of type {@code ClockResponse}. Otherwise, returns null.
     */
    public ClockResponse getEntityAsClockResponse() {
      if (response.getEntityType() == ClockResponse.class) {
        return (ClockResponse) response.getEntity();
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
      UpdateTimeResult o = (UpdateTimeResult) other;
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
      return builder.replace(0, 2, "UpdateTimeResult{").append('}').toString();
    }
  }
}
