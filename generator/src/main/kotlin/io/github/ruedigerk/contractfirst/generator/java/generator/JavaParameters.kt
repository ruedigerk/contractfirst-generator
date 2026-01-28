package io.github.ruedigerk.contractfirst.generator.java.generator

import io.github.ruedigerk.contractfirst.generator.java.model.JavaAnyType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaBodyParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaMultipartBodyParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaMultipartBodyParameter.BodyPartType.ATTACHMENT
import io.github.ruedigerk.contractfirst.generator.java.model.JavaParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaRegularParameter
import io.github.ruedigerk.contractfirst.generator.java.model.JavaTypeName

/**
 * Utility functions for handling Java parameters.
 */
object JavaParameters {

  /**
   * The type of a parameter is usually just the javaType property of the parameter, but for multipart body parameters of type attachment, the Java type is
   * framework-specific and this functions allows determining the correct type to use.
   */
  fun determineParameterType(parameter: JavaParameter, attachmentType: JavaTypeName): JavaAnyType {
    return when (parameter) {
      is JavaBodyParameter, is JavaRegularParameter -> parameter.javaType
      is JavaMultipartBodyParameter -> toMultipartFileParameterType(parameter, attachmentType)
    }
  }

  /**
   * Rewrite attachment type body parts to use Java type Attachment instead of InputStream.
   */
  private fun toMultipartFileParameterType(parameter: JavaMultipartBodyParameter, attachmentType: JavaTypeName): JavaAnyType = when {
    parameter.bodyPartType == ATTACHMENT -> parameter.javaType.rewriteSimpleType(JavaTypeName.INPUT_STREAM, attachmentType)
    else -> parameter.javaType
  }
}
