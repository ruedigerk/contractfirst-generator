package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.Configuration
import io.github.ruedigerk.contractfirst.generator.java.model.JavaAnyType
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.model.MSchema
import io.github.ruedigerk.contractfirst.generator.model.MSchemaNonRef
import io.github.ruedigerk.contractfirst.generator.model.MSchemaRef

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