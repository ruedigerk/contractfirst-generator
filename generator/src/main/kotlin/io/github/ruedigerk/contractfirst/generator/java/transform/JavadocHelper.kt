package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.model.ActualSchema

/**
 * For extracting the Javadoc relevant description from a schema.
 */
object JavadocHelper {
  
  fun toJavadoc(schema: ActualSchema): String? = schema.description ?: schema.title
}