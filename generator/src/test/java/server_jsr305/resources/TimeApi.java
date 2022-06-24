package server_jsr305.resources;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
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
