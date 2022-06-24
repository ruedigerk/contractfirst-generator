package server_jsr305.resources;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
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
