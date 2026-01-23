package io.github.ruedigerk.contractfirst.generator.java.generator

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec
import io.github.ruedigerk.contractfirst.generator.java.model.JavaAnyType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaOperation
import io.github.ruedigerk.contractfirst.generator.java.model.JavaParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaTypeName

/**
 * Implementations of this interface define the variants of the Java server code generator.
 */
interface ServerVariant {

  /**
   * The name of the response class used as return type of resource methods.
   */
  val responseClassName: String

  /**
   * The resources directory containing the ResponseWrapper template file.
   */
  val templateDirectory: String

  /**
   * The Java type name to use for parameters that represent attachment parts of multipart bodies.
   */
  val attachmentTypeName: JavaTypeName

  fun addAnnotationsToJavaInterface(builder: TypeSpec.Builder)

  fun addAnnotationsToOperationMethod(builder: MethodSpec.Builder, operation: JavaOperation)

  fun addAnnotationsToMethodParameter(builder: ParameterSpec.Builder, parameter: JavaParameter)

  fun buildResponseWithEntity(): String

  /**
   * The Java type to use for binary response bodies in typesafe response class methods. By default, it would use InputStream, but variants can choose to
   * rewrite it to framework-specific types.
   */
  fun rewriteResponseBodyType(javaType: JavaAnyType): JavaAnyType
}
