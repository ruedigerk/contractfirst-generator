package io.github.ruedigerk.contractfirst.generator

import io.github.ruedigerk.contractfirst.generator.configuration.Configuration
import io.github.ruedigerk.contractfirst.generator.configuration.GeneratorType
import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import io.github.ruedigerk.contractfirst.generator.java.generator.clientgenerator.ClientGenerator
import io.github.ruedigerk.contractfirst.generator.java.generator.modelgenerator.ModelGenerator
import io.github.ruedigerk.contractfirst.generator.java.generator.servergenerator.ServerGenerator
import io.github.ruedigerk.contractfirst.generator.java.model.JavaSpecification
import io.github.ruedigerk.contractfirst.generator.java.transform.JavaTransformer
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.parser.ContractParser

/**
 * A recipe for generating REST-operation and model code from an OpenAPI specification file.
 */
class RecipeForFullSpecification(
    private val log: Log,
    private val configuration: Configuration,
) : () -> Unit {

  override operator fun invoke() {
    val specification = ContractParser(log).toSpecification(configuration.inputContractFile)
    val javaConfiguration = JavaConfiguration.forFullSpecification(configuration, apiPackagePrefix(configuration))
    val javaSpecification = JavaTransformer(log, javaConfiguration).transform(specification)

    val generator = getGenerator(javaConfiguration)
    generator(javaSpecification)
    ModelGenerator(javaConfiguration).generateCode(javaSpecification.modelFiles)

    if (configuration.outputContract) {
      RecipeForAllInOneContract(log, configuration).invoke()
    }
  }

  private fun getGenerator(javaConfiguration: JavaConfiguration): (JavaSpecification) -> Unit = when (configuration.generator) {
    GeneratorType.CLIENT -> ClientGenerator(javaConfiguration)
    GeneratorType.SERVER -> ServerGenerator(javaConfiguration)
    GeneratorType.MODEL_ONLY -> error("Illegal generator type: ${configuration.generator}")
  }

  private fun apiPackagePrefix(configuration: Configuration): String = when (configuration.generator) {
    GeneratorType.CLIENT -> ".api"
    GeneratorType.SERVER -> ".resources"
    GeneratorType.MODEL_ONLY -> error("Illegal generator type: ${configuration.generator}")
  }
}
