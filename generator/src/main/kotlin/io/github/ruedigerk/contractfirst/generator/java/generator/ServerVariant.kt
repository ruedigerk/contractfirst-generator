package io.github.ruedigerk.contractfirst.generator.java.generator

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import io.github.ruedigerk.contractfirst.generator.java.model.JavaOperation
import io.github.ruedigerk.contractfirst.generator.java.model.JavaParameter

/**
 * Implementations of this interface define the variants of the server stub generator.
 */
interface ServerVariant {

  fun addAnnotationsToJavaInterface(builder: TypeSpec.Builder)
  fun addAnnotationsToOperationMethod(builder: MethodSpec.Builder, operation: JavaOperation)
  fun addAnnotationsToMethodParameter(builder: ParameterSpec.Builder, parameter: JavaParameter)
  fun toTypesafeResponseClass(operation: JavaOperation): TypeSpec
}
