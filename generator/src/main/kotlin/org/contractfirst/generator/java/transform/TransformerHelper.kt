package org.contractfirst.generator.java.transform

import org.contractfirst.generator.model.CtrSchemaNonRef

object TransformerHelper {
  
  fun toJavadoc(schema: CtrSchemaNonRef): String? = schema.description ?: schema.title
}