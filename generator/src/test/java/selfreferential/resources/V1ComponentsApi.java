package selfreferential.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import selfreferential.model.Model;
import selfreferential.resources.support.ResponseWrapper;

@Path("")
public interface V1ComponentsApi {
  /**
   * Get component.
   */
  @GET
  @Path("/v1/components")
  @Produces("application/json")
  GetComponentResponse getComponent();

  class GetComponentResponse extends ResponseWrapper {
    private GetComponentResponse(Response delegate) {
      super(delegate);
    }

    public static GetComponentResponse with200ApplicationJson(Model entity) {
      return new GetComponentResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetComponentResponse withCustomResponse(Response response) {
      return new GetComponentResponse(response);
    }
  }
}
