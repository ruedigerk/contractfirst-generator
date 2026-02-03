package server_spring.resources;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import server_spring.model.RestFailure;
import server_spring.model.RestSimpleEnum;
import server_spring.resources.support.ResponseWrapper;

@Validated
public interface MultiValuedParametersApi {
  /**
   * Test for handling of multi-valued parameters.
   *
   * @param testSelector Selects the assertions to perform on the server.
   * @param pathParam Multi-valued path parameter.
   * @param queryParam Multi-valued query parameter.
   * @param headerParam Multi-valued header parameter.
   * @param pathSetParam Multi-valued path parameter.
   * @param querySetParam Multi-valued query parameter.
   * @param headerSetParam Multi-valued header parameter.
   */
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/multiValuedParametersTest/{pathParam}/{pathSetParam}",
      produces = "application/json"
  )
  MultiValuedParametersTestResponse multiValuedParametersTest(
      @RequestParam(name = "testSelector", required = true) @NotNull String testSelector,
      @PathVariable(name = "pathParam", required = true) @NotNull List<String> pathParam,
      @RequestParam(name = "queryParam", required = false) List<RestSimpleEnum> queryParam,
      @RequestHeader(name = "headerParam", required = false) List<Integer> headerParam,
      @PathVariable(name = "pathSetParam", required = true) @NotNull Set<String> pathSetParam,
      @RequestParam(name = "querySetParam", required = false) Set<RestSimpleEnum> querySetParam,
      @RequestHeader(name = "headerSetParam", required = false) Set<Integer> headerSetParam);

  class MultiValuedParametersTestResponse extends ResponseWrapper {
    private MultiValuedParametersTestResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static MultiValuedParametersTestResponse with204() {
      return new MultiValuedParametersTestResponse(ResponseEntity.status(204).build());
    }

    public static MultiValuedParametersTestResponse withApplicationJson(int status,
        RestFailure entity) {
      return new MultiValuedParametersTestResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static MultiValuedParametersTestResponse withCustomResponse(ResponseEntity response) {
      return new MultiValuedParametersTestResponse(response);
    }
  }
}
