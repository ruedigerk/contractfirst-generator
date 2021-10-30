package server.resources;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import server.resources.support.ResponseWrapper;

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
