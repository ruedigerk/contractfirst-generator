package org.contractfirst.generator.java.transform

import org.contractfirst.generator.model.MSchemaNonRef

/**
 * For extracting the Javadoc relevant description from a schema.
 */
object JavadocHelper {
  
  fun toJavadoc(schema: MSchemaNonRef): String? = schema.description ?: schema.title
}