package validations_server_spring.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import validations_server_spring.model.Component;
import validations_server_spring.resources.support.ResponseWrapper;

@Validated
public interface V1ComponentsApi {
  /**
   * Get component.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/v1/components",
      produces = "application/json"
  )
  GetComponentResponse getComponent();

  class GetComponentResponse extends ResponseWrapper {
    private GetComponentResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetComponentResponse with200ApplicationJson(Component entity) {
      return new GetComponentResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static GetComponentResponse withCustomResponse(ResponseEntity response) {
      return new GetComponentResponse(response);
    }
  }
}
