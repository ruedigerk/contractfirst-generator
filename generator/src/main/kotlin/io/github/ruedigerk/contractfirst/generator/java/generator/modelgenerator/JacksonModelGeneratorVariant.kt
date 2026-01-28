package io.github.ruedigerk.contractfirst.generator.java.generator.modelgenerator

import io.github.ruedigerk.contractfirst.generator.java.generator.Annotations.toAnnotation

/**
 * Jackson-specific implementation of the ModelGeneratorVariant.
 */
class JacksonModelGeneratorVariant : ModelGeneratorVariant {

  override fun serializedNameAnnotation(originalName: String) = toAnnotation("com.fasterxml.jackson.annotation.JsonProperty", originalName)
}
