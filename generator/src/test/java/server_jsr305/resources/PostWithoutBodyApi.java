package server_jsr305.resources;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import server_jsr305.resources.support.ResponseWrapper;

@Path("")
public interface PostWithoutBodyApi {
  /**
   * Testing HTTP method POST without a request body.
   */
  @POST
  @Path("/postWithoutBody/post")
  @Produces
  PostWithoutBodyResponse postWithoutBody();

  /**
   * Testing HTTP method PUT without a request body.
   */
  @PUT
  @Path("/postWithoutBody/put")
  @Produces
  PutWithoutBodyResponse putWithoutBody();

  /**
   * Testing HTTP method PATCH without a request body.
   */
  @POST
  @Path("/postWithoutBody/patch")
  @Produces
  PatchWithoutBodyResponse patchWithoutBody();

  class PostWithoutBodyResponse extends ResponseWrapper {
    private PostWithoutBodyResponse(Response delegate) {
      super(delegate);
    }

    public static PostWithoutBodyResponse with204() {
      return new PostWithoutBodyResponse(Response.status(204).build());
    }

    public static PostWithoutBodyResponse withCustomResponse(Response response) {
      return new PostWithoutBodyResponse(response);
    }
  }

  class PutWithoutBodyResponse extends ResponseWrapper {
    private PutWithoutBodyResponse(Response delegate) {
      super(delegate);
    }

    public static PutWithoutBodyResponse with204() {
      return new PutWithoutBodyResponse(Response.status(204).build());
    }

    public static PutWithoutBodyResponse withCustomResponse(Response response) {
      return new PutWithoutBodyResponse(response);
    }
  }

  class PatchWithoutBodyResponse extends ResponseWrapper {
    private PatchWithoutBodyResponse(Response delegate) {
      super(delegate);
    }

    public static PatchWithoutBodyResponse with204() {
      return new PatchWithoutBodyResponse(Response.status(204).build());
    }

    public static PatchWithoutBodyResponse withCustomResponse(Response response) {
      return new PatchWithoutBodyResponse(response);
    }
  }
}
