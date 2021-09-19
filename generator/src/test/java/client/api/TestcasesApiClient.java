package client.api;

import client.model.Failure;
import client.model.GetInlineObjectInArray200;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import org.contractfirst.generator.client.ApiClientIoException;
import org.contractfirst.generator.client.ApiClientSupport;
import org.contractfirst.generator.client.ApiClientUndefinedResponseException;
import org.contractfirst.generator.client.ApiClientValidationException;
import org.contractfirst.generator.client.DefinedResponse;
import org.contractfirst.generator.client.GenericResponse;
import org.contractfirst.generator.client.internal.Operation;
import org.contractfirst.generator.client.internal.StatusCode;

public class TestcasesApiClient {
  private final ApiClientSupport support;

  public TestcasesApiClient(ApiClientSupport support) {
    this.support = support;
  }

  /**
   * A test case for the SchemaToJavaTypeTransformer.
   */
  public List<GetInlineObjectInArray200> getInlineObjectInArray() throws ApiClientIoException,
      ApiClientValidationException, ApiClientUndefinedResponseException {

    GenericResponse genericResponse = getInlineObjectInArrayWithResponse();
    DefinedResponse response = genericResponse.asDefinedResponse();

    if (!response.isSuccessful()) {
      throw new RestClientFailureEntityException(response.getStatusCode(), (Failure) response.getEntity());
    }

    return (List<GetInlineObjectInArray200>) response.getEntity();
  }

  /**
   * A test case for the SchemaToJavaTypeTransformer.
   */
  public GenericResponse getInlineObjectInArrayWithResponse() throws ApiClientIoException,
      ApiClientValidationException {

    Operation.Builder builder = new Operation.Builder("/testcases", "GET");

    builder.response(StatusCode.of(200), "application/json", new TypeToken<List<GetInlineObjectInArray200>>(){}.getType());
    builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

    return support.executeRequest(builder.build());
  }
}
