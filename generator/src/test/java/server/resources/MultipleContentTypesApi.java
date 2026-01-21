package server.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import server.model.Failure;
import server.model.Manual;
import server.resources.support.ResponseWrapper;

@Path("")
public interface MultipleContentTypesApi {
  /**
   * Test case for multiple response content types with different schemas.
   *
   * @param testCaseSelector Used to select the desired behaviour of the server in the test.
   */
  @GET
  @Path("/manuals")
  @Produces({
      "application/json",
      "application/pdf",
      "text/plain"
  })
  GetManualResponse getManual(@HeaderParam("testCaseSelector") String testCaseSelector);

  class GetManualResponse extends ResponseWrapper {
    private GetManualResponse(Response delegate) {
      super(delegate);
    }

    public static GetManualResponse with200ApplicationJson(Manual entity) {
      return new GetManualResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetManualResponse with200ApplicationPdf(InputStream entity) {
      return new GetManualResponse(Response.status(200).header("Content-Type", "application/pdf").entity(entity).build());
    }

    public static GetManualResponse with202TextPlain(String entity) {
      return new GetManualResponse(Response.status(202).header("Content-Type", "text/plain").entity(entity).build());
    }

    public static GetManualResponse with204() {
      return new GetManualResponse(Response.status(204).build());
    }

    public static GetManualResponse withApplicationJson(int status, Failure entity) {
      return new GetManualResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetManualResponse withCustomResponse(Response response) {
      return new GetManualResponse(response);
    }
  }
}
