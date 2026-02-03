package combinations_server_spring.resources;

import combinations_server_spring.model.Book;
import combinations_server_spring.model.CtcError;
import combinations_server_spring.model.SevereCtcError;
import combinations_server_spring.resources.support.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Validated
public interface ContentTypeCombinationsApi {
  /**
   * Test case for only having a response with status code "default".
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/defaultOnly",
      produces = "application/json"
  )
  GetDefaultOnlyResponse getDefaultOnly(
      @RequestHeader(name = "testCaseSelector", required = false) String testCaseSelector);

  /**
   * Test case for only having a single successful response.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/successOnly",
      produces = "application/json"
  )
  GetSuccessOnlyResponse getSuccessOnly();

  /**
   * Test case for only having a single failure response.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/failureOnly",
      produces = "application/json"
  )
  GetFailureOnlyResponse getFailureOnly();

  /**
   * Test case for having one successful response with an entity and a default for all errors.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/successEntityAndErrorDefault",
      produces = "application/json"
  )
  GetSuccessEntityAndErrorDefaultResponse getSuccessEntityAndErrorDefault(
      @RequestHeader(name = "testCaseSelector", required = false) String testCaseSelector);

  /**
   * Test case for having multiple success entity types.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/multipleSuccessEntities",
      produces = "application/json"
  )
  GetMultipleSuccessEntitiesResponse getMultipleSuccessEntities(
      @RequestHeader(name = "testCaseSelector", required = false) String testCaseSelector);

  /**
   * Test case for having multiple successful responses without content.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/multipleSuccessResponsesWithoutContent"
  )
  GetMultipleSuccessResponsesWithoutContentResponse getMultipleSuccessResponsesWithoutContent(
      @RequestHeader(name = "testCaseSelector", required = false) String testCaseSelector);

  /**
   * Test case for having multiple error entity types.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/multipleErrorEntities",
      produces = "application/json"
  )
  GetMultipleErrorEntitiesResponse getMultipleErrorEntities(
      @RequestHeader(name = "testCaseSelector", required = false) String testCaseSelector);

  /**
   * Test case for returning content with status code 204.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/contentFor204"
  )
  GetContentFor204Response getContentFor204();

  class GetDefaultOnlyResponse extends ResponseWrapper {
    private GetDefaultOnlyResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetDefaultOnlyResponse withApplicationJson(int status, Book entity) {
      return new GetDefaultOnlyResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static GetDefaultOnlyResponse withCustomResponse(ResponseEntity response) {
      return new GetDefaultOnlyResponse(response);
    }
  }

  class GetSuccessOnlyResponse extends ResponseWrapper {
    private GetSuccessOnlyResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetSuccessOnlyResponse with200ApplicationJson(Book entity) {
      return new GetSuccessOnlyResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static GetSuccessOnlyResponse withCustomResponse(ResponseEntity response) {
      return new GetSuccessOnlyResponse(response);
    }
  }

  class GetFailureOnlyResponse extends ResponseWrapper {
    private GetFailureOnlyResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetFailureOnlyResponse with400ApplicationJson(CtcError entity) {
      return new GetFailureOnlyResponse(ResponseEntity.status(400).header("Content-Type", "application/json").body(entity));
    }

    public static GetFailureOnlyResponse withCustomResponse(ResponseEntity response) {
      return new GetFailureOnlyResponse(response);
    }
  }

  class GetSuccessEntityAndErrorDefaultResponse extends ResponseWrapper {
    private GetSuccessEntityAndErrorDefaultResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetSuccessEntityAndErrorDefaultResponse with200ApplicationJson(Book entity) {
      return new GetSuccessEntityAndErrorDefaultResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static GetSuccessEntityAndErrorDefaultResponse withApplicationJson(int status,
        CtcError entity) {
      return new GetSuccessEntityAndErrorDefaultResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static GetSuccessEntityAndErrorDefaultResponse withCustomResponse(
        ResponseEntity response) {
      return new GetSuccessEntityAndErrorDefaultResponse(response);
    }
  }

  class GetMultipleSuccessEntitiesResponse extends ResponseWrapper {
    private GetMultipleSuccessEntitiesResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetMultipleSuccessEntitiesResponse with200ApplicationJson(Book entity) {
      return new GetMultipleSuccessEntitiesResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static GetMultipleSuccessEntitiesResponse with201ApplicationJson(CtcError entity) {
      return new GetMultipleSuccessEntitiesResponse(ResponseEntity.status(201).header("Content-Type", "application/json").body(entity));
    }

    public static GetMultipleSuccessEntitiesResponse withCustomResponse(ResponseEntity response) {
      return new GetMultipleSuccessEntitiesResponse(response);
    }
  }

  class GetMultipleSuccessResponsesWithoutContentResponse extends ResponseWrapper {
    private GetMultipleSuccessResponsesWithoutContentResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetMultipleSuccessResponsesWithoutContentResponse with200() {
      return new GetMultipleSuccessResponsesWithoutContentResponse(ResponseEntity.status(200).build());
    }

    public static GetMultipleSuccessResponsesWithoutContentResponse with204() {
      return new GetMultipleSuccessResponsesWithoutContentResponse(ResponseEntity.status(204).build());
    }

    public static GetMultipleSuccessResponsesWithoutContentResponse withCustomResponse(
        ResponseEntity response) {
      return new GetMultipleSuccessResponsesWithoutContentResponse(response);
    }
  }

  class GetMultipleErrorEntitiesResponse extends ResponseWrapper {
    private GetMultipleErrorEntitiesResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetMultipleErrorEntitiesResponse with200ApplicationJson(Book entity) {
      return new GetMultipleErrorEntitiesResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static GetMultipleErrorEntitiesResponse with400ApplicationJson(CtcError entity) {
      return new GetMultipleErrorEntitiesResponse(ResponseEntity.status(400).header("Content-Type", "application/json").body(entity));
    }

    public static GetMultipleErrorEntitiesResponse with500ApplicationJson(SevereCtcError entity) {
      return new GetMultipleErrorEntitiesResponse(ResponseEntity.status(500).header("Content-Type", "application/json").body(entity));
    }

    public static GetMultipleErrorEntitiesResponse withCustomResponse(ResponseEntity response) {
      return new GetMultipleErrorEntitiesResponse(response);
    }
  }

  class GetContentFor204Response extends ResponseWrapper {
    private GetContentFor204Response(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetContentFor204Response with204() {
      return new GetContentFor204Response(ResponseEntity.status(204).build());
    }

    public static GetContentFor204Response withCustomResponse(ResponseEntity response) {
      return new GetContentFor204Response(response);
    }
  }
}
