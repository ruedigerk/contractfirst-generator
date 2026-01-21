package server_jsr305.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import server_jsr305.model.Failure;
import server_jsr305.model.ProblematicName;
import server_jsr305.resources.support.ResponseWrapper;

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
