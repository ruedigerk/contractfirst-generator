package io.github.ruedigerk.contractfirst.generator.java.generator

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import io.github.ruedigerk.contractfirst.generator.java.model.JavaAnyType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaCollectionType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaMapType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaTypeName

/**
 * Code for creating Javapoet TypeNames.
 */
object TypeNames {

  fun JavaTypeName.toClassName(): ClassName = ClassName.get(this.packageName, this.simpleName)

  fun String.toClassName(): ClassName = ClassName.bestGuess(this)

  fun JavaAnyType.toTypeName(withValidationAnnotations: Boolean = false): TypeName {
    val baseType = name.toClassName()

    return when (this) {
      is JavaType -> {
        baseType
      }

      is JavaCollectionType -> {
        val elementTypeName = toOptionallyValidatedTypeName(elementType, withValidationAnnotations)
        ParameterizedTypeName.get(baseType, elementTypeName)
      }

      is JavaMapType -> {
        val keysTypeName = ClassName.get("java.lang", "String")
        val valuesTypeName = toOptionallyValidatedTypeName(valuesType, withValidationAnnotations)
        ParameterizedTypeName.get(baseType, keysTypeName, valuesTypeName)
      }
    }
  }

  private fun toOptionallyValidatedTypeName(javaType: JavaAnyType, withValidationAnnotations: Boolean): TypeName = if (withValidationAnnotations) {
    javaType.toTypeName().annotated(javaType.validations.map(Annotations::toAnnotation))
  } else {
    javaType.toTypeName()
  }
}
