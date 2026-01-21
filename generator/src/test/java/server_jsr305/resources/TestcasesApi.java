package server_jsr305.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.util.List;
import server_jsr305.model.Failure;
import server_jsr305.model.GetInlineObjectInArrayResponse200ApplicationJsonItem;
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
        List<GetInlineObjectInArrayResponse200ApplicationJsonItem> entity) {
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
