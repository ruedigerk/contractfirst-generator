package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.Configuration
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaTypeIdentifier
import io.github.ruedigerk.contractfirst.generator.java.model.*
import io.github.ruedigerk.contractfirst.generator.java.model.NumericValidationType.MAX
import io.github.ruedigerk.contractfirst.generator.java.model.NumericValidationType.MIN
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.model.*
import io.github.ruedigerk.contractfirst.generator.model.MPrimitiveType.*

/**
 * Transforms the parsed Schemas into Java types, assigning unique and valid type names. This needs to be done before creating Java source file models for these
 * types.
 */
class SchemaToJavaTypeTransformer(private val log: Log, private val configuration: Configuration) {

  private val modelPackage = "${configuration.outputJavaBasePackage}.model"
  private val schemaToTypeLookup: MutableMap<MSchemaNonRef, JavaAnyType> = mutableMapOf()
  private val uniqueNameFinder = UniqueNameFinder()

  /*
   * Using the schemaToTypeLookup is necessary for correctness, as calling toJavaType multiple times for the same schema would otherwise generate a new type 
   * name for generated types each time. 
   */
  fun toJavaType(schema: MSchemaNonRef): JavaAnyType {
    log.debug { "toJavaType ${schema.nameHint}" }

    return schemaToTypeLookup.getOrPut(schema) {
      when (schema) {
        is MSchemaObject -> toGeneratedJavaType(schema, false)
        is MSchemaEnum -> toGeneratedJavaType(schema, true)
        is MSchemaArray -> toJavaCollectionType(schema)
        is MSchemaMap -> toJavaMapType(schema)
        is MSchemaPrimitive -> toJavaBuiltInType(schema)
      }
    }
  }

  private fun toGeneratedJavaType(schema: MSchemaNonRef, isEnum: Boolean): JavaType {
    // Objects without properties seem to be used in the wild. Special-case to java.lang.Object.
    if (schema is MSchemaObject && schema.properties.isEmpty()) {
      return JavaType("Object", "java.lang")
    }

    val typeName = determineName(schema)
    val validations = if (isEnum) emptyList() else listOf(ValidatedValidation)

    return JavaType(typeName, modelPackage, validations)
  }

  private fun determineName(schema: MSchemaNonRef): String {
    val name = when (val parent = schema.embeddedIn) {
      is MSchemaObject -> toJavaType(parent).name + suggestName(schema, schema.nameHint.removePrefix(parent.nameHint))
      else -> configuration.outputJavaModelNamePrefix + suggestName(schema)
    }

    return uniqueNameFinder.toUniqueName(name)
  }

  private fun suggestName(schema: MSchemaNonRef, nameHint: NameHint = schema.nameHint) =
      nameHint.path.joinToString("/").toJavaTypeIdentifier()

  private fun toJavaCollectionType(schema: MSchemaArray): JavaCollectionType {
    val elementSchema = schema.itemSchema as? MSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $schema")
    val elementType = toJavaType(elementSchema)
    val typeName = if (schema.uniqueItems) "Set" else "List"
    val elementValidations = if (ValidatedValidation in elementType.validations) listOf(ValidatedValidation) else emptyList()

    return JavaCollectionType(typeName, "java.util", elementType, elementValidations + sizeValidations(schema.minItems, schema.maxItems))
  }

  private fun toJavaMapType(schema: MSchemaMap): JavaMapType {
    val valuesSchema = schema.valuesSchema as? MSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $schema")
    val valuesType = toJavaType(valuesSchema)
    val elementValidations = if (ValidatedValidation in valuesType.validations) listOf(ValidatedValidation) else emptyList()

    return JavaMapType("Map", "java.util", valuesType, elementValidations + sizeValidations(schema.minItems, schema.maxItems))
  }

  private fun toJavaBuiltInType(schema: MSchemaPrimitive): JavaType = when (schema.type) {

    BOOLEAN -> JavaType("Boolean", "java.lang")

    INTEGER -> {
      val validations = integralValidations(schema)
      when (schema.format) {
        "int32" -> JavaType("Integer", "java.lang", validations)
        "int64" -> JavaType("Long", "java.lang", validations)
        else -> JavaType("BigInteger", "java.math", validations)
      }
    }

    NUMBER -> {
      val validations = decimalValidations(schema)
      when (schema.format) {
        "float" -> JavaType("Float", "java.lang", validations)
        "double" -> JavaType("Double", "java.lang", validations)
        else -> JavaType("BigDecimal", "java.math", validations)
      }
    }

    STRING -> when (schema.format) {
      "date" -> JavaType("LocalDate", "java.time")
      "date-time" -> JavaType("OffsetDateTime", "java.time")
      "binary" -> JavaType("InputStream", "java.io")
      else -> JavaType("String", "java.lang", sizeValidations(schema.minLength, schema.maxLength) + patternValidations(schema))
    }
  }

  private fun integralValidations(schema: MSchemaPrimitive): List<TypeValidation> {
    val validations = mutableListOf<TypeValidation>()

    if (schema.minimum != null) {
      validations.add(IntegralValidation(MIN, schema.minimum.toLong()))
    }
    if (schema.maximum != null) {
      validations.add(IntegralValidation(MAX, schema.maximum.toLong()))
    }

    return validations.toList()
  }

  private fun decimalValidations(schema: MSchemaPrimitive): List<TypeValidation> {
    val validations = mutableListOf<TypeValidation>()

    if (schema.minimum != null) {
      validations.add(DecimalValidation(MIN, schema.minimum, !schema.exclusiveMinimum))
    }
    if (schema.maximum != null) {
      validations.add(DecimalValidation(MAX, schema.maximum, !schema.exclusiveMaximum))
    }

    return validations.toList()
  }

  private fun sizeValidations(minSize: Int?, maxSize: Int?): List<TypeValidation> = if (minSize != null || maxSize != null) {
    listOf(SizeValidation(minSize, maxSize))
  } else {
    emptyList()
  }

  private fun patternValidations(schema: MSchemaPrimitive): List<TypeValidation> = if (schema.pattern != null) {
    listOf(PatternValidation(schema.pattern))
  } else {
    emptyList()
  }
}