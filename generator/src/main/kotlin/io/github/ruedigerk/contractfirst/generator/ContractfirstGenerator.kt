package io.github.ruedigerk.contractfirst.generator

import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaTypeIdentifier
import io.github.ruedigerk.contractfirst.generator.java.generator.ClientGenerator
import io.github.ruedigerk.contractfirst.generator.java.generator.ModelGenerator
import io.github.ruedigerk.contractfirst.generator.java.generator.ServerStubGenerator
import io.github.ruedigerk.contractfirst.generator.java.transform.JavaTransformer
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.logging.LogAdapter
import io.github.ruedigerk.contractfirst.generator.parser.Parser
import io.github.ruedigerk.contractfirst.generator.parser.ParserException
import io.github.ruedigerk.contractfirst.generator.serializer.SerializerException
import io.github.ruedigerk.contractfirst.generator.serializer.YamlSerializer
import io.swagger.v3.oas.models.OpenAPI
import java.io.File

/**
 * The Contractfirst-Generator code generator.
 */
class ContractfirstGenerator(logAdapter: LogAdapter) {

  private val log = Log(logAdapter)

  /**
   * Generate the source code according to the supplied configuration.
   * @throws NotSupportedException when an OpenAPI feature is used in the input contract that is not supported by the generator.
   * @throws ParserException when the input contract is invalid and cannot be parsed.
   * @throws InvalidConfigurationException some option in the supplied configuration is invalid.
   * @throws SerializerException when the option to write an output contract is on and there is an error serializing it.
   */
  @Throws(NotSupportedException::class, ParserException::class, InvalidConfigurationException::class, SerializerException::class)
  fun generate(configuration: Configuration) {
    log.debug { "Configuration:\n${configuration.prettyPrint()}" }

    validateConfiguration(configuration)

    val specification = Parser(log).parse(configuration.inputContractFile)

    if (configuration.outputContract) {
      writeParsedContract(configuration, specification.source)
    }

    val javaSpecification = JavaTransformer(log, configuration).transform(specification)

    when (configuration.generator) {
      GeneratorType.CLIENT -> ClientGenerator(configuration).generateCode(javaSpecification)
      GeneratorType.SERVER -> ServerStubGenerator(configuration).generateCode(javaSpecification)
    }

    ModelGenerator(configuration).generateCode(javaSpecification)
  }

  // TODO: also validate the other configuration parameters
  @Throws(InvalidConfigurationException::class)
  private fun validateConfiguration(configuration: Configuration) {
    if (configuration.outputJavaModelNamePrefix.isNotEmpty() && configuration.outputJavaModelNamePrefix != configuration.outputJavaModelNamePrefix.toJavaTypeIdentifier()) {
      throw InvalidConfigurationException(
          "parameter outputJavaModelNamePrefix: \"${configuration.outputJavaModelNamePrefix}\" is not a valid prefix for a Java class name, " +
              "e.g. it must start with an upper case letter and must not contain spaces or invalid characters."
      )
    }
  }

  private fun writeParsedContract(configuration: Configuration, openApi: OpenAPI) {
    val outputDir = File(configuration.outputDir)
    val contractOutputFile = File(configuration.outputContractFile)
    val outputFile = if (contractOutputFile.isAbsolute) contractOutputFile else outputDir.resolve(contractOutputFile)

    log.info { "Writing all-in-one contract to: $outputFile" }

    outputFile.parentFile.mkdirs()
    outputFile.writeText(YamlSerializer.toYaml(openApi))
  }
}