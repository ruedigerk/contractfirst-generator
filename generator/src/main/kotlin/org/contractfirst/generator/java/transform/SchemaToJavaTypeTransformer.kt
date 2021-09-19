package org.contractfirst.generator.java.transform

import org.contractfirst.generator.Configuration
import org.contractfirst.generator.java.Identifiers.toJavaTypeIdentifier
import org.contractfirst.generator.java.model.*
import org.contractfirst.generator.java.model.NumericValidationType.MAX
import org.contractfirst.generator.java.model.NumericValidationType.MIN
import org.contractfirst.generator.logging.Log
import org.contractfirst.generator.model.*
import org.contractfirst.generator.model.CtrPrimitiveType.*

/**
 * Transforms the parsed Schemas into Java types, assigning unique and valid type names. This needs to be done before creating Java source file models for these
 * types.
 */
class SchemaToJavaTypeTransformer(private val log: Log, private val configuration: Configuration) {

  private val modelPackage = "${configuration.outputJavaBasePackage}.model"
  private val schemaToTypeLookup: MutableMap<CtrSchemaNonRef, JavaAnyType> = mutableMapOf()
  private val uniqueNameFinder = UniqueNameFinder()

  /*
   * Using the schemaToTypeLookup is necessary for correctness, as calling toJavaType multiple times for the same schema would otherwise generate a new type 
   * name for generated types each time. 
   */
  fun toJavaType(schema: CtrSchemaNonRef): JavaAnyType {
    log.debug { "toJavaType ${schema.nameHint}" }

    return schemaToTypeLookup.getOrPut(schema) {
      when (schema) {
        is CtrSchemaObject -> toGeneratedJavaType(schema, false)
        is CtrSchemaEnum -> toGeneratedJavaType(schema, true)
        is CtrSchemaArray -> toJavaCollectionType(schema)
        is CtrSchemaMap -> toJavaMapType(schema)
        is CtrSchemaPrimitive -> toJavaBuiltInType(schema)
      }
    }
  }

  private fun toGeneratedJavaType(schema: CtrSchemaNonRef, isEnum: Boolean): JavaType {
    // Objects without properties seem to be used in the wild. Special-case to java.lang.Object.
    if (schema is CtrSchemaObject && schema.properties.isEmpty()) {
      return JavaType("Object", "java.lang")
    }

    val typeName = determineName(schema)
    val validations = if (isEnum) emptyList() else listOf(ValidatedValidation)

    return JavaType(typeName, modelPackage, validations)
  }

  private fun determineName(schema: CtrSchemaNonRef): String {
    val name = when (val parent = schema.embeddedIn) {
      is CtrSchemaObject -> toJavaType(parent).name + suggestName(schema, schema.nameHint.removePrefix(parent.nameHint))
      else -> configuration.outputJavaNamePrefix + suggestName(schema)
    }

    return uniqueNameFinder.toUniqueName(name)
  }

  private fun suggestName(schema: CtrSchemaNonRef, nameHint: NameHint = schema.nameHint) =
      nameHint.path.joinToString("/").toJavaTypeIdentifier()

  private fun toJavaCollectionType(schema: CtrSchemaArray): JavaCollectionType {
    val elementSchema = schema.itemSchema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $schema")
    val elementType = toJavaType(elementSchema)
    val typeName = if (schema.uniqueItems) "Set" else "List"
    val elementValidations = if (ValidatedValidation in elementType.validations) listOf(ValidatedValidation) else emptyList()

    return JavaCollectionType(typeName, "java.util", elementType, elementValidations + sizeValidations(schema.minItems, schema.maxItems))
  }

  private fun toJavaMapType(schema: CtrSchemaMap): JavaMapType {
    val valuesSchema = schema.valuesSchema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $schema")
    val valuesType = toJavaType(valuesSchema)
    val elementValidations = if (ValidatedValidation in valuesType.validations) listOf(ValidatedValidation) else emptyList()

    return JavaMapType("Map", "java.util", valuesType, elementValidations + sizeValidations(schema.minItems, schema.maxItems))
  }

  private fun toJavaBuiltInType(schema: CtrSchemaPrimitive): JavaType = when (schema.type) {

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

  private fun integralValidations(schema: CtrSchemaPrimitive): List<TypeValidation> {
    val validations = mutableListOf<TypeValidation>()

    if (schema.minimum != null) {
      validations.add(IntegralValidation(MIN, schema.minimum.toLong()))
    }
    if (schema.maximum != null) {
      validations.add(IntegralValidation(MAX, schema.maximum.toLong()))
    }

    return validations.toList()
  }

  private fun decimalValidations(schema: CtrSchemaPrimitive): List<TypeValidation> {
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

  private fun patternValidations(schema: CtrSchemaPrimitive): List<TypeValidation> = if (schema.pattern != null) {
    listOf(PatternValidation(schema.pattern))
  } else {
    emptyList()
  }
}