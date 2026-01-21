package server.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import server.model.Item;
import server.resources.support.ResponseWrapper;

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
