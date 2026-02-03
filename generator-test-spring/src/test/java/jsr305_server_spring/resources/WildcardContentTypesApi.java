package jsr305_server_spring.resources;

import jsr305_server_spring.model.Failure;
import jsr305_server_spring.resources.support.ResponseWrapper;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Validated
public interface WildcardContentTypesApi {
  /**
   * Test wildcard response content types.
   *
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/wildcardContentTypes",
      produces = {
          "application/*",
          "application/json",
          "text/*"
      }
  )
  GetWildcardContentTypesResponse getWildcardContentTypes(
      @RequestHeader(name = "testCaseSelector", required = false) String testCaseSelector);

  class GetWildcardContentTypesResponse extends ResponseWrapper {
    private GetWildcardContentTypesResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetWildcardContentTypesResponse with200TextStar(String entity) {
      return new GetWildcardContentTypesResponse(ResponseEntity.status(200).header("Content-Type", "text/*").body(entity));
    }

    public static GetWildcardContentTypesResponse with200ApplicationStar(Resource entity) {
      return new GetWildcardContentTypesResponse(ResponseEntity.status(200).header("Content-Type", "application/*").body(entity));
    }

    public static GetWildcardContentTypesResponse withApplicationJson(int status, Failure entity) {
      return new GetWildcardContentTypesResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static GetWildcardContentTypesResponse withCustomResponse(ResponseEntity response) {
      return new GetWildcardContentTypesResponse(response);
    }
  }
}
