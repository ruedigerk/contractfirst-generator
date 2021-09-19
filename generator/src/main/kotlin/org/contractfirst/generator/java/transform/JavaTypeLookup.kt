package org.contractfirst.generator.java.transform

import org.contractfirst.generator.Configuration
import org.contractfirst.generator.java.model.JavaAnyType
import org.contractfirst.generator.logging.Log
import org.contractfirst.generator.model.MSchema
import org.contractfirst.generator.model.MSchemaNonRef
import org.contractfirst.generator.model.MSchemaRef

/**
 * Used for looking up the Java type for a schema. Uses SchemaToJavaTypeTransformer to first translate all schemas to their respective types.
 */
class JavaTypeLookup(
    private val log: Log,
    configuration: Configuration,
    val allSchemas: List<MSchemaNonRef>
) {

  private val schemasToTypes = createSchemasToTypesLookup(configuration)

  private fun createSchemasToTypesLookup(configuration: Configuration): Map<MSchemaNonRef, JavaAnyType> {
    val typeTransformer = SchemaToJavaTypeTransformer(log, configuration)
    return allSchemas.distinct().associateWith(typeTransformer::toJavaType)
  }

  fun lookupJavaTypeFor(schema: MSchema): JavaAnyType {
    if (schema is MSchemaRef) {
      throw IllegalStateException("Specification must not contain any MSchemaRef instances, but was: $schema")
    }

    return schemasToTypes[schema] ?: throw IllegalArgumentException("Schema not in schemasToTypes: $schema")
  }
}