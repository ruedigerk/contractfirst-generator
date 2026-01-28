package jsr305_server_spring.resources;

import java.util.List;
import jsr305_server_spring.model.Failure;
import jsr305_server_spring.model.GetInlineObjectInArrayResponse200ApplicationJsonItem;
import jsr305_server_spring.resources.support.ResponseWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
}
