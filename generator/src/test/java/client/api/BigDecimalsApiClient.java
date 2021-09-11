package client.api;

import java.math.BigDecimal;
import org.contractfirst.generator.client.ApiClientIoException;
import org.contractfirst.generator.client.ApiClientSupport;
import org.contractfirst.generator.client.ApiClientUndefinedResponseException;
import org.contractfirst.generator.client.ApiClientValidationException;
import org.contractfirst.generator.client.DefinedResponse;
import org.contractfirst.generator.client.GenericResponse;
import org.contractfirst.generator.client.internal.Operation;
import org.contractfirst.generator.client.internal.ParameterLocation;
import org.contractfirst.generator.client.internal.StatusCode;

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

    return support.executeRequest(builder.build());
  }
}
