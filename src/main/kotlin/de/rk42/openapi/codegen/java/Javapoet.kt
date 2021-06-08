package de.rk42.openapi.codegen.java

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import de.rk42.openapi.codegen.java.model.JavaAnyType
import de.rk42.openapi.codegen.java.model.JavaCollectionType
import de.rk42.openapi.codegen.java.model.JavaMapType
import de.rk42.openapi.codegen.java.model.JavaType

object Javapoet {

  fun <T> T.doIf(condition: Boolean, action: T.() -> Unit): T {
    if (condition) {
      action(this)
    }
    return this
  }

  fun <T, V> T.doIfNotNull(value: V?, action: T.(V) -> Unit): T {
    if (value != null) {
      action(this, value)
    }
    return this
  }

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

  fun toAnnotation(name: String, stringValue: String): AnnotationSpec = toAnnotation(name, listOf(stringValue))
  
  fun toAnnotation(name: String, stringValues: List<String> = emptyList()): AnnotationSpec {
    val builder = AnnotationSpec.builder(name.toTypeName())
    stringValues.forEach { builder.addMember("value", "\$S", it) }
    return builder.build()
  }
}