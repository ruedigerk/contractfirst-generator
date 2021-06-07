package de.rk42.openapi.codegen.java

import com.squareup.javapoet.AnnotationSpec
import de.rk42.openapi.codegen.java.JavaTypes.toTypeName

object JavapoetHelper {

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

  fun toAnnotation(name: String, stringValue: String): AnnotationSpec = toAnnotation(name, listOf(stringValue))
  
  fun toAnnotation(name: String, stringValues: List<String> = emptyList()): AnnotationSpec {
    val builder = AnnotationSpec.builder(name.toTypeName())
    stringValues.forEach { builder.addMember("value", "\$S", it) }
    return builder.build()
  }
}