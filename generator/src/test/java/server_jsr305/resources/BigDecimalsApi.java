package server_jsr305.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import server_jsr305.model.Failure;
import server_jsr305.resources.support.ResponseWrapper;

@Path("")
public interface BigDecimalsApi {
  /**
   * Test serialization of schema type number as BigDecimal.
   *
   * @param decimalNumber Test BigDecimal
   */
  @GET
  @Path("/bigDecimals")
  @Produces("application/json")
  GetNumberResponse getNumber(@QueryParam("decimalNumber") @NotNull BigDecimal decimalNumber);

  class GetNumberResponse extends ResponseWrapper {
    private GetNumberResponse(Response delegate) {
      super(delegate);
    }

    public static GetNumberResponse with200ApplicationJson(BigDecimal entity) {
      return new GetNumberResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetNumberResponse withApplicationJson(int status, Failure entity) {
      return new GetNumberResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetNumberResponse withCustomResponse(Response response) {
      return new GetNumberResponse(response);
    }
  }
}
