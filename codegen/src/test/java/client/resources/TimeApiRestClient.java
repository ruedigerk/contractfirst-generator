package client.resources;

import client.model.Clock;
import client.model.ClockResponse;
import client.model.Error;
import de.rk42.openapi.codegen.client.DefinedResponse;
import de.rk42.openapi.codegen.client.GenericResponse;
import de.rk42.openapi.codegen.client.RestClientIoException;
import de.rk42.openapi.codegen.client.RestClientSupport;
import de.rk42.openapi.codegen.client.RestClientUndefinedResponseException;
import de.rk42.openapi.codegen.client.RestClientValidationException;
import de.rk42.openapi.codegen.client.internal.Operation;
import de.rk42.openapi.codegen.client.internal.ParameterLocation;
import de.rk42.openapi.codegen.client.internal.StatusCode;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public class TimeApiRestClient {
  private final RestClientSupport support;

  public TimeApiRestClient(RestClientSupport support) {
    this.support = support;
  }

  /**
   * For testing handling of date and date-time formats.
   */
  public ClockResponse updateTime(LocalDate timeId, LocalDate queryTimeA, OffsetDateTime queryTimeB,
      LocalDate headerTimeA, OffsetDateTime headerTimeB, Clock requestBody) throws
      RestClientIoException, RestClientValidationException, RestClientUndefinedResponseException {

    GenericResponse genericResponse = updateTimeWithResponse(timeId, queryTimeA, queryTimeB, headerTimeA, headerTimeB, requestBody);
    DefinedResponse response = genericResponse.asDefinedResponse();

    if (!response.isSuccessful()) {
      throw new RestClientErrorEntityException(response.getStatusCode(), (Error) response.getEntity());
    }

    return (ClockResponse) response.getEntity();
  }

  /**
   * For testing handling of date and date-time formats.
   */
  public GenericResponse updateTimeWithResponse(LocalDate timeId, LocalDate queryTimeA,
      OffsetDateTime queryTimeB, LocalDate headerTimeA, OffsetDateTime headerTimeB,
      Clock requestBody) throws RestClientIoException, RestClientValidationException {

    Operation.Builder builder = new Operation.Builder("/time/{timeId}", "POST");

    builder.parameter("timeId", ParameterLocation.PATH, true, timeId);
    builder.parameter("queryTimeA", ParameterLocation.QUERY, true, queryTimeA);
    builder.parameter("queryTimeB", ParameterLocation.QUERY, true, queryTimeB);
    builder.parameter("headerTimeA", ParameterLocation.HEADER, true, headerTimeA);
    builder.parameter("headerTimeB", ParameterLocation.HEADER, true, headerTimeB);
    builder.requestBody("application/json", true, requestBody);

    builder.response(StatusCode.of(200), "application/json", ClockResponse.class);
    builder.response(StatusCode.DEFAULT, "application/json", Error.class);

    return support.executeRequest(builder.build());
  }
}
