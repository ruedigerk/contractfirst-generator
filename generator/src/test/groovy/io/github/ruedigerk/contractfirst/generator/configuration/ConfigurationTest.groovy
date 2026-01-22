package io.github.ruedigerk.contractfirst.generator.configuration

import spock.lang.Specification

class ConfigurationTest extends Specification {

  def "Valid configuration is accepted"() {
    given:
    Configuration configuration = new Configuration(
        "test.yaml",
        generatorType,
        generatorVariant,
        modelVariant,
        "output",
        true,
        "openapi.yaml",
        "package",
        true,
        "/dir-prefix/",
        "Prefix",
        false
    )

    when:
    configuration.validate()

    then:
    noExceptionThrown()

    where:
    generatorType            | generatorVariant                   | modelVariant
    GeneratorType.CLIENT     | GeneratorVariant.CLIENT_OKHTTP     | ModelVariant.GSON
    GeneratorType.MODEL_ONLY | GeneratorVariant.MODEL_ONLY        | ModelVariant.GSON
    GeneratorType.SERVER     | GeneratorVariant.SERVER_JAX_RS     | ModelVariant.GSON
    GeneratorType.SERVER     | GeneratorVariant.SERVER_SPRING_WEB | ModelVariant.GSON
  }

  def "Invalid configuration is rejected"() {
    given:
    Configuration configuration = new Configuration(
        "test.yaml",
        GeneratorType.SERVER,
        GeneratorVariant.SERVER_JAX_RS,
        ModelVariant.GSON,
        "output",
        true,
        "openapi.yaml",
        "package",
        true,
        "/prefix/",
        "lowercase",
        false
    )

    when:
    configuration.validate()

    then:
    def e = thrown InvalidConfigurationException
    e.message.contains("outputJavaModelNamePrefix")
  }
}
