package de.rk42.openapi.codegen

import com.squareup.javapoet.AnnotationSpec
import de.rk42.openapi.codegen.JavaTypes.toTypeName

object JavapoetHelper {
  
  fun toAnnotation(name: String, stringValue: String): AnnotationSpec = toAnnotation(name, listOf(stringValue))
  
  fun toAnnotation(name: String, stringValues: List<String> = emptyList()): AnnotationSpec {
    val builder = AnnotationSpec.builder(name.toTypeName())
    stringValues.forEach { builder.addMember("value", "\$S", it) }
    return builder.build()
  }
}