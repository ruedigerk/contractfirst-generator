package de.rk42.openapi.codegen

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import de.rk42.openapi.codegen.model.java.JavaReference

object JavaTypes {

  fun String.toTypeName(): ClassName = ClassName.bestGuess(this)

  fun JavaReference.toTypeName(): TypeName {
    val baseType = ClassName.get(this.packageName, this.typeName)
    val typeParameter = this.typeParameter

    return if (typeParameter == null) {
      baseType
    } else {
      val typeArgument = typeParameter.toTypeName()
      ParameterizedTypeName.get(baseType, typeArgument)
    }
  }
}