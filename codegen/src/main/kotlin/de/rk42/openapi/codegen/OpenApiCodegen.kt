package de.rk42.openapi.codegen

import de.rk42.openapi.codegen.java.generator.model.ModelGenerator
import de.rk42.openapi.codegen.java.generator.server.ServerStubGenerator
import de.rk42.openapi.codegen.java.transform.JavaTransformer
import de.rk42.openapi.codegen.logging.Log
import de.rk42.openapi.codegen.logging.LogAdapter
import de.rk42.openapi.codegen.parser.Parser
import de.rk42.openapi.codegen.parser.ParserException
import de.rk42.openapi.codegen.serializer.YamlSerializer
import io.swagger.v3.oas.models.OpenAPI
import java.io.File
import kotlin.jvm.Throws

/**
 * The OpenAPI Code Generator.
 *
 * TODO:
 *  - Add JSON-Pointers to exceptions/error messages to pinpoint the error origin.
 *  - Print out the YAML in case of error, to help showing to where the JSON-Pointers point, as swagger-parser inlines external references.
 *  - Support allOf, oneOf (anyOf?).
 *  - Enums of a different primitive type than string.
 *  - Store the "real" value of an enum constant in a "value" field with getter.
 */
class OpenApiCodegen(logAdapter: LogAdapter) {

  private val log = Log(logAdapter)

  /**
   * Generate the source code according to the supplied configuration.
   * @throws NotSupportedException when an OpenAPI feature is used in the input contract that is not supported by the generator.
   * @throws ParserException when the input contract is invalid and cannot be parsed.
   */
  @Throws(NotSupportedException::class, ParserException::class)
  fun generate(configuration: Configuration) {
    log.debug { "Configuration:\n${configuration.prettyPrint()}" }

    val ctrSpecification = Parser(log).parse(configuration.contractFile)

    if (configuration.outputContract) {
      writeParsedContract(configuration, ctrSpecification.source)
    }

    val javaSpecification = JavaTransformer(log, configuration).transform(ctrSpecification)

    ServerStubGenerator(configuration).generateCode(javaSpecification)
    ModelGenerator(configuration).generateCode(javaSpecification)
  }

  private fun writeParsedContract(configuration: Configuration, openApi: OpenAPI) {
    val outputDir = File(configuration.outputDir)
    val contractOutputFile = File(configuration.contractOutputFile)
    val outputFile = if (contractOutputFile.isAbsolute) contractOutputFile else outputDir.resolve(contractOutputFile)

    log.info { "Writing all-in-one contract to: $outputFile" }

    outputFile.parentFile.mkdirs()
    outputFile.writeText(YamlSerializer.toYaml(openApi))
  }
}