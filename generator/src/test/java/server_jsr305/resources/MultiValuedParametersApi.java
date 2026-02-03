package server_jsr305.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import server_jsr305.model.Failure;
import server_jsr305.model.SimpleEnum;
import server_jsr305.resources.support.ResponseWrapper;

@Path("")
public interface MultiValuedParametersApi {
  /**
   * Test for handling of multi-valued parameters.
   *
   * @param testSelector Selects the assertions to perform on the server.
   * @param pathParam Multi-valued path parameter.
   * @param queryParam Multi-valued query parameter.
   * @param headerParam Multi-valued header parameter.
   * @param pathSetParam Multi-valued path parameter.
   * @param querySetParam Multi-valued query parameter.
   * @param headerSetParam Multi-valued header parameter.
   */
  @POST
  @Path("/multiValuedParametersTest/{pathParam}/{pathSetParam}")
  @Produces("application/json")
  MultiValuedParametersTestResponse multiValuedParametersTest(
      @QueryParam("testSelector") @NotNull String testSelector,
      @PathParam("pathParam") @NotNull List<String> pathParam,
      @QueryParam("queryParam") List<SimpleEnum> queryParam,
      @HeaderParam("headerParam") List<Integer> headerParam,
      @PathParam("pathSetParam") @NotNull Set<String> pathSetParam,
      @QueryParam("querySetParam") Set<SimpleEnum> querySetParam,
      @HeaderParam("headerSetParam") Set<Integer> headerSetParam);

  class MultiValuedParametersTestResponse extends ResponseWrapper {
    private MultiValuedParametersTestResponse(Response delegate) {
      super(delegate);
    }

    public static MultiValuedParametersTestResponse with204() {
      return new MultiValuedParametersTestResponse(Response.status(204).build());
    }

    public static MultiValuedParametersTestResponse withApplicationJson(int status,
        Failure entity) {
      return new MultiValuedParametersTestResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static MultiValuedParametersTestResponse withCustomResponse(Response response) {
      return new MultiValuedParametersTestResponse(response);
    }
  }
}
