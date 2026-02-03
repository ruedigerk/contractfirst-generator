package jsr305_server_spring.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import jsr305_server_spring.model.Clock;
import jsr305_server_spring.model.ClockResponse;
import jsr305_server_spring.model.Failure;
import jsr305_server_spring.resources.support.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
  UpdateTimeResponse updateTime(
      @PathVariable(name = "timeId", required = true) @NotNull LocalDate timeId,
      @RequestParam(name = "queryTimeA", required = true) @NotNull LocalDate queryTimeA,
      @RequestParam(name = "queryTimeB", required = true) @NotNull OffsetDateTime queryTimeB,
      @RequestHeader(name = "headerTimeA", required = true) @NotNull LocalDate headerTimeA,
      @RequestHeader(name = "headerTimeB", required = true) @NotNull OffsetDateTime headerTimeB,
      @RequestBody @NotNull @Valid Clock requestBody);

  class UpdateTimeResponse extends ResponseWrapper {
    private UpdateTimeResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static UpdateTimeResponse with200ApplicationJson(ClockResponse entity) {
      return new UpdateTimeResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static UpdateTimeResponse withApplicationJson(int status, Failure entity) {
      return new UpdateTimeResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static UpdateTimeResponse withCustomResponse(ResponseEntity response) {
      return new UpdateTimeResponse(response);
    }
  }
}
