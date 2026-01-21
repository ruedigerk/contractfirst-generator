package combinations_server.resources;

import combinations_server.model.Book;
import combinations_server.model.CtcError;
import combinations_server.model.SevereCtcError;
import combinations_server.resources.support.ResponseWrapper;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("")
public interface ContentTypeCombinationsApi {
  /**
   * Test case for only having a response with status code "default".
   */
  @GET
  @Path("/defaultOnly")
  @Produces("application/json")
  GetDefaultOnlyResponse getDefaultOnly(@HeaderParam("testCaseSelector") String testCaseSelector);

  /**
   * Test case for only having a single successful response.
   */
  @GET
  @Path("/successOnly")
  @Produces("application/json")
  GetSuccessOnlyResponse getSuccessOnly();

  /**
   * Test case for only having a single failure response.
   */
  @GET
  @Path("/failureOnly")
  @Produces("application/json")
  GetFailureOnlyResponse getFailureOnly();

  /**
   * Test case for having one successful response with an entity and a default for all errors.
   */
  @GET
  @Path("/successEntityAndErrorDefault")
  @Produces("application/json")
  GetSuccessEntityAndErrorDefaultResponse getSuccessEntityAndErrorDefault(
      @HeaderParam("testCaseSelector") String testCaseSelector);

  /**
   * Test case for having multiple success entity types.
   */
  @GET
  @Path("/multipleSuccessEntities")
  @Produces("application/json")
  GetMultipleSuccessEntitiesResponse getMultipleSuccessEntities(
      @HeaderParam("testCaseSelector") String testCaseSelector);

  /**
   * Test case for having multiple successful responses without content.
   */
  @GET
  @Path("/multipleSuccessResponsesWithoutContent")
  @Produces
  GetMultipleSuccessResponsesWithoutContentResponse getMultipleSuccessResponsesWithoutContent(
      @HeaderParam("testCaseSelector") String testCaseSelector);

  /**
   * Test case for having multiple error entity types.
   */
  @GET
  @Path("/multipleErrorEntities")
  @Produces("application/json")
  GetMultipleErrorEntitiesResponse getMultipleErrorEntities(
      @HeaderParam("testCaseSelector") String testCaseSelector);

  /**
   * Test case for returning content with status code 204.
   */
  @GET
  @Path("/contentFor204")
  @Produces
  GetContentFor204Response getContentFor204();

  class GetDefaultOnlyResponse extends ResponseWrapper {
    private GetDefaultOnlyResponse(Response delegate) {
      super(delegate);
    }

    public static GetDefaultOnlyResponse withApplicationJson(int status, Book entity) {
      return new GetDefaultOnlyResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetDefaultOnlyResponse withCustomResponse(Response response) {
      return new GetDefaultOnlyResponse(response);
    }
  }

  class GetSuccessOnlyResponse extends ResponseWrapper {
    private GetSuccessOnlyResponse(Response delegate) {
      super(delegate);
    }

    public static GetSuccessOnlyResponse with200ApplicationJson(Book entity) {
      return new GetSuccessOnlyResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetSuccessOnlyResponse withCustomResponse(Response response) {
      return new GetSuccessOnlyResponse(response);
    }
  }

  class GetFailureOnlyResponse extends ResponseWrapper {
    private GetFailureOnlyResponse(Response delegate) {
      super(delegate);
    }

    public static GetFailureOnlyResponse with400ApplicationJson(CtcError entity) {
      return new GetFailureOnlyResponse(Response.status(400).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetFailureOnlyResponse withCustomResponse(Response response) {
      return new GetFailureOnlyResponse(response);
    }
  }

  class GetSuccessEntityAndErrorDefaultResponse extends ResponseWrapper {
    private GetSuccessEntityAndErrorDefaultResponse(Response delegate) {
      super(delegate);
    }

    public static GetSuccessEntityAndErrorDefaultResponse with200ApplicationJson(Book entity) {
      return new GetSuccessEntityAndErrorDefaultResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetSuccessEntityAndErrorDefaultResponse withApplicationJson(int status,
        CtcError entity) {
      return new GetSuccessEntityAndErrorDefaultResponse(Response.status(status).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetSuccessEntityAndErrorDefaultResponse withCustomResponse(Response response) {
      return new GetSuccessEntityAndErrorDefaultResponse(response);
    }
  }

  class GetMultipleSuccessEntitiesResponse extends ResponseWrapper {
    private GetMultipleSuccessEntitiesResponse(Response delegate) {
      super(delegate);
    }

    public static GetMultipleSuccessEntitiesResponse with200ApplicationJson(Book entity) {
      return new GetMultipleSuccessEntitiesResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetMultipleSuccessEntitiesResponse with201ApplicationJson(CtcError entity) {
      return new GetMultipleSuccessEntitiesResponse(Response.status(201).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetMultipleSuccessEntitiesResponse withCustomResponse(Response response) {
      return new GetMultipleSuccessEntitiesResponse(response);
    }
  }

  class GetMultipleSuccessResponsesWithoutContentResponse extends ResponseWrapper {
    private GetMultipleSuccessResponsesWithoutContentResponse(Response delegate) {
      super(delegate);
    }

    public static GetMultipleSuccessResponsesWithoutContentResponse with200() {
      return new GetMultipleSuccessResponsesWithoutContentResponse(Response.status(200).build());
    }

    public static GetMultipleSuccessResponsesWithoutContentResponse with204() {
      return new GetMultipleSuccessResponsesWithoutContentResponse(Response.status(204).build());
    }

    public static GetMultipleSuccessResponsesWithoutContentResponse withCustomResponse(
        Response response) {
      return new GetMultipleSuccessResponsesWithoutContentResponse(response);
    }
  }

  class GetMultipleErrorEntitiesResponse extends ResponseWrapper {
    private GetMultipleErrorEntitiesResponse(Response delegate) {
      super(delegate);
    }

    public static GetMultipleErrorEntitiesResponse with200ApplicationJson(Book entity) {
      return new GetMultipleErrorEntitiesResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetMultipleErrorEntitiesResponse with400ApplicationJson(CtcError entity) {
      return new GetMultipleErrorEntitiesResponse(Response.status(400).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetMultipleErrorEntitiesResponse with500ApplicationJson(SevereCtcError entity) {
      return new GetMultipleErrorEntitiesResponse(Response.status(500).header("Content-Type", "application/json").entity(entity).build());
    }

    public static GetMultipleErrorEntitiesResponse withCustomResponse(Response response) {
      return new GetMultipleErrorEntitiesResponse(response);
    }
  }

  class GetContentFor204Response extends ResponseWrapper {
    private GetContentFor204Response(Response delegate) {
      super(delegate);
    }

    public static GetContentFor204Response with204() {
      return new GetContentFor204Response(Response.status(204).build());
    }

    public static GetContentFor204Response withCustomResponse(Response response) {
      return new GetContentFor204Response(response);
    }
  }
}
