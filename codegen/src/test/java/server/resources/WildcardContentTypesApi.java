package server.resources;

import java.io.InputStream;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import server.model.Error;
import server.resources.support.ResponseWrapper;

@Path("")
public interface WildcardContentTypesApi {
  /**
   * Test wildcard response content types.
   *
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  @GET
  @Path("/wildcardContentTypes")
  @Produces({
      "application/*",
      "application/json",
      "text/*"
  })
  GetWildcardContentTypesResponse getWildcardContentTypes(
      @HeaderParam("testCaseSelector") String testCaseSelector);

  class GetWildcardContentTypesResponse extends ResponseWrapper {
    private GetWildcardContentTypesResponse(Response delegate) {
      super(delegate);
    }

    public static GetWildcardContentTypesResponse with200TextStar(String entity) {
      return new GetWildcardContentTypesResponse(Response.status(200).header("Content-Type", "text/*").entity(entity).build());
    }

    public static GetWildcardContentTypesResponse with200ApplicationStar(InputStream entity) {
      return new GetWildcardContentTypesResponse(Response.status(200).header("Content-Type", "application/*").entity(entity).build());
    }

    public static GetWildcardContentTypesResponse withApplicationJson(int status, Error entity) {
      return new GetWildcardContentTypesResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetWildcardContentTypesResponse withCustomResponse(Response response) {
      return new GetWildcardContentTypesResponse(response);
    }
  }
}
