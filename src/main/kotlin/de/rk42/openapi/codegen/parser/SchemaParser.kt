package de.rk42.openapi.codegen.parser

import de.rk42.openapi.codegen.NotSupportedException
import de.rk42.openapi.codegen.model.CtrPrimitiveType
import de.rk42.openapi.codegen.model.CtrSchema
import de.rk42.openapi.codegen.model.CtrSchemaArray
import de.rk42.openapi.codegen.model.CtrSchemaEnum
import de.rk42.openapi.codegen.model.CtrSchemaMap
import de.rk42.openapi.codegen.model.CtrSchemaNonRef
import de.rk42.openapi.codegen.model.CtrSchemaObject
import de.rk42.openapi.codegen.model.CtrSchemaPrimitive
import de.rk42.openapi.codegen.model.CtrSchemaProperty
import de.rk42.openapi.codegen.model.CtrSchemaRef
import de.rk42.openapi.codegen.parser.ParserHelper.normalize
import de.rk42.openapi.codegen.parser.ParserHelper.nullToEmpty
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object SchemaParser {

  private val log: Logger = LoggerFactory.getLogger(SchemaParser::class.java)
  
  fun parseTopLevelSchemas(schemas: Map<String, Schema<Any>>): Map<CtrSchemaRef, CtrSchemaNonRef> = schemas
      .mapKeys { CtrSchemaRef("#/components/schemas/${it.key}") }
      .mapValues { toTopLevelSchema(it.value) }
      .onEach { log.debug("Parsed schema: {}", it.key.reference) }

  private fun toTopLevelSchema(schema: Schema<Any>): CtrSchemaNonRef =
      (parseSchema(schema) as? CtrSchemaNonRef) ?: throw NotSupportedException("Unsupported schema reference in #/components/schemas: $schema")

  fun parseSchema(schema: Schema<Any>): CtrSchema {
    if (schema.`$ref` != null) {
      return CtrSchemaRef(schema.`$ref`)
    }
    if (schema.enum != null && schema.enum.isNotEmpty()) {
      return toEnumSchema(schema)
    }

    return when (schema.type) {
      "array" -> toArraySchema(schema as ArraySchema)
      "boolean", "integer", "number", "string" -> toPrimitiveSchema(CtrPrimitiveType.valueOf(schema.type.uppercase()), schema)
      "object", null -> toObjectOrMapSchema(schema)
      else -> throw NotSupportedException("Schema type '${schema.type}' is not supported: $schema")
    }
  }

  private fun toEnumSchema(schema: Schema<Any>): CtrSchemaEnum {
    if (schema.type != "string") {
      throw NotSupportedException("Currently only enum schemas of type 'string' are supported, type is '${schema.type}'")
    }

    return CtrSchemaEnum(schema.title.normalize(), schema.description.normalize(), schema.enum.map { it.toString() })
  }

  @Suppress("UNCHECKED_CAST")
  private fun toArraySchema(schema: ArraySchema): CtrSchemaArray {
    return CtrSchemaArray(
        schema.title.normalize(),
        schema.description.normalize(),
        parseSchema(schema.items as Schema<Any>),
        schema.uniqueItems ?: false,
        schema.minItems,
        schema.maxItems
    )
  }

  private fun toPrimitiveSchema(primitiveType: CtrPrimitiveType, schema: Schema<Any>): CtrSchemaPrimitive {
    return CtrSchemaPrimitive(
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
        schema.pattern
    )
  }

  private fun toObjectOrMapSchema(schema: Schema<Any>): CtrSchemaNonRef {
    if (schema.properties.nullToEmpty().isNotEmpty() && schema.additionalProperties != null) {
      throw NotSupportedException("Object schemas having both properties and additionalProperties are not supported, just either or: $schema")
    }

    val additionalPropertiesValuesSchema = parseAdditionalProperties(schema)

    return if (additionalPropertiesValuesSchema != null) {
      toMapSchema(schema, additionalPropertiesValuesSchema)
    } else {
      toObjectSchema(schema)
    }
  }

  @Suppress("UNCHECKED_CAST")
  private fun parseAdditionalProperties(schema: Schema<Any>): CtrSchema? {
    val additionalProperties = schema.additionalProperties
    return if (additionalProperties is Schema<*>) parseSchema(additionalProperties as Schema<Any>) else null
  }

  private fun toMapSchema(schema: Schema<Any>, additionalPropertiesSchema: CtrSchema): CtrSchemaMap =
      CtrSchemaMap(schema.title.normalize(), schema.description.normalize(), additionalPropertiesSchema, schema.minItems, schema.maxItems)

  private fun toObjectSchema(schema: Schema<Any>): CtrSchemaObject {
    val requiredProperties = schema.required.nullToEmpty().toSet()

    return CtrSchemaObject(
        schema.title.normalize(),
        schema.description.normalize(),
        schema.properties.nullToEmpty().map { (name, schema) -> CtrSchemaProperty(name, requiredProperties.contains(name), parseSchema(schema)) }
    )
  }
}