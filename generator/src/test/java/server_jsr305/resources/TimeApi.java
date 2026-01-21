package server_jsr305.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import server_jsr305.model.Clock;
import server_jsr305.model.ClockResponse;
import server_jsr305.model.Failure;
import server_jsr305.resources.support.ResponseWrapper;

@Path("")
public interface TimeApi {
  /**
   * For testing handling of date and date-time formats.
   */
  @POST
  @Path("/time/{timeId}")
  @Consumes("application/json")
  @Produces("application/json")
  UpdateTimeResponse updateTime(@PathParam("timeId") @NotNull LocalDate timeId,
      @QueryParam("queryTimeA") @NotNull LocalDate queryTimeA,
      @QueryParam("queryTimeB") @NotNull OffsetDateTime queryTimeB,
      @HeaderParam("headerTimeA") @NotNull LocalDate headerTimeA,
      @HeaderParam("headerTimeB") @NotNull OffsetDateTime headerTimeB,
      @NotNull @Valid Clock requestBody);

  class UpdateTimeResponse extends ResponseWrapper {
    private UpdateTimeResponse(Response delegate) {
      super(delegate);
    }

    public static UpdateTimeResponse with200ApplicationJson(ClockResponse entity) {
      return new UpdateTimeResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static UpdateTimeResponse withApplicationJson(int status, Failure entity) {
      return new UpdateTimeResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static UpdateTimeResponse withCustomResponse(Response response) {
      return new UpdateTimeResponse(response);
    }
  }
}
