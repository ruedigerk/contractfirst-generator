package org.contractfirst.generator

import io.swagger.v3.oas.models.OpenAPI
import org.contractfirst.generator.java.generator.ClientGenerator
import org.contractfirst.generator.java.generator.ModelGenerator
import org.contractfirst.generator.java.generator.ServerStubGenerator
import org.contractfirst.generator.java.transform.JavaTransformer
import org.contractfirst.generator.logging.Log
import org.contractfirst.generator.logging.LogAdapter
import org.contractfirst.generator.parser.Parser
import org.contractfirst.generator.parser.ParserException
import org.contractfirst.generator.serializer.YamlSerializer
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
   */
  @Throws(NotSupportedException::class, ParserException::class)
  fun generate(configuration: Configuration) {
    log.debug { "Configuration:\n${configuration.prettyPrint()}" }

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

  private fun writeParsedContract(configuration: Configuration, openApi: OpenAPI) {
    val outputDir = File(configuration.outputDir)
    val contractOutputFile = File(configuration.outputContractFile)
    val outputFile = if (contractOutputFile.isAbsolute) contractOutputFile else outputDir.resolve(contractOutputFile)

    log.info { "Writing all-in-one contract to: $outputFile" }

    outputFile.parentFile.mkdirs()
    outputFile.writeText(YamlSerializer.toYaml(openApi))
  }
}