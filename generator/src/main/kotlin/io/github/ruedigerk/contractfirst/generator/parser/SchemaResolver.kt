package io.github.ruedigerk.contractfirst.generator.parser

import io.swagger.v3.oas.models.media.Schema
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.model.*

/**
 * Used for resolving schema references and determining the set of all schemas of the contract that are actually used. Not all schemas in the
 * "/components/schemas" section of a contract are necessarily used/referenced in the contract. Also, there can be inline schemas in the contract.
 */
class SchemaResolver(log: Log, topLevelSchemas: Map<String, Schema<Any>>) {

  private val schemaParser = SchemaParser(log)

  // All schemas actually being used/referenced in the contract.
  private val referencedSchemas: MutableSet<MSchemaNonRef> = mutableSetOf()

  // Top level schemas are the schemas of the components section of the contract. Only they can be referenced by a $ref.
  private val topLevelSchemas: Map<MSchemaRef, MSchemaNonRef> = schemaParser.parseTopLevelSchemas(topLevelSchemas)

  fun resolveSchema(schema: Schema<Any>, location: NameHint): MSchemaNonRef =
      when (val parsed = schemaParser.parseSchema(schema, location)) {
        is MSchemaRef    -> lookupSchemaRef(parsed)
        is MSchemaNonRef -> {
          referencedSchemas.add(parsed)
          parsed
        }
      }

  private fun lookupSchemaRef(schema: MSchemaRef): MSchemaNonRef {
    val referencedSchema = topLevelSchemas[schema] ?: throw ParserException("Unresolvable schema reference $schema")
    referencedSchemas.add(referencedSchema)
    return referencedSchema
  }

  /**
   * Resolves all schemas actually being used in the contract and returns them. A schema is resolved, when all schema references in its children (its
   * properties, items, etc.) have been replaced with the referenced schemas and these referenced schemas themselves have been resolved, too.
   */
  fun determineAndResolveReferencedSchemas(): List<MSchemaNonRef> {
    if (referencedSchemas.isEmpty()) {
      throw IllegalStateException("determineAndResolveReferencedSchemas must be called after parsing the contract's operations")
    }

    val resolvedSchemas = mutableSetOf<MSchemaNonRef>()
    var schemasForResolving = referencedSchemas.toSet()

    // Iteratively resolve all child schemas. Not done recursively to avoid endless cycles on self-referencing schemas/cyclic schemas.
    do {
      schemasForResolving.forEach(::resolveSchemaComponents)
      resolvedSchemas.addAll(schemasForResolving)
      schemasForResolving = referencedSchemas - resolvedSchemas
    } while (schemasForResolving.isNotEmpty())

    return referencedSchemas.toList()
  }

  private fun resolveSchemaComponents(schema: MSchemaNonRef) = when (schema) {
    is MSchemaObject -> resolveObjectProperties(schema)
    is MSchemaArray  -> resolveArrayElements(schema)
    is MSchemaMap    -> resolveMapValues(schema)
    else               -> {
      // do nothing 
    }
  }

  private fun resolveFurther(schema: MSchemaNonRef) {
    val newSchema = referencedSchemas.add(schema)
    if (newSchema) {
      resolveSchemaComponents(schema)
    }
  }

  private fun resolveObjectProperties(schema: MSchemaObject) {
    schema.properties.forEach { property ->
      when (val propertySchema = property.schema) {
        is MSchemaRef    -> property.schema = lookupSchemaRef(propertySchema)
        is MSchemaNonRef -> resolveFurther(propertySchema)
      }
    }
  }

  private fun resolveArrayElements(schema: MSchemaArray) {
    when (val itemSchema = schema.itemSchema) {
      is MSchemaRef    -> schema.itemSchema = lookupSchemaRef(itemSchema)
      is MSchemaNonRef -> resolveFurther(itemSchema)
    }
  }

  private fun resolveMapValues(schema: MSchemaMap) {
    when (val valuesSchema = schema.valuesSchema) {
      is MSchemaRef    -> schema.valuesSchema = lookupSchemaRef(valuesSchema)
      is MSchemaNonRef -> resolveFurther(valuesSchema)
    }
  }
}