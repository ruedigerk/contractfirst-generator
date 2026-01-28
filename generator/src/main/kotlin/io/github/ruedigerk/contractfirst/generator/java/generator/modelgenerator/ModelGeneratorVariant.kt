package io.github.ruedigerk.contractfirst.generator.java.generator.modelgenerator

import com.squareup.javapoet.AnnotationSpec

/**
 * Implementations of this interface define the variants of the Java model code generator.
 */
interface ModelGeneratorVariant {

  /**
   * Creates an annotation spec that allows to define the serialized name of a field or enum constant.
   */
  fun serializedNameAnnotation(originalName: String): AnnotationSpec
}
