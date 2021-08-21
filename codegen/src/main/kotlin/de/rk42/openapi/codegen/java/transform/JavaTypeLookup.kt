package de.rk42.openapi.codegen.java.transform

import de.rk42.openapi.codegen.Configuration
import de.rk42.openapi.codegen.java.model.JavaAnyType
import de.rk42.openapi.codegen.model.CtrSchema
import de.rk42.openapi.codegen.model.CtrSchemaNonRef
import de.rk42.openapi.codegen.model.CtrSchemaRef

/**
 * Used for looking up the Java type for a schema. Uses SchemaToJavaTypeTransformer to first translate all schemas to their respective types.
 */
class JavaTypeLookup(
    configuration: Configuration,
    val allSchemas: List<CtrSchemaNonRef>
) {

  private val schemasToTypes = createSchemasToTypesLookup(configuration)

  private fun createSchemasToTypesLookup(configuration: Configuration): Map<CtrSchemaNonRef, JavaAnyType> {
    val typeTransformer = SchemaToJavaTypeTransformer(configuration)
    return allSchemas.distinct().associateWith(typeTransformer::toJavaType)
  }

  fun lookupJavaTypeFor(schema: CtrSchema): JavaAnyType {
    if (schema is CtrSchemaRef) {
      throw IllegalStateException("Specification must not contain any CtrSchemaRef instances, but was: $schema")
    }

    return schemasToTypes[schema] ?: throw IllegalArgumentException("Schema not in schemasToTypes: $schema")
  }
}