package uspto.resources;

import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import uspto.model.PerformSearchRequestBody;
import uspto.resources.support.ResponseWrapper;

@Path("")
public interface SearchApi {
  /**
   * This API is based on Solr/Lucense Search. The data is indexed using SOLR. This GET API returns the list of all the searchable field names that are in the Solr Index. Please see the 'fields' attribute which returns an array of field names. Each field or a combination of fields can be searched using the Solr/Lucene Syntax. Please refer https://lucene.apache.org/core/3_6_2/queryparsersyntax.html#Overview for the query syntax. List of field names that are searchable can be determined using above GET api.
   *
   * @param version Version of the dataset.
   * @param dataset Name of the dataset. In this case, the default value is oa_citations
   */
  @POST
  @Path("/{dataset}/{version}/records")
  @Consumes("application/x-www-form-urlencoded")
  @Produces("application/json")
  PerformSearchResponse performSearch(@PathParam("version") @NotNull String version,
      @PathParam("dataset") @NotNull String dataset, @Valid PerformSearchRequestBody requestBody);

  class PerformSearchResponse extends ResponseWrapper {
    private PerformSearchResponse(Response delegate) {
      super(delegate);
    }

    public static PerformSearchResponse with200ApplicationJson(List<Map<String, Object>> entity) {
      return new PerformSearchResponse(Response.status(200).header("Content-Type", "application/json").entity(entity).build());
    }

    public static PerformSearchResponse with404() {
      return new PerformSearchResponse(Response.status(404).build());
    }

    public static PerformSearchResponse withCustomResponse(Response response) {
      return new PerformSearchResponse(response);
    }
  }
}
