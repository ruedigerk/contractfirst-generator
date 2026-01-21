package server_jsr305.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import server_jsr305.model.Failure;
import server_jsr305.resources.support.ResponseWrapper;

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

    public static GetWildcardContentTypesResponse withApplicationJson(int status, Failure entity) {
      return new GetWildcardContentTypesResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetWildcardContentTypesResponse withCustomResponse(Response response) {
      return new GetWildcardContentTypesResponse(response);
    }
  }
}
