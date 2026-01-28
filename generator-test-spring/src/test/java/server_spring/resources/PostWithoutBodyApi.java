package server_spring.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import server_spring.resources.support.ResponseWrapper;

@Validated
public interface PostWithoutBodyApi {
  /**
   * Testing HTTP method POST without a request body.
   */
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/postWithoutBody/post"
  )
  PostWithoutBodyResponse postWithoutBody();

  /**
   * Testing HTTP method PUT without a request body.
   */
  @RequestMapping(
      method = RequestMethod.PUT,
      value = "/postWithoutBody/put"
  )
  PutWithoutBodyResponse putWithoutBody();

  /**
   * Testing HTTP method PATCH without a request body.
   */
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/postWithoutBody/patch"
  )
  PatchWithoutBodyResponse patchWithoutBody();

  class PostWithoutBodyResponse extends ResponseWrapper {
    private PostWithoutBodyResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static PostWithoutBodyResponse with204() {
      return new PostWithoutBodyResponse(ResponseEntity.status(204).build());
    }

    public static PostWithoutBodyResponse withCustomResponse(ResponseEntity response) {
      return new PostWithoutBodyResponse(response);
    }
  }

  class PutWithoutBodyResponse extends ResponseWrapper {
    private PutWithoutBodyResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static PutWithoutBodyResponse with204() {
      return new PutWithoutBodyResponse(ResponseEntity.status(204).build());
    }

    public static PutWithoutBodyResponse withCustomResponse(ResponseEntity response) {
      return new PutWithoutBodyResponse(response);
    }
  }

  class PatchWithoutBodyResponse extends ResponseWrapper {
    private PatchWithoutBodyResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static PatchWithoutBodyResponse with204() {
      return new PatchWithoutBodyResponse(ResponseEntity.status(204).build());
    }

    public static PatchWithoutBodyResponse withCustomResponse(ResponseEntity response) {
      return new PatchWithoutBodyResponse(response);
    }
  }
}
