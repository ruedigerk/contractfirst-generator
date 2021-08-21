package de.rk42.openapi.codegen

import de.rk42.openapi.codegen.crosscutting.Log.Companion.getLogger
import de.rk42.openapi.codegen.crosscutting.LogbackConfigurator
import de.rk42.openapi.codegen.java.generator.model.ModelGenerator
import de.rk42.openapi.codegen.java.generator.server.ServerStubGenerator
import de.rk42.openapi.codegen.java.transform.JavaTransformer
import de.rk42.openapi.codegen.parser.Parser
import de.rk42.openapi.codegen.serializer.YamlSerializer
import io.swagger.v3.oas.models.OpenAPI
import java.io.File

/**
 * Implements the OpenApiCodegen functionality.
 *
 * TODO:
 *  - Add JSON-Pointers to exceptions/error messages to pinpoint the error origin.
 *  - Print out the YAML in case of error, to help showing to where the JSON-Pointers point, as swagger-parser inlines external references.
 *  - Check why uspto contract generates invalid code.
 *  - Support allOf, oneOf (anyOf?).
 *  - Enums of a different primitive type than string.
 *  - Store the "real" value of an enum constant in a "value" field with getter.
 */
object OpenApiCodegen {

  private val log = getLogger()
  
  fun generate(configuration: Configuration) {
    applyLoggingVerbosity(configuration.verbosity)

    log.debug { "Configuration:\n${configuration.prettyPrint()}" }

    val ctrSpecification = Parser().parse(configuration.contractFile)

    if (configuration.outputContract) {
      writeParsedContract(configuration, ctrSpecification.source)
    }

    val javaSpecification = JavaTransformer(configuration).transform(ctrSpecification)

    ServerStubGenerator(configuration).generateCode(javaSpecification)
    ModelGenerator(configuration).generateCode(javaSpecification)
  }

  fun applyLoggingVerbosity(verbosity: Configuration.Verbosity) {
    LogbackConfigurator.applyLoggingVerbosity(verbosity)
  }

  private fun writeParsedContract(configuration: Configuration, openApi: OpenAPI) {
    val contractOutputFile = File(configuration.outputDir).resolve(configuration.contractOutputFile)

    contractOutputFile.parentFile.mkdirs()
    contractOutputFile.writeText(YamlSerializer.toYaml(openApi))
  }
}