package parameters_server.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import parameters_server.resources.support.ResponseWrapper;

@Path("")
public interface EquallyNamedParametersApi {
  /**
   * Test for multiple equally named parameters.
   */
  @GET
  @Path("/getEquallyNamedParameters/{theParameter}")
  @Consumes("application/x-www-form-urlencoded")
  @Produces
  GetEquallyNamedParametersResponse getEquallyNamedParameters(
      @CookieParam("theParameter") String theParameterInCookie,
      @HeaderParam("theParameter") String theParameterInHeader,
      @PathParam("theParameter") @NotNull String theParameterInPath,
      @QueryParam("theParameter") String theParameterInQuery,
      @FormParam("theParameter") String theParameterInBody, @FormParam("other") String other);

  class GetEquallyNamedParametersResponse extends ResponseWrapper {
    private GetEquallyNamedParametersResponse(Response delegate) {
      super(delegate);
    }

    public static GetEquallyNamedParametersResponse with204() {
      return new GetEquallyNamedParametersResponse(Response.status(204).build());
    }

    public static GetEquallyNamedParametersResponse withCustomResponse(Response response) {
      return new GetEquallyNamedParametersResponse(response);
    }
  }
}
