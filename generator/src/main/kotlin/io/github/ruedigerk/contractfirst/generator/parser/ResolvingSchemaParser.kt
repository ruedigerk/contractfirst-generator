package io.github.ruedigerk.contractfirst.generator.parser

import io.github.ruedigerk.contractfirst.generator.NotSupportedException
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.openapi.ArraySchema
import io.github.ruedigerk.contractfirst.generator.openapi.EnumSchema
import io.github.ruedigerk.contractfirst.generator.openapi.MapSchema
import io.github.ruedigerk.contractfirst.generator.openapi.ObjectSchema
import io.github.ruedigerk.contractfirst.generator.openapi.PrimitiveSchema
import io.github.ruedigerk.contractfirst.generator.openapi.PrimitiveType
import io.github.ruedigerk.contractfirst.generator.openapi.Schema
import io.github.ruedigerk.contractfirst.generator.openapi.SchemaId
import io.github.ruedigerk.contractfirst.generator.openapi.SchemaProperty
import java.io.File

/**
 * A parser for JSON Schema files in either JSON oder YAML format.
 */
class ResolvingSchemaParser(
  private val log: Log,
  private val parseableCache: ParseableCache,
) {

  private val schemasToParse = ArrayDeque<Parseable>()
  private val visitedSchemas = mutableSetOf<SchemaId>()

  fun parseAndResolveAll(schemas: Collection<Parseable>): Map<SchemaId, Schema> {
    schemasToParse.addAll(schemas)

    val parsedSchemas = mutableMapOf<SchemaId, Schema>()

    while (schemasToParse.isNotEmpty()) {
      val parseable = schemasToParse.removeFirst()
      val id = SchemaId(parseable)

      // Enter the schema ID in visitedSchemas before parsing it, to avoid adding it to schemasToParse again during parsing.
      visitedSchemas.add(id)

      val schema = parseSchema(parseable)
      parsedSchemas[id] = schema
    }

    return parsedSchemas
  }

  private fun dereferenceAndRememberSchema(schemaOrReference: Parseable): SchemaId {
    val schema = parseableCache.resolveWhileReference(schemaOrReference)
    val id = SchemaId(schema)

    if (id !in visitedSchemas) {
      schemasToParse.addLast(schema)
    }

    return id
  }

  /**
   * The supplied parseable must not be a schema reference.
   */
  private fun parseSchema(parseable: Parseable): Schema {
    log.debug { "Parsing schema ${parseable.position}" }

    if (parseable.isReference()) {
      throw IllegalArgumentException(
        "Parseable supplied to parseSchema must not be a schema reference, but was ${parseable.getReference()} at ${parseable.position}",
      )
    }

    val type = parseable.optionalField("type").string()

    if (isEnum(parseable)) {
      if (type == null || type == "string") {
        // Assume type is meant to be string if the type is omitted.
        return toEnumOfStringSchema(parseable)
      } else {
        // Enums that are not of type string are ignored.
        log.warn { "Only enums of type string are supported, generating non-enum type for ${parseable.position} of type $type." }
      }
    }

    return when (type) {
      "array" -> toArraySchema(parseable)
      "boolean", "integer", "number", "string" -> toPrimitiveSchema(PrimitiveType.valueOf(type.uppercase()), parseable)
      "object", null -> toObjectOrMapSchema(parseable)
      else -> throw NotSupportedException("Schema type '$type' is not supported at ${parseable.position}")
    }
  }

  private fun isEnum(parseable: Parseable): Boolean {
    return parseable.optionalField("enum").isPresent()
  }

  private fun toEnumOfStringSchema(parseable: Parseable): EnumSchema {
    val enumValues = parseable.requiredField("enum").requireArray().requireNonEmpty().stringElements()

    return EnumSchema(
      parseable.optionalField("title").string().normalize(),
      parseable.optionalField("description").string().normalize(),
      enumValues,
      parseable.position,
    )
  }

  private fun toArraySchema(parseable: Parseable): ArraySchema {
    val itemsParseable = parseable.requiredField("items").requireObject()
    val itemsSchema = dereferenceAndRememberSchema(itemsParseable)

    return ArraySchema(
      parseable.optionalField("title").string().normalize(),
      parseable.optionalField("description").string().normalize(),
      itemsSchema,
      parseable.optionalField("uniqueItems").boolean() ?: false,
      parseable.optionalField("minItems").int(),
      parseable.optionalField("maxItems").int(),
      parseable.position,
    )
  }

  private fun toPrimitiveSchema(primitiveType: PrimitiveType, parseable: Parseable): PrimitiveSchema {
    return PrimitiveSchema(
      parseable.optionalField("title").string().normalize(),
      parseable.optionalField("description").string().normalize(),
      primitiveType,
      parseable.optionalField("format").string().normalize(),
      parseable.optionalField("minimum").number(),
      parseable.optionalField("maximum").number(),
      parseable.optionalField("exclusiveMinimum").boolean() ?: false,
      parseable.optionalField("exclusiveMaximum").boolean() ?: false,
      parseable.optionalField("minLength").int(),
      parseable.optionalField("maxLength").int(),
      parseable.optionalField("pattern").string(),
      parseable.position,
    )
  }

  private fun toObjectOrMapSchema(parseable: Parseable): Schema {
    val properties = parseable.optionalField("properties").let { if (!it.isPresent() || !it.isObject() || it.isEmpty()) null else it }

    // TODO: Support additionalProperties of type Boolean instead of type Object/Schema.
    val additionalProperties = parseable.optionalField("additionalProperties").let { if (!it.isPresent() || !it.isObject()) null else it }

    return when {
      properties != null && additionalProperties != null -> throw NotSupportedException(
        "Object schemas having both properties and additionalProperties are not supported, just either or, at ${parseable.position}",
      )

      additionalProperties != null -> toMapSchema(parseable, additionalProperties)

      else -> toObjectSchema(parseable)
    }
  }

  private fun toMapSchema(parseable: Parseable, valuesParseable: Parseable): MapSchema {
    val valuesSchema = dereferenceAndRememberSchema(valuesParseable)

    return MapSchema(
      parseable.optionalField("title").string().normalize(),
      parseable.optionalField("description").string().normalize(),
      valuesSchema,
      parseable.optionalField("minItems").int(),
      parseable.optionalField("maxItems").int(),
      parseable.position,
    )
  }

  private fun toObjectSchema(parseable: Parseable): ObjectSchema {
    val requiredProperties = parseable.optionalField("required").stringElements().toSet()
    val properties: List<SchemaProperty> = parseable.optionalField("properties").properties().map { (name, propertySchemaParseable) ->
      val propertySchema = dereferenceAndRememberSchema(propertySchemaParseable)
      SchemaProperty(name, requiredProperties.contains(name), propertySchema)
    }

    return ObjectSchema(
      parseable.optionalField("title").string().normalize(),
      parseable.optionalField("description").string().normalize(),
      properties,
      parseable.position,
    )
  }

  companion object {

    /**
     * Utility method for parsing standalone schema files, instead of parsing the schemas referenced from an OpenAPI contract.
     */
    @JvmStatic
    fun parseAndResolveAll(log: Log, files: Collection<File>): Map<SchemaId, Schema> {
      val parseableCache = ParseableCache()
      val parseables = files.map { parseableCache.get(it) }
      val parser = ResolvingSchemaParser(log, parseableCache)

      return parser.parseAndResolveAll(parseables)
    }
  }
}
