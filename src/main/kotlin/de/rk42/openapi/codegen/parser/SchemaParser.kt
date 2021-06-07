package de.rk42.openapi.codegen.parser

import de.rk42.openapi.codegen.NotSupportedException
import de.rk42.openapi.codegen.model.CtrPrimitiveType.BOOLEAN
import de.rk42.openapi.codegen.model.CtrPrimitiveType.INTEGER
import de.rk42.openapi.codegen.model.CtrPrimitiveType.NUMBER
import de.rk42.openapi.codegen.model.CtrPrimitiveType.STRING
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

object SchemaParser {

  fun parseTopLevelSchemas(schemas: Map<String, Schema<Any>>): Map<CtrSchemaRef, CtrSchemaNonRef> = schemas
      .mapKeys { CtrSchemaRef("#/components/schemas/${it.key}") }
      .mapValues { toTopLevelSchema(it.value) }

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
      "boolean", "integer", "number", "string" -> toPrimitiveSchema(schema)
      "null" -> throw NotSupportedException("Schema type 'null' is not supported")
      else -> toObjectOrMapSchema(schema)
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
    return CtrSchemaArray(schema.title.normalize(), schema.description.normalize(), parseSchema(schema.items as Schema<Any>))
  }

  private fun toPrimitiveSchema(schema: Schema<Any>): CtrSchemaPrimitive {
    val type = when (schema.type) {
      "boolean" -> BOOLEAN
      "integer" -> INTEGER
      "number" -> NUMBER
      "string" -> STRING
      else -> throw IllegalArgumentException("Program error, unexpected type ${schema.type} in toPrimitiveSchema")
    }

    return CtrSchemaPrimitive(schema.title.normalize(), schema.description.normalize(), type, schema.format)
  }

  private fun toObjectOrMapSchema(schema: Schema<Any>): CtrSchemaNonRef {
    if (schema.properties.nullToEmpty().isNotEmpty() && schema.additionalProperties != null) {
      throw NotSupportedException("Object schemas having both properties and additionalProperties is not supported, just either or: $schema")
    }
      
    val additionalPropertiesSchema = parseAdditionalProperties(schema)

    return if (additionalPropertiesSchema != null) {
      toMapSchema(schema, additionalPropertiesSchema)
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
      CtrSchemaMap(schema.title.normalize(), schema.description.normalize(), additionalPropertiesSchema)

  private fun toObjectSchema(schema: Schema<Any>): CtrSchemaObject {
    val requiredProperties = schema.required.nullToEmpty().toSet()

    return CtrSchemaObject(
        schema.title.normalize(),
        schema.description.normalize(),
        schema.properties.nullToEmpty().map { (name, schema) -> CtrSchemaProperty(name, requiredProperties.contains(name), parseSchema(schema)) }
    )
  }
}