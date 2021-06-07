package de.rk42.openapi.codegen

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import de.rk42.openapi.codegen.model.java.JavaBasicReference
import de.rk42.openapi.codegen.model.java.JavaCollectionReference
import de.rk42.openapi.codegen.model.java.JavaMapReference
import de.rk42.openapi.codegen.model.java.JavaReference

object JavaTypes {

  fun String.toTypeName(): ClassName = ClassName.bestGuess(this)

  fun JavaReference.toTypeName(): TypeName {
    val baseType = ClassName.get(this.packageName, this.typeName)

    return when (this) {
      is JavaBasicReference -> baseType
      is JavaCollectionReference -> {
        val elementTypeName = this.elementType.toTypeName()
        ParameterizedTypeName.get(baseType, elementTypeName)
      }
      is JavaMapReference -> {
        val keysTypeName = ClassName.get("java.lang", "String")
        val valuesTypeName = this.valuesType.toTypeName()
        ParameterizedTypeName.get(baseType, keysTypeName, valuesTypeName)
      }
    }
  }
}