package client.api;

import client.model.Failure;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientIoException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientSupport;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientUndefinedResponseException;
import io.github.ruedigerk.contractfirst.generator.client.ApiClientValidationException;
import io.github.ruedigerk.contractfirst.generator.client.DefinedResponse;
import io.github.ruedigerk.contractfirst.generator.client.GenericResponse;
import io.github.ruedigerk.contractfirst.generator.client.internal.Operation;
import io.github.ruedigerk.contractfirst.generator.client.internal.ParameterLocation;
import io.github.ruedigerk.contractfirst.generator.client.internal.StatusCode;
import java.math.BigDecimal;

public class BigDecimalsApiClient {
  private final ApiClientSupport support;

  public BigDecimalsApiClient(ApiClientSupport support) {
    this.support = support;
  }

  /**
   * Test serialization of schema type number as BigDecimal.
   *
   * @param decimalNumber Test BigDecimal
   */
  public BigDecimal getNumber(BigDecimal decimalNumber) throws ApiClientIoException,
      ApiClientValidationException, ApiClientUndefinedResponseException {

    GenericResponse genericResponse = getNumberWithResponse(decimalNumber);
    DefinedResponse response = genericResponse.asDefinedResponse();

    if (!response.isSuccessful()) {
      throw new RestClientFailureEntityException(response.getStatusCode(), (Failure) response.getEntity());
    }

    return (BigDecimal) response.getEntity();
  }

  /**
   * Test serialization of schema type number as BigDecimal.
   *
   * @param decimalNumber Test BigDecimal
   */
  public GenericResponse getNumberWithResponse(BigDecimal decimalNumber) throws
      ApiClientIoException, ApiClientValidationException {

    Operation.Builder builder = new Operation.Builder("/bigDecimals", "GET");

    builder.parameter("decimalNumber", ParameterLocation.QUERY, true, decimalNumber);

    builder.response(StatusCode.of(200), "application/json", BigDecimal.class);
    builder.response(StatusCode.DEFAULT, "application/json", Failure.class);

    return support.executeRequest(builder.build());
  }
}
