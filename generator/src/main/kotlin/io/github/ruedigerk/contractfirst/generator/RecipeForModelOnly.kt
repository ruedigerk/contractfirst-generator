package io.github.ruedigerk.contractfirst.generator

import io.github.ruedigerk.contractfirst.generator.configuration.Configuration
import io.github.ruedigerk.contractfirst.generator.configuration.InvalidConfigurationException
import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import io.github.ruedigerk.contractfirst.generator.java.generator.modelgenerator.ModelGenerator
import io.github.ruedigerk.contractfirst.generator.java.transform.JavaSchemaToSourceTransformer
import io.github.ruedigerk.contractfirst.generator.java.transform.JavaSchemaToTypeTransformer
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.parser.ResolvingSchemaParser
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
    val parsedSchemas = ResolvingSchemaParser.parseAndResolveAll(log, modelFiles)
    
    val javaConfiguration = JavaConfiguration.forModelOnly(configuration)
    val types = JavaSchemaToTypeTransformer(log, parsedSchemas, javaConfiguration, emptyMap()).transform()
    val sourceFiles = JavaSchemaToSourceTransformer(parsedSchemas, types).transform()

    ModelGenerator(javaConfiguration).generateCode(sourceFiles)
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
}
