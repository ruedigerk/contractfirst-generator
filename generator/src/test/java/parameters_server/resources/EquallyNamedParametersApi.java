package parameters_server.resources;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
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
