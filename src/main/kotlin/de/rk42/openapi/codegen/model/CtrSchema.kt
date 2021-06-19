package de.rk42.openapi.codegen.model

import java.math.BigDecimal

/**
 * Represents any type of schema in a specification.
 */
sealed interface CtrSchema

/**
 * Represents a schema reference.
 */
data class CtrSchemaRef(
    val reference: String
) : CtrSchema

/**
 * Represents a schema that is not a schema reference, i.e. a "real" schema.
 */
sealed interface CtrSchemaNonRef : CtrSchema {

  val title: String?
  val description: String?
  val nameHint: NameHint
  var embeddedIn: CtrSchemaNonRef?
}

/**
 * Represents an object schema.
 */
data class CtrSchemaObject(
    override val title: String?,
    override val description: String?,
    val properties: List<CtrSchemaProperty>,
    override val nameHint: NameHint,
) : CtrSchemaNonRef {

  override var embeddedIn: CtrSchemaNonRef? = null
}

/**
 * Represents a property of an object schema.
 */
data class CtrSchemaProperty(
    val name: String,
    val required: Boolean,
    var schema: CtrSchema
)

/**
 * Represents an array schema.
 */
data class CtrSchemaArray(
    override val title: String?,
    override val description: String?,
    var itemSchema: CtrSchema,
    val uniqueItems: Boolean,
    val minItems: Int?,
    val maxItems: Int?,
    override val nameHint: NameHint,
) : CtrSchemaNonRef {

  override var embeddedIn: CtrSchemaNonRef? = null
}

/**
 * Represents a special object schema whose property names are not known but only their types, i.e. an additionalProperties schema.
 */
data class CtrSchemaMap(
    override val title: String?,
    override val description: String?,
    var valuesSchema: CtrSchema,
    val minItems: Int?,
    val maxItems: Int?,
    override val nameHint: NameHint,
) : CtrSchemaNonRef {

  override var embeddedIn: CtrSchemaNonRef? = null
}

/**
 * Represents an enum schema.
 * Currently enums are always assumed to habe type "string".
 */
data class CtrSchemaEnum(
    override val title: String?,
    override val description: String?,
    val values: List<String>,
    override val nameHint: NameHint,
) : CtrSchemaNonRef {

  override var embeddedIn: CtrSchemaNonRef? = null
}

/**
 * Represents a schema of a "primitive" type, i.e. a type that is just a boolean, string or number.
 */
data class CtrSchemaPrimitive(
    override val title: String?,
    override val description: String?,
    val type: CtrPrimitiveType,
    val format: String?,
    val minimum: BigDecimal?,
    val maximum: BigDecimal?,
    val exclusiveMinimum: Boolean,
    val exclusiveMaximum: Boolean,
    val minLength: Int?,
    val maxLength: Int?,
    val pattern: String?,
    override val nameHint: NameHint,
) : CtrSchemaNonRef {

  override var embeddedIn: CtrSchemaNonRef? = null
}

/**
 * Represents the type of a primitive schema.
 * Type "null" is currently not supported.
 */
enum class CtrPrimitiveType {

  BOOLEAN,
  INTEGER,
  NUMBER,
  STRING
}