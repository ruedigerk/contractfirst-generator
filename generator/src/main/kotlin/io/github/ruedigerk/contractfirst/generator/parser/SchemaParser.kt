package io.github.ruedigerk.contractfirst.generator.parser

import io.github.ruedigerk.contractfirst.generator.NotSupportedException
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.model.*
import io.github.ruedigerk.contractfirst.generator.parser.ParserHelper.normalize
import io.github.ruedigerk.contractfirst.generator.parser.ParserHelper.nullToEmpty
import io.swagger.v3.oas.models.media.ArraySchema as SwaggerArraySchema
import io.swagger.v3.oas.models.media.Schema as SwaggerSchema

/**
 * Parses Swagger schemas to Contractfirst schemas.
 */
class SchemaParser(private val log: Log) {

  /**
   * Parses the supplied Swagger schema to a Contractfirst schemas.
   * @param schema the schema to parse.
   * @param location the location of the schema in the contract. Can later be used for naming.
   */
  fun parseSchema(schema: SwaggerSchema<*>, location: NameHint): Schema {
    log.debug { "Parsing schema $location of ${schema.javaClass.simpleName}" }

    if (schema.`$ref` != null) {
      // TODO: Support "description" together with $ref? 
      return SchemaRef(schema.`$ref`)
    }
    if (schema.enum != null && schema.enum.isNotEmpty()) {
      return toEnumSchema(schema, location)
    }

    return when (schema.type) {
      "array" -> toArraySchema(schema as SwaggerArraySchema, location)
      "boolean", "integer", "number", "string" -> toPrimitiveSchema(PrimitiveType.valueOf(schema.type.uppercase()), schema, location)
      "object", null -> toObjectOrMapSchema(schema, location)
      else -> throw NotSupportedException("Schema type '${schema.type}' is not supported: $schema")
    }
  }

  private fun toEnumSchema(schema: SwaggerSchema<*>, location: NameHint): EnumSchema {
    if (schema.type != "string") {
      throw NotSupportedException("Currently only enum schemas of type 'string' are supported, type is '${schema.type}'")
    }

    return EnumSchema(schema.title.normalize(), schema.description.normalize(), schema.enum.map { it.toString() }, location)
  }

  @Suppress("UNCHECKED_CAST")
  private fun toArraySchema(schema: SwaggerArraySchema, location: NameHint): ArraySchema {
    val itemsSchema = parseSchema(schema.items as SwaggerSchema<*>, location)

    return ArraySchema(
        schema.title.normalize(),
        schema.description.normalize(),
        itemsSchema,
        schema.uniqueItems ?: false,
        schema.minItems,
        schema.maxItems,
        location
    ).also { itemsSchema.embedIn(it) }
  }

  private fun toPrimitiveSchema(primitiveType: PrimitiveType, schema: SwaggerSchema<*>, location: NameHint): PrimitiveSchema {
    return PrimitiveSchema(
        schema.title.normalize(),
        schema.description.normalize(),
        primitiveType,
        schema.format.normalize(),
        schema.minimum,
        schema.maximum,
        schema.exclusiveMinimum ?: false,
        schema.exclusiveMaximum ?: false,
        schema.minLength,
        schema.maxLength,
        schema.pattern,
        location
    )
  }

  private fun toObjectOrMapSchema(schema: SwaggerSchema<*>, location: NameHint): ActualSchema {
    val properties = schema.properties.nullToEmpty()
    val additionalProperties = schema.additionalProperties as? SwaggerSchema<*>
    // TODO: additionalProperties of type Boolean instead of Schema are ignored.

    return when {
      properties.isNotEmpty() && additionalProperties != null -> {
        throw NotSupportedException("Object schemas having both properties and additionalProperties are not supported, just either or: $schema")
      }
      additionalProperties != null -> toMapSchema(schema, parseSchema(additionalProperties, location / "additionalProperties"), location)
      else -> toObjectSchema(schema, location)
    }
  }

  private fun toMapSchema(schema: SwaggerSchema<*>, valuesSchema: Schema, location: NameHint): MapSchema =
      MapSchema(schema.title.normalize(), schema.description.normalize(), valuesSchema, schema.minItems, schema.maxItems, location)
          .also { valuesSchema.embedIn(it) }

  private fun toObjectSchema(schema: SwaggerSchema<*>, location: NameHint): ObjectSchema {
    val requiredProperties = schema.required.nullToEmpty().toSet()
    val properties = schema.properties.nullToEmpty().map { (name, schema) ->
      SchemaProperty(name, requiredProperties.contains(name), parseSchema(schema, location / name))
    }

    val schemaObject = ObjectSchema(schema.title.normalize(), schema.description.normalize(), properties, location)

    properties.forEach { it.schema.embedIn(schemaObject) }

    return schemaObject
  }

  private fun Schema.embedIn(parent: ActualSchema) {
    // Only inline schemas are treated as embedded in another schema, referencing a schema is not treated as embedding. 
    if (this is ActualSchema) {
      this.embeddedIn = parent
    }
  }
}