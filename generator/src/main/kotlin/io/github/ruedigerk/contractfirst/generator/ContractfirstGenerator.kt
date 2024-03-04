package io.github.ruedigerk.contractfirst.generator

import io.github.ruedigerk.contractfirst.generator.allinonecontract.SerializerException
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaTypeIdentifier
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.logging.LogAdapter
import java.io.IOException

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

    val recipe = when (configuration.generator) {
      GeneratorType.CLIENT, GeneratorType.SERVER -> RecipeForFullSpecification(log, configuration)
      GeneratorType.MODEL_ONLY -> RecipeForModelOnly(log, configuration)
    }

    try {
      recipe.invoke()
    } catch (e: IOException) {
      throw ParserIoException("IO error during parsing ${e.message}", e)
    }
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
}