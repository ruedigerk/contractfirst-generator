package de.rk42.openapi.codegen.model.contract

sealed interface CtrSchema

data class CtrSchemaRef(
    val reference: String
) : CtrSchema

data class CtrSchemaObject(
    val title: String,
    val properties: Map<String, CtrSchema>,
    val required: Set<String>,
) : CtrSchema

data class CtrSchemaArray(
    val title: String,
    val items: CtrSchema,
) : CtrSchema

/**
 * Currently Enums are always asumed to habe type "string".
 */
data class CtrSchemaEnum(
    val title: String,
    val values: List<String>
) : CtrSchema

data class CtrSchemaPrimitive(
    val type: CtrPrimitiveType,
    val title: String,
) : CtrSchema

/**
 * Type "null" is currently not supported
 */
enum class CtrPrimitiveType {

  BOOLEAN,
  INTEGER,
  NUMBER,
  STRING
}