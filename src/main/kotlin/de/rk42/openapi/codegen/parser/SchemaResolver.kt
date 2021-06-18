package de.rk42.openapi.codegen.parser

import de.rk42.openapi.codegen.model.CtrSchemaArray
import de.rk42.openapi.codegen.model.CtrSchemaMap
import de.rk42.openapi.codegen.model.CtrSchemaNonRef
import de.rk42.openapi.codegen.model.CtrSchemaObject
import de.rk42.openapi.codegen.model.CtrSchemaRef
import de.rk42.openapi.codegen.model.NameHint
import io.swagger.v3.oas.models.media.Schema

/**
 * Used for resolving schema references and determining the set of all schemas of the contract that are actually used. Not all schemas in the
 * "/components/schemas" section of a contract are necessarly used/referenced in the contract. Also, there can be inline schemas in the contract.
 */
class SchemaResolver(topLevelSchemas: Map<String, Schema<Any>>) {

  // All schemas actually being used/referenced in the contract.
  private val referencedSchemas: MutableSet<CtrSchemaNonRef> = mutableSetOf()

  // Top level schemas are the schemas of the components section of the contract. Only they can be referenced by a $ref.
  private val topLevelSchemas: Map<CtrSchemaRef, CtrSchemaNonRef> = SchemaParser.parseTopLevelSchemas(topLevelSchemas)

  fun resolveSchema(schema: Schema<Any>, location: NameHint): CtrSchemaNonRef = when (val parsed = SchemaParser.parseSchema(schema, location)) {
    is CtrSchemaRef -> lookupSchemaRef(parsed)
    is CtrSchemaNonRef -> {
      referencedSchemas.add(parsed)
      parsed
    }
  }

  private fun lookupSchemaRef(schema: CtrSchemaRef): CtrSchemaNonRef {
    val referencedSchema = topLevelSchemas[schema] ?: throw ParserException("Unresolvable schema reference $schema")

    referencedSchemas.add(referencedSchema)

    return referencedSchema
  }

  /**
   * Return all schemas actually being used in the contract.
   */
  fun determineAndResolveReferencedSchemas(): List<CtrSchemaNonRef> {
    if (referencedSchemas.isEmpty()) {
      throw IllegalStateException("determineAndResolveReferencedSchemas must be called after parsing the contract's operations")
    }

    val resolvedSchemas = mutableSetOf<CtrSchemaNonRef>()
    var schemasForResolving = referencedSchemas.toSet()

    // Iteratively resolve all schemas
    do {
      schemasForResolving.forEach(::resolveSchemaComponents)
      resolvedSchemas.addAll(schemasForResolving)
      schemasForResolving = referencedSchemas - resolvedSchemas
    } while (schemasForResolving.isNotEmpty())

    return referencedSchemas.toList()
  }

  private fun resolveSchemaComponents(schema: CtrSchemaNonRef) = when (schema) {
    is CtrSchemaObject -> resolveObjectProperties(schema)
    is CtrSchemaArray -> resolveArrayElements(schema)
    is CtrSchemaMap -> resolveMapValues(schema)
    else -> {
      // do nothing 
    }
  }

  private fun resolveFurther(schema: CtrSchemaNonRef) {
    val newSchema = referencedSchemas.add(schema)
    if (newSchema) {
      resolveSchemaComponents(schema)
    }
  }

  private fun resolveObjectProperties(schema: CtrSchemaObject) {
    schema.properties.forEach { property ->
      when (val propertySchema = property.schema) {
        is CtrSchemaRef -> property.schema = lookupSchemaRef(propertySchema)
        is CtrSchemaNonRef -> resolveFurther(propertySchema)
      }
    }
  }

  private fun resolveArrayElements(schema: CtrSchemaArray) {
    when (val itemSchema = schema.itemSchema) {
      is CtrSchemaRef -> schema.itemSchema = lookupSchemaRef(itemSchema)
      is CtrSchemaNonRef -> resolveFurther(itemSchema)
    }
  }

  private fun resolveMapValues(schema: CtrSchemaMap) {
    when (val valuesSchema = schema.valuesSchema) {
      is CtrSchemaRef -> schema.valuesSchema = lookupSchemaRef(valuesSchema)
      is CtrSchemaNonRef -> resolveFurther(valuesSchema)
    }
  }
}