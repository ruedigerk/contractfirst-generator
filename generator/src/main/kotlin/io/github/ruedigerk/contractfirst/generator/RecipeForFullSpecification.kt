/*
 * Copyright (C) 2022 Sopra Financial Technology GmbH
 * Frankenstraße 146, 90461 Nürnberg, Germany
 *
 * This software is the confidential and proprietary information of
 * Sopra Financial Technology GmbH ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * Sopra Financial Technology GmbH.
 */

package io.github.ruedigerk.contractfirst.generator

import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import io.github.ruedigerk.contractfirst.generator.java.generator.ModelGenerator
import io.github.ruedigerk.contractfirst.generator.java.model.JavaSpecification
import io.github.ruedigerk.contractfirst.generator.java.transform.JavaTransformer
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.parser.Parser
import io.github.ruedigerk.contractfirst.generator.parser.SwaggerParser
import io.github.ruedigerk.contractfirst.generator.serializer.YamlSerializer
import io.swagger.v3.oas.models.OpenAPI
import java.io.File

/**
 * A recipe for generating REST-operation and model code from an OpenAPI specification file.
 */
class RecipeForFullSpecification(
    private val log: Log,
    private val configuration: Configuration,
    private val javaGenerator: (JavaConfiguration) -> (JavaSpecification) -> Unit
) : () -> Unit {

  override operator fun invoke() {
    val swaggerSpecification = SwaggerParser().parseFile(configuration.inputContractFile)
    val specification = Parser(log).toSpecification(swaggerSpecification)

    if (configuration.outputContract) {
      writeParsedContract(configuration, specification.source)
    }

    val javaConfiguration = JavaConfiguration.forFullSpecification(configuration, apiPackagePrefix(configuration))
    val javaSpecification = JavaTransformer(log, javaConfiguration).transform(specification)
    javaGenerator(javaConfiguration)(javaSpecification)
    ModelGenerator(javaConfiguration).generateCode(javaSpecification)
  }

  private fun apiPackagePrefix(configuration: Configuration): String = when (configuration.generator) {
    GeneratorType.CLIENT -> ".api"
    GeneratorType.SERVER -> ".resources"
    GeneratorType.MODEL_ONLY -> error("Illegal generator type: ${configuration.generator}")
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