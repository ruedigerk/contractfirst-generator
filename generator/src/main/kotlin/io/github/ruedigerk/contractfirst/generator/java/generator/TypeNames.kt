package io.github.ruedigerk.contractfirst.generator.java.generator

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import io.github.ruedigerk.contractfirst.generator.java.model.*

/**
 * Code for creating Javapoet TypeNames.
 */
object TypeNames {

  fun String.toTypeName(): ClassName = ClassName.bestGuess(this)

  fun JavaAnyType.toTypeName(withValidationAnnotations: Boolean = false): TypeName {
    val baseType = ClassName.get(this.packageName, this.name)

    return when (this) {
      is JavaType -> baseType
      is JavaCollectionType -> {
        val elementTypeName = toOptionallyValidatedTypeName(this.elementType, withValidationAnnotations)
        ParameterizedTypeName.get(baseType, elementTypeName)
      }
      is JavaMapType -> {
        val keysTypeName = ClassName.get("java.lang", "String")
        val valuesTypeName = toOptionallyValidatedTypeName(this.valuesType, withValidationAnnotations)
        ParameterizedTypeName.get(baseType, keysTypeName, valuesTypeName)
      }
    }
  }

  private fun toOptionallyValidatedTypeName(javaType: JavaAnyType, withValidationAnnotations: Boolean): TypeName =
      if (withValidationAnnotations) {
        javaType.toTypeName().annotated(javaType.validations.map(Annotations::toAnnotation))
      } else {
        javaType.toTypeName()
      }
}