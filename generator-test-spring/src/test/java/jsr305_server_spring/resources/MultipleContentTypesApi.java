package jsr305_server_spring.resources;

import jsr305_server_spring.model.Failure;
import jsr305_server_spring.model.Manual;
import jsr305_server_spring.resources.support.ResponseWrapper;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Validated
public interface MultipleContentTypesApi {
  /**
   * Test case for multiple response content types with different schemas.
   *
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/manuals",
      produces = {
          "application/json",
          "application/pdf",
          "text/plain"
      }
  )
  GetManualResponse getManual(@RequestHeader("testCaseSelector") String testCaseSelector);

  class GetManualResponse extends ResponseWrapper {
    private GetManualResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetManualResponse with200ApplicationJson(Manual entity) {
      return new GetManualResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static GetManualResponse with200ApplicationPdf(Resource entity) {
      return new GetManualResponse(ResponseEntity.status(200).header("Content-Type", "application/pdf").body(entity));
    }

    public static GetManualResponse with202TextPlain(String entity) {
      return new GetManualResponse(ResponseEntity.status(202).header("Content-Type", "text/plain").body(entity));
    }

    public static GetManualResponse with204() {
      return new GetManualResponse(ResponseEntity.status(204).build());
    }

    public static GetManualResponse withApplicationJson(int status, Failure entity) {
      return new GetManualResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static GetManualResponse withCustomResponse(ResponseEntity response) {
      return new GetManualResponse(response);
    }
  }
}
