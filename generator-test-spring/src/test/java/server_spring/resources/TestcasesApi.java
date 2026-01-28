package server_spring.resources;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import server_spring.model.RestFailure;
import server_spring.model.RestGetInlineObjectInArrayResponse200ApplicationJsonItem;
import server_spring.resources.support.ResponseWrapper;

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
        List<RestGetInlineObjectInArrayResponse200ApplicationJsonItem> entity) {
      return new GetInlineObjectInArrayResponse(ResponseEntity.status(200).header("Content-Type", "application/json").body(entity));
    }

    public static GetInlineObjectInArrayResponse withApplicationJson(int status,
        RestFailure entity) {
      return new GetInlineObjectInArrayResponse(ResponseEntity.status(status).header("Content-Type", "application/json").body(entity));
    }

    public static GetInlineObjectInArrayResponse withCustomResponse(ResponseEntity response) {
      return new GetInlineObjectInArrayResponse(response);
    }
  }
}
