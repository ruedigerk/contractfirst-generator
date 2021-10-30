package server.resources;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import server.model.Failure;
import server.model.ProblematicName;
import server.resources.support.ResponseWrapper;

@Path("")
public interface NonJavaPropertyNamesApi {
  /**
   * A test case for a JSON model with properties that are not legal Java identifiers.
   */
  @POST
  @Path("/nonJavaPropertyNames")
  @Consumes("application/json")
  @Produces("application/json")
  PostNonJavaPropertyNamesResponse postNonJavaPropertyNames(
      @NotNull @Valid ProblematicName requestBody);

  class PostNonJavaPropertyNamesResponse extends ResponseWrapper {
    private PostNonJavaPropertyNamesResponse(Response delegate) {
      super(delegate);
    }

    public static PostNonJavaPropertyNamesResponse with200ApplicationJson(ProblematicName entity) {
      return new PostNonJavaPropertyNamesResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static PostNonJavaPropertyNamesResponse withApplicationJson(int status, Failure entity) {
      return new PostNonJavaPropertyNamesResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static PostNonJavaPropertyNamesResponse withCustomResponse(Response response) {
      return new PostNonJavaPropertyNamesResponse(response);
    }
  }
}
