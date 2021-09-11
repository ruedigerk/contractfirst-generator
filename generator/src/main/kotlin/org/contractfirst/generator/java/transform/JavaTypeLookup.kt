package org.contractfirst.generator.java.transform

import org.contractfirst.generator.Configuration
import org.contractfirst.generator.java.model.JavaAnyType
import org.contractfirst.generator.logging.Log
import org.contractfirst.generator.model.CtrSchema
import org.contractfirst.generator.model.CtrSchemaNonRef
import org.contractfirst.generator.model.CtrSchemaRef

/**
 * Used for looking up the Java type for a schema. Uses SchemaToJavaTypeTransformer to first translate all schemas to their respective types.
 */
class JavaTypeLookup(
    private val log: Log,
    configuration: Configuration,
    val allSchemas: List<CtrSchemaNonRef>
) {

  private val schemasToTypes = createSchemasToTypesLookup(configuration)

  private fun createSchemasToTypesLookup(configuration: Configuration): Map<CtrSchemaNonRef, JavaAnyType> {
    val typeTransformer = SchemaToJavaTypeTransformer(log, configuration)
    return allSchemas.distinct().associateWith(typeTransformer::toJavaType)
  }

  fun lookupJavaTypeFor(schema: CtrSchema): JavaAnyType {
    if (schema is CtrSchemaRef) {
      throw IllegalStateException("Specification must not contain any CtrSchemaRef instances, but was: $schema")
    }

    return schemasToTypes[schema] ?: throw IllegalArgumentException("Schema not in schemasToTypes: $schema")
  }
}