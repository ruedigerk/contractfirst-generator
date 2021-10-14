package io.github.ruedigerk.contractfirst.generator.parser

import io.github.ruedigerk.contractfirst.generator.NotSupportedException
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.model.*
import io.swagger.v3.oas.models.media.Schema as SwaggerSchema

/**
 * Used for finding all schemas used in the contract. For a schema to be used, it is not sufficient for it to be listed in the "/components/schemas" section of
 * the contract. It must be referenced in a content element of the contract or be at least transitively referenced in schemas that are.
 */
class SchemaResolver(log: Log, topLevelSchemas: Map<String, SwaggerSchema<Any>>) {

  private val schemaParser = SchemaParser(log)

  // Top level schemas are the schemas of the components section of the contract. Only they can be referenced by a $ref.
  private val topLevelSchemas: Map<SchemaRef, ActualSchema> = topLevelSchemas
      .mapValues { schemaParser.parseSchema(it.value, NameHint(it.key)) }
      .mapValues { it.value as? ActualSchema ?: throw NotSupportedException("Unsupported schema reference in #/components/schemas: ${it.value}") }
      .mapKeys { SchemaRef("#/components/schemas/${it.key}") }

  // All schemas actually being used in the contract.
  private val usedSchemas: MutableSet<ActualSchema> = mutableSetOf()

  /**
   * Parses and returns the schema. Also remembers that it is used in the contract. In case of a reference, remembers the referenced schema.
   */
  fun parseSchema(schema: SwaggerSchema<Any>, location: NameHint): Schema =
      schemaParser.parseSchema(schema, location).also {
        when (it) {
          is SchemaRef -> rememberReferencedSchema(it)
          is ActualSchema -> usedSchemas.add(it)
        }
      }

  private fun rememberReferencedSchema(schemaRef: SchemaRef) {
    val schema: Schema = topLevelSchemas[schemaRef] ?: throw ParserException("Unresolvable schema reference $schemaRef")
    val referencedSchema = schema as? ActualSchema ?: throw NotSupportedException("Unsupported: schema reference pointing to reference: $schemaRef -> $schema")
    usedSchemas.add(referencedSchema)
  }

  /**
   * Finds all schemas actually being used in the contract. This is done by recursively examining all schemas remembered by parseSchema. Must be called after
   * the contract has been parsed.
   */
  fun findAllUsedSchemas(): Schemas {
    if (usedSchemas.isEmpty()) {
      throw IllegalStateException("determineAndResolveReferencedSchemas must be called after parsing the contract's operations")
    }

    val foundSchemas = mutableSetOf<ActualSchema>()
    var schemasToExamine = usedSchemas.toSet()

    // Iteratively resolve all child schemas.
    do {
      schemasToExamine.forEach(::examineSchema)
      foundSchemas.addAll(schemasToExamine)
      schemasToExamine = usedSchemas - foundSchemas
    } while (schemasToExamine.isNotEmpty())

    return Schemas(usedSchemas.toSet(), topLevelSchemas.filterValues { usedSchemas.contains(it) })
  }

  /**
   * Recursively examines the schema for embedded schemas and remembers them by adding them to the usedSchemas set.
   */
  private fun examineSchema(schema: ActualSchema) = when (schema) {
    is ObjectSchema -> examineObjectProperties(schema)
    is ArraySchema -> examineArrayItems(schema)
    is MapSchema -> examineMapValues(schema)
    else -> {
      // do nothing 
    }
  }

  private fun rememberAndExamineSchema(schema: ActualSchema) {
    val newSchema = usedSchemas.add(schema)
    if (newSchema) {
      examineSchema(schema)
    }
  }

  private fun examineObjectProperties(schema: ObjectSchema) {
    schema.properties.forEach { property ->
      when (val propertySchema = property.schema) {
        is SchemaRef -> rememberReferencedSchema(propertySchema)
        is ActualSchema -> rememberAndExamineSchema(propertySchema)
      }
    }
  }

  private fun examineArrayItems(schema: ArraySchema) {
    when (val itemSchema = schema.itemSchema) {
      is SchemaRef -> rememberReferencedSchema(itemSchema)
      is ActualSchema -> rememberAndExamineSchema(itemSchema)
    }
  }

  private fun examineMapValues(schema: MapSchema) {
    when (val valuesSchema = schema.valuesSchema) {
      is SchemaRef -> rememberReferencedSchema(valuesSchema)
      is ActualSchema -> rememberAndExamineSchema(valuesSchema)
    }
  }

  data class Schemas(
      val allSchemas: Set<ActualSchema>,
      val topLevelSchemas: Map<SchemaRef, ActualSchema>,
  )
}