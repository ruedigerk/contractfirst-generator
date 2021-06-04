package de.rk42.openapi.codegen.model.contract

sealed interface CtrSchema

data class CtrSchemaRef(
    val reference: String
) : CtrSchema {

  fun referencedName(): String {
    val lastSlashIndex = reference.lastIndexOf('/')
    return reference.substring(lastSlashIndex + 1)
  }
}

sealed interface CtrSchemaNonRef : CtrSchema {

  /** The reference this schema is referenced by (optional) */
  var reference: CtrSchemaRef?
}

data class CtrSchemaObject(
    val title: String,
    val properties: List<CtrSchemaProperty>,
    override var reference: CtrSchemaRef? = null
) : CtrSchemaNonRef

data class CtrSchemaProperty(
    val name: String,
    val required: Boolean,
    var schema: CtrSchema
)

data class CtrSchemaArray(
    val title: String,
    var itemSchema: CtrSchema,
    override var reference: CtrSchemaRef? = null
) : CtrSchemaNonRef

/**
 * Currently Enums are always assumed to habe type "string".
 */
data class CtrSchemaEnum(
    val title: String,
    val values: List<String>,
    override var reference: CtrSchemaRef? = null
) : CtrSchemaNonRef

data class CtrSchemaPrimitive(
    val type: CtrPrimitiveType,
    val format: String?,
    val title: String,
    override var reference: CtrSchemaRef? = null
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