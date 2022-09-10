package server.resources;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import server.model.Failure;
import server.model.GetInlineObjectInArrayResponse200ApplicationJsonItem;
import server.resources.support.ResponseWrapper;

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
