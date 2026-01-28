package server_spring.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import server_spring.model.RestFailure;
import server_spring.model.RestItem;
import server_spring.resources.support.ResponseWrapper;

@Validated
public interface ResponseVariantsApi {
  /**
   * Test for the various parameter locations and for serializing request and response body entities.
   *
   * @param systemId ID of the system to create the item in.
   * @param dryRun Do a dry run?
   * @param partNumber Optional part number
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/{systemId}/components",
      consumes = "application/json",
      produces = "application/json"
  )
  CreateItemResponse createItem(@PathVariable("systemId") @NotNull String systemId,
      @RequestParam("dryRun") Boolean dryRun, @RequestHeader("partNumber") Long partNumber,
      @RequestHeader("testCaseSelector") String testCaseSelector,
      @RequestBody @NotNull @Valid RestItem requestBody);

  class CreateItemResponse extends ResponseWrapper {
    private CreateItemResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static CreateItemResponse with200ApplicationJson(RestItem entity) {
      return new CreateItemResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static CreateItemResponse with201() {
      return new CreateItemResponse(ResponseEntity.status(201).build());
    }

    public static CreateItemResponse with204() {
      return new CreateItemResponse(ResponseEntity.status(204).build());
    }

    public static CreateItemResponse with400ApplicationJson(RestFailure entity) {
      return new CreateItemResponse(ResponseEntity.status(400).header("Content-Type", "application/json").body(entity));
    }

    public static CreateItemResponse withApplicationJson(int status, RestFailure entity) {
      return new CreateItemResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static CreateItemResponse withCustomResponse(ResponseEntity response) {
      return new CreateItemResponse(response);
    }
  }
}
