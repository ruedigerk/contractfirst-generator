package jsr305_server_spring.resources;

import java.util.List;
import jsr305_server_spring.model.Failure;
import jsr305_server_spring.model.GetInlineObjectInArrayResponse200ApplicationJsonItem;
import jsr305_server_spring.resources.support.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Validated
public interface TestcasesApi {
  /**
   * A test case for the SchemaToJavaTypeTransformer.
   */
  @RequestMapping(
      method = RequestMethod.GET,
      value = "/testcases",
      produces = "application/json"
  )
  GetInlineObjectInArrayResponse getInlineObjectInArray();

  /**
   * Testing that enums of a type different from string are supported by ignoring the enum part of the type.
   */
  @RequestMapping(
      method = RequestMethod.POST,
      value = "/nonStringEnumTypeIsIgnored"
  )
  NonStringEnumTypeIsIgnoredResponse nonStringEnumTypeIsIgnored(
      @RequestParam(name = "booleanEnum", required = false) Boolean booleanEnum);

  class GetInlineObjectInArrayResponse extends ResponseWrapper {
    private GetInlineObjectInArrayResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static GetInlineObjectInArrayResponse with200ApplicationJson(
        List<GetInlineObjectInArrayResponse200ApplicationJsonItem> entity) {
      return new GetInlineObjectInArrayResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static GetInlineObjectInArrayResponse withApplicationJson(int status, Failure entity) {
      return new GetInlineObjectInArrayResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static GetInlineObjectInArrayResponse withCustomResponse(ResponseEntity response) {
      return new GetInlineObjectInArrayResponse(response);
    }
  }

  class NonStringEnumTypeIsIgnoredResponse extends ResponseWrapper {
    private NonStringEnumTypeIsIgnoredResponse(ResponseEntity delegate) {
      super(delegate);
    }

    public static NonStringEnumTypeIsIgnoredResponse with204() {
      return new NonStringEnumTypeIsIgnoredResponse(ResponseEntity.status(204).build());
    }

    public static NonStringEnumTypeIsIgnoredResponse withCustomResponse(ResponseEntity response) {
      return new NonStringEnumTypeIsIgnoredResponse(response);
    }
  }
}
