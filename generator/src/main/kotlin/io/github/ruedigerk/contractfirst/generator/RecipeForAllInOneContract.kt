package io.github.ruedigerk.contractfirst.generator

import io.github.ruedigerk.contractfirst.generator.allinonecontract.SwaggerParser
import io.github.ruedigerk.contractfirst.generator.allinonecontract.YamlSerializer
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.swagger.v3.oas.models.OpenAPI
import java.io.File

/**
 * A recipe for generating REST-operation and model code from an OpenAPI specification file.
 */
class RecipeForAllInOneContract(
    private val log: Log,
    private val configuration: Configuration,
) : () -> Unit {

  override operator fun invoke() {
    val openApi = SwaggerParser().parseFile(configuration.inputContractFile)
    writeParsedContract(configuration, openApi)
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