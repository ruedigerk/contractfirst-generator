package server_spring.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import server_spring.model.RestClock;
import server_spring.model.RestClockResponse;
import server_spring.model.RestFailure;
import server_spring.resources.support.ResponseWrapper;

@Validated
public interface TimeApi {
  /**
   * For testing handling of date and date-time formats.
   */
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/time/{timeId}",
      consumes = "application/json",
      produces = "application/json"
  )
  UpdateTimeResponse updateTime(@PathVariable("timeId") @NotNull LocalDate timeId,
      @RequestParam("queryTimeA") @NotNull LocalDate queryTimeA,
      @RequestParam("queryTimeB") @NotNull OffsetDateTime queryTimeB,
      @RequestHeader("headerTimeA") @NotNull LocalDate headerTimeA,
      @RequestHeader("headerTimeB") @NotNull OffsetDateTime headerTimeB,
      @RequestBody @NotNull @Valid RestClock requestBody);

  class UpdateTimeResponse extends ResponseWrapper {
    private UpdateTimeResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static UpdateTimeResponse with200ApplicationJson(RestClockResponse entity) {
      return new UpdateTimeResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static UpdateTimeResponse withApplicationJson(int status, RestFailure entity) {
      return new UpdateTimeResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static UpdateTimeResponse withCustomResponse(ResponseEntity response) {
      return new UpdateTimeResponse(response);
    }
  }
}
