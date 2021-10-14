package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.model.ActualSchema
import io.github.ruedigerk.contractfirst.generator.model.Schema
import io.github.ruedigerk.contractfirst.generator.model.SchemaRef

/**
 * Used for looking up schema references.
 */
class SchemaRefLookup(private val topLevelSchemas: Map<SchemaRef, ActualSchema>) {

  /**
   * Look it up if the supplied schema is a reference, or just return it if it is an actual schema.
   */
  fun lookupIfRef(schema: Schema): ActualSchema = when (schema) {
    // SchemaResolver ensures that all schema refs in the contract are resolvable.
    is SchemaRef -> topLevelSchemas[schema]!!
    is ActualSchema -> schema
  }
}