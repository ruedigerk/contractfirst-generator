package io.github.ruedigerk.contractfirst.generator

import io.github.ruedigerk.contractfirst.generator.allinonecontract.SerializerException
import io.github.ruedigerk.contractfirst.generator.configuration.Configuration
import io.github.ruedigerk.contractfirst.generator.configuration.GeneratorType
import io.github.ruedigerk.contractfirst.generator.configuration.InvalidConfigurationException
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.logging.LogAdapter
import java.io.IOException

/**
 * The "entry point" for the Contractfirst-Generator code generator.
 */
class ContractfirstGenerator(logAdapter: LogAdapter) {

  private val log = Log(logAdapter)

  /**
   * Generate the source code according to the supplied configuration.
   *
   * @throws NotSupportedException when an OpenAPI feature is used in the input contract that is not supported by the generator.
   * @throws ParserException when the input contract is invalid and cannot be parsed.
   * @throws io.github.ruedigerk.contractfirst.generator.configuration.InvalidConfigurationException when some option in the supplied configuration is invalid.
   * @throws SerializerException when the option to write an output contract is on and there is an error serializing it.
   */
  @Throws(NotSupportedException::class, ParserException::class, InvalidConfigurationException::class, SerializerException::class)
  fun generate(configuration: Configuration) {
    log.debug { "Configuration:\n${configuration.prettyPrint()}" }

    configuration.validate()

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
}
