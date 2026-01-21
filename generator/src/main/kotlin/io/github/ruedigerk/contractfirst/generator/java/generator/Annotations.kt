package io.github.ruedigerk.contractfirst.generator.java.generator

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import io.github.ruedigerk.contractfirst.generator.java.model.*
import java.math.BigInteger


/**
 * For creating annotations and BeanValidation annotations.
 */
object Annotations {

  private const val BEAN_VALIDATION_PACKAGE = "jakarta.validation.constraints"

  private val VALID_ANNOTATION: AnnotationSpec = toAnnotation("jakarta.validation.Valid")
  private val JSR305_NULLABLE_ANNOTATION: AnnotationSpec = toAnnotation("javax.annotation.Nullable")
  private val JSR305_NONNULL_ANNOTATION: AnnotationSpec = toAnnotation("javax.annotation.Nonnull")

  val NOT_NULL_ANNOTATION: AnnotationSpec = toAnnotation("jakarta.validation.constraints.NotNull")

  fun toAnnotation(name: String, value: Any, stringValue: Boolean = true): AnnotationSpec = toAnnotation(name, listOf(value), stringValue)

  fun toAnnotation(name: String, values: List<Any> = emptyList(), stringValue: Boolean = true): AnnotationSpec {
    val builder = AnnotationSpec.builder(ClassName.bestGuess(name))
    values.forEach { builder.addMember("value", if (stringValue) "\$S" else "\$L", it) }
    return builder.build()
  }

  fun toAnnotation(validation: TypeValidation): AnnotationSpec {
    return when (validation) {
      is IntegralValidation -> createIntegralMinMaxAnnotation(validation)
      is DecimalValidation -> createDecimalMinMaxAnnotation(validation)
      is SizeValidation -> createSizeAnnotation(validation)
      is PatternValidation -> createPatternAnnotation(validation)
      is ValidatedValidation -> VALID_ANNOTATION
    }
  }

  fun jsr305NullabilityAnnotation(nonnull: Boolean) = if (nonnull) {
    JSR305_NONNULL_ANNOTATION
  } else {
    JSR305_NULLABLE_ANNOTATION
  }

  private fun createIntegralMinMaxAnnotation(validation: IntegralValidation): AnnotationSpec = when {
    isRepresentableAsLong(validation.value) -> createMinMaxAnnotation(validation)
    else -> createDecimalMinMaxAnnotation(validation.toDecimalValidation())
  }

  /**
   * Returns whether the supplied value is outside the range of values representable with Java type long.
   */
  private fun isRepresentableAsLong(value: BigInteger) = value <= BigInteger.valueOf(Long.MAX_VALUE) && value >= BigInteger.valueOf(Long.MIN_VALUE)

  private fun createMinMaxAnnotation(validation: IntegralValidation): AnnotationSpec {
    val name = when (validation.type) {
      NumericValidationType.MIN -> "Min"
      NumericValidationType.MAX -> "Max"
    }

    return AnnotationSpec.builder(ClassName.get(BEAN_VALIDATION_PACKAGE, name)).addMember("value", "\$LL", validation.value).build()
  }

  private fun createDecimalMinMaxAnnotation(validation: DecimalValidation): AnnotationSpec {
    val name = when (validation.type) {
      NumericValidationType.MIN -> "DecimalMin"
      NumericValidationType.MAX -> "DecimalMax"
    }

    val builder = AnnotationSpec.builder(ClassName.get(BEAN_VALIDATION_PACKAGE, name))
    builder.addMember("value", "\$S", validation.value)

    if (!validation.inclusive) {
      builder.addMember("inclusive", "\$L", false)
    }

    return builder.build()
  }

  private fun createSizeAnnotation(validation: SizeValidation): AnnotationSpec {
    val builder = AnnotationSpec.builder(ClassName.get(BEAN_VALIDATION_PACKAGE, "Size"))
    if (validation.min != null) {
      builder.addMember("min", "\$L", validation.min)
    }
    if (validation.max != null) {
      builder.addMember("max", "\$L", validation.max)
    }
    return builder.build()
  }

  private fun createPatternAnnotation(validation: PatternValidation) =
      AnnotationSpec.builder(ClassName.get(BEAN_VALIDATION_PACKAGE, "Pattern")).addMember("regexp", "\$S", validation.pattern).build()
}