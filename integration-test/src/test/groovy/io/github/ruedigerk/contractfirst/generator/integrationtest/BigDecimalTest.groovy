package io.github.ruedigerk.contractfirst.generator.integrationtest

import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.client.api.BigDecimalsApiClient
import io.github.ruedigerk.contractfirst.generator.integrationtest.generated.server.resources.BigDecimalsApi
import io.github.ruedigerk.contractfirst.generator.integrationtest.spec.EmbeddedJaxRsServerSpecification
import spock.lang.Subject
import spock.lang.Unroll

/**
 * Tests serialization of schema type number to BigDecimal.
 */
class BigDecimalTest extends EmbeddedJaxRsServerSpecification {

  @Subject
  BigDecimalsApiClient apiClient = new BigDecimalsApiClient(apiClientSupport)

  @Override
  Class<?> getTestResource() {
    EmbeddedServerResource
  }

  @Unroll
  @SuppressWarnings('ChangeToOperator')
  def "Test BigDecimal as parameter: #input"() {
    when:
    BigDecimal response = apiClient.getNumber(number)

    then:
    response.equals(number)

    where:
    input << ["0", "0.00", "1e10", "1e-10", "123.45", "12345678901234567890.1234567890"]
    number = new BigDecimal(input)
  }

  /**
   * JAX-RS resource implementation used in this test.
   */
  static class EmbeddedServerResource implements BigDecimalsApi {

    @Override
    GetNumberResponse getNumber(BigDecimal decimalNumber) {
      return GetNumberResponse.with200ApplicationJson(decimalNumber)
    }
  }
}
