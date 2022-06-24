package io.github.ruedigerk.contractfirst.generator

import spock.lang.Specification

class ContractfirstGeneratorTest extends Specification {

  def "configuration is validated"() {
    given:
    Configuration configuration = new Configuration(
        "test.yaml",
        GeneratorType.SERVER,
        "output",
        true,
        "openapi.yaml",
        "package",
        "lowercase",
        false
    )

    when:
    new ContractfirstGenerator(new NoLoggingLogAdapter()).generate(configuration)

    then:
    def e = thrown InvalidConfigurationException
    e.message.contains("outputJavaModelNamePrefix")
  }
}
