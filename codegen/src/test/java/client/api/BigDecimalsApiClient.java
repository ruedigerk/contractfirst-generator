package client.api;

import de.rk42.openapi.codegen.client.ApiClientIoException;
import de.rk42.openapi.codegen.client.ApiClientSupport;
import de.rk42.openapi.codegen.client.ApiClientUndefinedResponseException;
import de.rk42.openapi.codegen.client.ApiClientValidationException;
import de.rk42.openapi.codegen.client.DefinedResponse;
import de.rk42.openapi.codegen.client.GenericResponse;
import de.rk42.openapi.codegen.client.internal.Operation;
import de.rk42.openapi.codegen.client.internal.ParameterLocation;
import de.rk42.openapi.codegen.client.internal.StatusCode;
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
