package de.rk42.openapi.codegen.model.contract

sealed interface CtrSchema

data class CtrSchemaRef(
    val reference: String
) : CtrSchema

sealed interface CtrSchemaNonRef : CtrSchema

data class CtrSchemaObject(
    val title: String,
    val properties: List<CtrSchemaProperty>,
) : CtrSchemaNonRef

data class CtrSchemaProperty(
    val name: String,
    val required: Boolean,
    var schema: CtrSchema
)

data class CtrSchemaArray(
    val title: String,
    var itemSchema: CtrSchema
) : CtrSchemaNonRef

/**
 * Currently Enums are always assumed to habe type "string".
 */
data class CtrSchemaEnum(
    val title: String,
    val values: List<String>
) : CtrSchemaNonRef

data class CtrSchemaPrimitive(
    val type: CtrPrimitiveType,
    val title: String,
) : CtrSchemaNonRef

/**
 * Type "null" is currently not supported.
 */
enum class CtrPrimitiveType {

  BOOLEAN,
  INTEGER,
  NUMBER,
  STRING
}