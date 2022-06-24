package server_jsr305.resources;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import server_jsr305.model.Failure;
import server_jsr305.model.GetInlineObjectInArray200;
import server_jsr305.resources.support.ResponseWrapper;

@Path("")
public interface TestcasesApi {
  /**
   * A test case for the SchemaToJavaTypeTransformer.
   */
  @GET
  @Path("/testcases")
  @Produces("application/json")
  GetInlineObjectInArrayResponse getInlineObjectInArray();

  class GetInlineObjectInArrayResponse extends ResponseWrapper {
    private GetInlineObjectInArrayResponse(Response delegate) {
      super(delegate);
    }

    public static GetInlineObjectInArrayResponse with200ApplicationJson(
        List<GetInlineObjectInArray200> entity) {
      return new GetInlineObjectInArrayResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetInlineObjectInArrayResponse withApplicationJson(int status, Failure entity) {
      return new GetInlineObjectInArrayResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetInlineObjectInArrayResponse withCustomResponse(Response response) {
      return new GetInlineObjectInArrayResponse(response);
    }
  }
}
