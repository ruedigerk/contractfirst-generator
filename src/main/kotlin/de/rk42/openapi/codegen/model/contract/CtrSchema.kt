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
    
    
) : CtrSchemaNonRef {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as CtrSchemaArray

    if (title != other.title) return false
    if (itemSchema != other.itemSchema) return false

    return true
  }

  override fun hashCode(): Int {
    var result = title.hashCode()
    result = 31 * result + itemSchema.hashCode()
    return result
  }
}

/**
 * Currently Enums are always asumed to habe type "string".
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
 * Type "null" is currently not supported
 */
enum class CtrPrimitiveType {

  BOOLEAN,
  INTEGER,
  NUMBER,
  STRING
}