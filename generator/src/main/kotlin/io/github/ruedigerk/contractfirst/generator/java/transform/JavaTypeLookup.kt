package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.Configuration
import io.github.ruedigerk.contractfirst.generator.java.model.JavaAnyType
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.model.ActualSchema
import io.github.ruedigerk.contractfirst.generator.model.Schema
import io.github.ruedigerk.contractfirst.generator.model.SchemaRef

/**
 * Used for looking up the Java type for a schema. Uses SchemaToJavaTypeTransformer to first translate all schemas to their respective types.
 */
class JavaTypeLookup(
    private val log: Log,
    configuration: Configuration,
    val allSchemas: Set<ActualSchema>,
    topLevelSchemas: Map<SchemaRef, ActualSchema>
) {

  private val schemaRefLookup = SchemaRefLookup(topLevelSchemas)
  private val javaTypesForSchemas: Map<ActualSchema, JavaAnyType> = createSchemasToTypesLookup(configuration)

  private fun createSchemasToTypesLookup(configuration: Configuration): Map<ActualSchema, JavaAnyType> {
    val typeTransformer = SchemaToJavaTypeTransformer(log, configuration, schemaRefLookup)
    return allSchemas.associateWith(typeTransformer::toJavaType)
  }

  fun lookupJavaTypeFor(schema: Schema): JavaAnyType = lookupJavaTypeFor(schemaRefLookup.lookupIfRef(schema))

  // SchemaResolver ensures that all schema used in the contract are known and therefore contained in javaTypesForSchemas. 
  fun lookupJavaTypeFor(schema: ActualSchema): JavaAnyType = javaTypesForSchemas[schema]!!

  fun lookupIfRef(schema: Schema): ActualSchema = schemaRefLookup.lookupIfRef(schema)
}