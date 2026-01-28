package server_spring.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import server_spring.model.RestFailure;
import server_spring.model.RestProblematicName;
import server_spring.resources.support.ResponseWrapper;

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
      @RequestBody @NotNull @Valid RestProblematicName requestBody);

  class PostNonJavaPropertyNamesResponse extends ResponseWrapper {
    private PostNonJavaPropertyNamesResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static PostNonJavaPropertyNamesResponse with200ApplicationJson(
        RestProblematicName entity) {
      return new PostNonJavaPropertyNamesResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static PostNonJavaPropertyNamesResponse withApplicationJson(int status,
        RestFailure entity) {
      return new PostNonJavaPropertyNamesResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static PostNonJavaPropertyNamesResponse withCustomResponse(ResponseEntity response) {
      return new PostNonJavaPropertyNamesResponse(response);
    }
  }
}
