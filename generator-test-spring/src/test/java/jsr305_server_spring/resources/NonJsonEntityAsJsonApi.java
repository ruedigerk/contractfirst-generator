package jsr305_server_spring.resources;

import jsr305_server_spring.model.Item;
import jsr305_server_spring.resources.support.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Validated
public interface NonJsonEntityAsJsonApi {
  /**
   * Test case for an operation that declares a non-JSON entity and the server nevertheless sending the entity as JSON.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/nonJsonEntityAsJson",
      produces = "application/xml"
  )
  GetNonJsonEntityAsJsonResponse getNonJsonEntityAsJson();

  class GetNonJsonEntityAsJsonResponse extends ResponseWrapper {
    private GetNonJsonEntityAsJsonResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetNonJsonEntityAsJsonResponse with200ApplicationXml(Item entity) {
      return new GetNonJsonEntityAsJsonResponse(ResponseEntity.status(200).header("Content-Type", "application/xml").body(entity));
    }

    public static GetNonJsonEntityAsJsonResponse withCustomResponse(ResponseEntity response) {
      return new GetNonJsonEntityAsJsonResponse(response);
    }
  }
}
