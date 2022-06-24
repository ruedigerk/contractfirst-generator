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
import io.github.ruedigerk.contractfirst.generator.java.transform.JavaTransformer
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.model.Specification
import io.github.ruedigerk.contractfirst.generator.parser.SchemaResolver
import io.github.ruedigerk.contractfirst.generator.parser.SwaggerParser
import io.swagger.v3.oas.models.OpenAPI
import java.io.File

/**
 * A recipe for generating only model code from a set of JSON-Schema files. Does not generate any REST-operation code.
 */
class RecipeForModelOnly(
    private val log: Log,
    private val configuration: Configuration
) : () -> Unit {

  override operator fun invoke() {
    val modelFiles = findModelFiles(configuration)
    val specificationText = createSpecificationText(modelFiles)
    val swaggerSpecification = SwaggerParser().parseString(specificationText)
    val specification = toSpecification(swaggerSpecification)

    val javaConfiguration = JavaConfiguration.forModelOnly(configuration)
    val javaSpecification = JavaTransformer(log, javaConfiguration).transform(specification)

    ModelGenerator(javaConfiguration).generateCode(javaSpecification)
  }

  private fun findModelFiles(configuration: Configuration): List<File> {
    val modelDirectory = File(configuration.inputContractFile)

    if (!modelDirectory.exists()) {
      throw InvalidConfigurationException("parameter inputContractFile: \"$modelDirectory\" does not point to an existing file or directory.")
    }

    val modelFiles = modelDirectory.walk()
        .filter { it.name.endsWith(".yaml") || it.name.endsWith(".json") }
        .toList()

    if (modelFiles.isEmpty()) {
      throw InvalidConfigurationException("No YAML or JSON model files found for inputContractFile: \"$modelDirectory\".")
    }

    return modelFiles
  }

  private fun createSpecificationText(modelFiles: List<File>): String {
    val builder = StringBuilder(DUMMY_SPECIFICATION)
    var count = 0

    for (file in modelFiles) {
      count++
      builder.append("\n    $INPUT_MODEL_PREFIX$count:")
      builder.append("\n      \$ref: \"$file\"")
    }

    return builder.toString()
  }

  private fun toSpecification(openApi: OpenAPI): Specification {
    // schemas cannot be null here, as we made sure above, that we added at least one schema to the input.
    val schemaResolver = SchemaResolver(log, openApi.components?.schemas!!)

    // Swagger-Parser inlines all schema references for the input models, but additionally adds each inlined schema a second time with its file name as the
    // reference. Here we discard all the original input models and declare the remaining ones as used in the SchemaResolver.
    val schemas = schemaResolver.topLevelSchemas.filterKeys { !it.reference.startsWith("#/components/schemas/$INPUT_MODEL_PREFIX") }

    schemaResolver.overwriteUsedSchemas(schemas.values)
    val usedSchemas = schemaResolver.findAllUsedSchemasRecursively()

    return Specification(emptyList(), usedSchemas.allSchemas, usedSchemas.topLevelSchemas, openApi)
  }

  companion object {

    private const val INPUT_MODEL_PREFIX = "___Input__Model___"
    private val DUMMY_SPECIFICATION = """
      openapi: "3.0.0"
      info:
        version: 1.0.0
        title: Dummy contract for generating model files only.
      paths:
        /dummy:
          get:
            operationId: dummyOperation
            responses:
              200:
                description: Component created.
      components:
        schemas:
    """.trimIndent()
  }
}