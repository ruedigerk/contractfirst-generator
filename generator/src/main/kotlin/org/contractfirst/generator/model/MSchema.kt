package org.contractfirst.generator.model

import java.math.BigDecimal

/**
 * Represents any type of schema in the contract.
 */
sealed interface MSchema

/**
 * Represents a schema reference.
 */
data class MSchemaRef(
    val reference: String
) : MSchema

/**
 * Represents a schema that is not a schema reference, i.e. a "real" schema.
 */
sealed interface MSchemaNonRef : MSchema {

  val title: String?
  val description: String?
  val nameHint: NameHint
  var embeddedIn: MSchemaNonRef?
}

/**
 * Represents an object schema.
 */
data class MSchemaObject(
    override val title: String?,
    override val description: String?,
    val properties: List<MSchemaProperty>,
    override val nameHint: NameHint,
) : MSchemaNonRef {

  override var embeddedIn: MSchemaNonRef? = null
}

/**
 * Represents a property of an object schema.
 */
data class MSchemaProperty(
    val name: String,
    val required: Boolean,
    var schema: MSchema
)

/**
 * Represents an array schema.
 */
data class MSchemaArray(
    override val title: String?,
    override val description: String?,
    var itemSchema: MSchema,
    val uniqueItems: Boolean,
    val minItems: Int?,
    val maxItems: Int?,
    override val nameHint: NameHint,
) : MSchemaNonRef {

  override var embeddedIn: MSchemaNonRef? = null
}

/**
 * Represents a special object schema whose property names are not known but only their types, i.e. an additionalProperties schema.
 */
data class MSchemaMap(
    override val title: String?,
    override val description: String?,
    var valuesSchema: MSchema,
    val minItems: Int?,
    val maxItems: Int?,
    override val nameHint: NameHint,
) : MSchemaNonRef {

  override var embeddedIn: MSchemaNonRef? = null
}

/**
 * Represents an enum schema.
 * Currently enums are always assumed to habe type "string".
 */
data class MSchemaEnum(
    override val title: String?,
    override val description: String?,
    val values: List<String>,
    override val nameHint: NameHint,
) : MSchemaNonRef {

  override var embeddedIn: MSchemaNonRef? = null
}

/**
 * Represents a schema of a "primitive" type, i.e. a type that is just a boolean, string or number.
 */
data class MSchemaPrimitive(
    override val title: String?,
    override val description: String?,
    val type: MPrimitiveType,
    val format: String?,
    val minimum: BigDecimal?,
    val maximum: BigDecimal?,
    val exclusiveMinimum: Boolean,
    val exclusiveMaximum: Boolean,
    val minLength: Int?,
    val maxLength: Int?,
    val pattern: String?,
    override val nameHint: NameHint,
) : MSchemaNonRef {

  override var embeddedIn: MSchemaNonRef? = null
}

/**
 * Represents the type of a primitive schema.
 * 
 * Type "null" is currently not supported.
 */
enum class MPrimitiveType {

  BOOLEAN,
  INTEGER,
  NUMBER,
  STRING
}