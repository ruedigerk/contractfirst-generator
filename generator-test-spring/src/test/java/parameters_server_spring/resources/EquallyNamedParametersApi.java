package parameters_server_spring.resources;

import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import parameters_server_spring.resources.support.ResponseWrapper;

@Validated
public interface EquallyNamedParametersApi {
  /**
   * Test for multiple equally named parameters.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/getEquallyNamedParameters/{theParameter}",
      consumes = "application/x-www-form-urlencoded"
  )
  GetEquallyNamedParametersResponse getEquallyNamedParameters(
      @CookieValue("theParameter") String theParameterInCookie,
      @RequestHeader("theParameter") String theParameterInHeader,
      @PathVariable("theParameter") @NotNull String theParameterInPath,
      @RequestParam("theParameter") String theParameterInQuery,
      @RequestPart(value = "theParameter", required = false) String theParameterInBody,
      @RequestPart(value = "other", required = false) String other);

  class GetEquallyNamedParametersResponse extends ResponseWrapper {
    private GetEquallyNamedParametersResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetEquallyNamedParametersResponse with204() {
      return new GetEquallyNamedParametersResponse(ResponseEntity.status(204).build());
    }

    public static GetEquallyNamedParametersResponse withCustomResponse(ResponseEntity response) {
      return new GetEquallyNamedParametersResponse(response);
    }
  }
}
