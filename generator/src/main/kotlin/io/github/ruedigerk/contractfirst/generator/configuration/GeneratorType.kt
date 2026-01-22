package io.github.ruedigerk.contractfirst.generator.configuration

/**
 * List the types of generators supported.
 */
enum class GeneratorType(val defaultVariant: GeneratorVariant) {

  CLIENT(GeneratorVariant.CLIENT_OKHTTP),
  SERVER(GeneratorVariant.SERVER_JAX_RS),
  MODEL_ONLY(GeneratorVariant.MODEL_ONLY),
}
