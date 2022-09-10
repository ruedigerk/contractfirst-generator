package io.github.ruedigerk.contractfirst.generator

import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import io.github.ruedigerk.contractfirst.generator.java.generator.ModelGenerator
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
    private val javaGenerator: (JavaConfiguration) -> (JavaSpecification) -> Unit
) : () -> Unit {

  override operator fun invoke() {
    val specification = ContractParser(log).toSpecification(configuration.inputContractFile)
    val javaConfiguration = JavaConfiguration.forFullSpecification(configuration, apiPackagePrefix(configuration))
    val javaSpecification = JavaTransformer(log, javaConfiguration).transform(specification)
    
    javaGenerator(javaConfiguration)(javaSpecification)
    ModelGenerator(javaConfiguration).generateCode(javaSpecification.modelFiles)

    if (configuration.outputContract) {
      RecipeForAllInOneContract(log, configuration).invoke()
    }
  }

  private fun apiPackagePrefix(configuration: Configuration): String = when (configuration.generator) {
    GeneratorType.CLIENT -> ".api"
    GeneratorType.SERVER -> ".resources"
    GeneratorType.MODEL_ONLY -> error("Illegal generator type: ${configuration.generator}")
  }
}