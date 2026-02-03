package server.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.util.List;
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

  /**
   * Testing that enums of a type different from string are supported by ignoring the enum part of the type.
   */
  @POST
  @Path("/nonStringEnumTypeIsIgnored")
  @Produces
  NonStringEnumTypeIsIgnoredResponse nonStringEnumTypeIsIgnored(
      @QueryParam("booleanEnum") Boolean booleanEnum);

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

  class NonStringEnumTypeIsIgnoredResponse extends ResponseWrapper {
    private NonStringEnumTypeIsIgnoredResponse(Response delegate) {
      super(delegate);
    }

    public static NonStringEnumTypeIsIgnoredResponse with204() {
      return new NonStringEnumTypeIsIgnoredResponse(Response.status(204).build());
    }

    public static NonStringEnumTypeIsIgnoredResponse withCustomResponse(Response response) {
      return new NonStringEnumTypeIsIgnoredResponse(response);
    }
  }
}
