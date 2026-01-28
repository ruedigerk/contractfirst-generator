package jsr305_server_spring.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jsr305_server_spring.model.Failure;
import jsr305_server_spring.model.ProblematicName;
import jsr305_server_spring.resources.support.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Validated
public interface NonJavaPropertyNamesApi {
  /**
   * A test case for a JSON model with properties that are not legal Java identifiers.
   */
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/nonJavaPropertyNames",
      consumes = "application/json",
      produces = "application/json"
  )
  PostNonJavaPropertyNamesResponse postNonJavaPropertyNames(
      @RequestBody @NotNull @Valid ProblematicName requestBody);

  class PostNonJavaPropertyNamesResponse extends ResponseWrapper {
    private PostNonJavaPropertyNamesResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static PostNonJavaPropertyNamesResponse with200ApplicationJson(ProblematicName entity) {
      return new PostNonJavaPropertyNamesResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static PostNonJavaPropertyNamesResponse withApplicationJson(int status, Failure entity) {
      return new PostNonJavaPropertyNamesResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static PostNonJavaPropertyNamesResponse withCustomResponse(ResponseEntity response) {
      return new PostNonJavaPropertyNamesResponse(response);
    }
  }
}
