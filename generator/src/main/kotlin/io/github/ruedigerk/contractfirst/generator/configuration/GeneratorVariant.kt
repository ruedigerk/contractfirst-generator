package io.github.ruedigerk.contractfirst.generator.configuration

/**
 * The variant of a generator. Each generator has its own set of supported variants.
 */
enum class GeneratorVariant(private val associatedGeneratorSupplier: () -> GeneratorType) {

  CLIENT_OKHTTP({ GeneratorType.CLIENT }),
  SERVER_JAX_RS({ GeneratorType.SERVER }),
  SERVER_SPRING_WEB({ GeneratorType.SERVER }),
  MODEL_ONLY({ GeneratorType.MODEL_ONLY }),
  ;

  val associatedGenerator: GeneratorType
    get() = associatedGeneratorSupplier()
}
