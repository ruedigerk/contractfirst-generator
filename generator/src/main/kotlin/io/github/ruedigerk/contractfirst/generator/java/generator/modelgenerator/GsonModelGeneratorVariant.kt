package io.github.ruedigerk.contractfirst.generator.java.generator.modelgenerator

import io.github.ruedigerk.contractfirst.generator.java.generator.Annotations.toAnnotation

/**
 * Gson-specific implementation of the ModelGeneratorVariant.
 */
class GsonModelGeneratorVariant : ModelGeneratorVariant {

  override fun serializedNameAnnotation(originalName: String) = toAnnotation("com.google.gson.annotations.SerializedName", originalName)
}
