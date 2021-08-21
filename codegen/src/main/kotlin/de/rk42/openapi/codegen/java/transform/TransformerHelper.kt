package de.rk42.openapi.codegen.java.transform

import de.rk42.openapi.codegen.model.CtrSchemaNonRef

object TransformerHelper {
  
  fun toJavadoc(schema: CtrSchemaNonRef): String? = schema.description ?: schema.title
}