package de.rk42.openapi.codegen.java.generator

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import de.rk42.openapi.codegen.java.model.DecimalValidation
import de.rk42.openapi.codegen.java.model.IntegralValidation
import de.rk42.openapi.codegen.java.model.JavaAnyType
import de.rk42.openapi.codegen.java.model.JavaCollectionType
import de.rk42.openapi.codegen.java.model.JavaMapType
import de.rk42.openapi.codegen.java.model.JavaType
import de.rk42.openapi.codegen.java.model.NumericValidationType.MAX
import de.rk42.openapi.codegen.java.model.NumericValidationType.MIN
import de.rk42.openapi.codegen.java.model.PatternValidation
import de.rk42.openapi.codegen.java.model.SizeValidation
import de.rk42.openapi.codegen.java.model.TypeValidation
import de.rk42.openapi.codegen.java.model.ValidatedValidation

object GeneratorCommon {

  fun String.toTypeName(): ClassName = ClassName.bestGuess(this)

  fun JavaAnyType.toTypeName(): TypeName {
    val baseType = ClassName.get(this.packageName, this.name)

    return when (this) {
      is JavaType -> baseType
      is JavaCollectionType -> {
        val elementTypeName = this.elementType.toTypeName()
        ParameterizedTypeName.get(baseType, elementTypeName)
      }
      is JavaMapType -> {
        val keysTypeName = ClassName.get("java.lang", "String")
        val valuesTypeName = this.valuesType.toTypeName()
        ParameterizedTypeName.get(baseType, keysTypeName, valuesTypeName)
      }
    }
  }

  fun toAnnotation(name: String, value: Any, stringValue: Boolean = true): AnnotationSpec = toAnnotation(name, listOf(value), stringValue)

  fun toAnnotation(name: String, values: List<Any> = emptyList(), stringValue: Boolean = true): AnnotationSpec {
    val builder = AnnotationSpec.builder(name.toTypeName())
    values.forEach { builder.addMember("value", if (stringValue) "\$S" else "\$L", it) }
    return builder.build()
  }

  fun toAnnotation(validation: TypeValidation): AnnotationSpec {
    return when (validation) {

      is DecimalValidation -> {
        val name = when (validation.type) {
          MIN -> "DecimalMin"
          MAX -> "DecimalMax"
        }
        val builder = AnnotationSpec.builder("javax.validation.constraints.$name".toTypeName())
        builder.addMember("value", "\$S", validation.value)
        if (!validation.inclusive) {
          builder.addMember("inclusive", "\$L", validation.inclusive)
        }
        builder.build()
      }

      is IntegralValidation -> {
        val name = when (validation.type) {
          MIN -> "Min"
          MAX -> "Max"
        }
        AnnotationSpec.builder("javax.validation.constraints.$name".toTypeName()).addMember("value", "\$L", validation.value).build()
      }

      is SizeValidation -> {
        val builder = AnnotationSpec.builder("javax.validation.constraints.Size".toTypeName())
        if (validation.min != null) {
          builder.addMember("min", "\$L", validation.min)
        }
        if (validation.max != null) {
          builder.addMember("max", "\$L", validation.max)
        }
        builder.build()
      }

      is PatternValidation ->
        AnnotationSpec.builder("javax.validation.constraints.Pattern".toTypeName()).addMember("regexp", "\$S", validation.pattern).build()

      is ValidatedValidation -> toAnnotation("javax.validation.Valid")
    }
  }
}