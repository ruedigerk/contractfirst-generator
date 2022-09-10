package io.github.ruedigerk.contractfirst.generator.model

import java.math.BigDecimal

/**
 * Represents any type of schema, but not schema references.
 */
sealed interface Schema {

  val title: String?
  val description: String?
  val position: Position

  /**
   * Is this schema embedded inline in another schema? Used for naming.
   */
  var embeddedIn: Schema?
}

/**
 * Represents an object schema.
 */
data class ObjectSchema(
    override val title: String?,
    override val description: String?,
    val properties: List<SchemaProperty>,
    override val position: Position,
) : Schema {

  override var embeddedIn: Schema? = null
}

/**
 * Represents a property of an object schema.
 */
data class SchemaProperty(
    val name: String,
    val required: Boolean,
    val schema: SchemaId
)

/**
 * Represents an array schema.
 */
data class ArraySchema(
    override val title: String?,
    override val description: String?,
    val itemSchema: SchemaId,
    val uniqueItems: Boolean,
    val minItems: Int?,
    val maxItems: Int?,
    override val position: Position,
) : Schema {

  override var embeddedIn: Schema? = null
}

/**
 * Represents a special object schema whose property names are not known but only their types, i.e. an additionalProperties schema.
 */
data class MapSchema(
    override val title: String?,
    override val description: String?,
    val valuesSchema: SchemaId,
    val minItems: Int?,
    val maxItems: Int?,
    override val position: Position,
) : Schema {

  override var embeddedIn: Schema? = null
}

/**
 * Represents an enum schema.
 * Currently, enums are always assumed to have type "string".
 */
data class EnumSchema(
    override val title: String?,
    override val description: String?,
    val values: List<String>,
    override val position: Position,
) : Schema {

  override var embeddedIn: Schema? = null
}

/**
 * Represents a schema of a "primitive" type, i.e. a type that is just a boolean, string or number.
 */
data class PrimitiveSchema(
    override val title: String?,
    override val description: String?,
    val type: PrimitiveType,
    val format: String?,
    val minimum: BigDecimal?,
    val maximum: BigDecimal?,
    val exclusiveMinimum: Boolean,
    val exclusiveMaximum: Boolean,
    val minLength: Int?,
    val maxLength: Int?,
    val pattern: String?,
    override val position: Position,
) : Schema {

  override var embeddedIn: Schema? = null
}

/**
 * Represents the type of primitive schema.
 * 
 * Type "null" is currently not supported.
 */
enum class PrimitiveType {

  BOOLEAN,
  INTEGER,
  NUMBER,
  STRING
}