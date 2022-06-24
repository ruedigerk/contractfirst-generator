package server_jsr305.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import server_jsr305.model.Item;
import server_jsr305.resources.support.ResponseWrapper;

@Path("")
public interface NonJsonEntityAsJsonApi {
  /**
   * Test case for an operation that declares a non-JSON entity and the server nevertheless sending the entity as JSON.
   */
  @GET
  @Path("/nonJsonEntityAsJson")
  @Produces("application/xml")
  GetNonJsonEntityAsJsonResponse getNonJsonEntityAsJson();

  class GetNonJsonEntityAsJsonResponse extends ResponseWrapper {
    private GetNonJsonEntityAsJsonResponse(Response delegate) {
      super(delegate);
    }

    public static GetNonJsonEntityAsJsonResponse with200ApplicationXml(Item entity) {
      return new GetNonJsonEntityAsJsonResponse(Response.status(200).header("Content-Type", "application/xml").entity(entity).build());
    }

    public static GetNonJsonEntityAsJsonResponse withCustomResponse(Response response) {
      return new GetNonJsonEntityAsJsonResponse(response);
    }
  }
}
