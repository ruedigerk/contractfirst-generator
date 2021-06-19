package de.rk42.openapi.codegen.java.transform

import de.rk42.openapi.codegen.CliConfiguration
import de.rk42.openapi.codegen.java.Identifiers.toJavaTypeIdentifier
import de.rk42.openapi.codegen.java.model.DecimalValidation
import de.rk42.openapi.codegen.java.model.IntegralValidation
import de.rk42.openapi.codegen.java.model.JavaAnyType
import de.rk42.openapi.codegen.java.model.JavaCollectionType
import de.rk42.openapi.codegen.java.model.JavaMapType
import de.rk42.openapi.codegen.java.model.JavaType
import de.rk42.openapi.codegen.java.model.NumericValidationType.MAX
import de.rk42.openapi.codegen.java.model.NumericValidationType.MIN
import de.rk42.openapi.codegen.java.model.PatternValidation
import de.rk42.openapi.codegen.java.model.SizeValidation
import de.rk42.openapi.codegen.java.model.TypeValidation
import de.rk42.openapi.codegen.java.model.ValidatedValidation
import de.rk42.openapi.codegen.model.CtrPrimitiveType.BOOLEAN
import de.rk42.openapi.codegen.model.CtrPrimitiveType.INTEGER
import de.rk42.openapi.codegen.model.CtrPrimitiveType.NUMBER
import de.rk42.openapi.codegen.model.CtrPrimitiveType.STRING
import de.rk42.openapi.codegen.model.CtrSchemaArray
import de.rk42.openapi.codegen.model.CtrSchemaEnum
import de.rk42.openapi.codegen.model.CtrSchemaMap
import de.rk42.openapi.codegen.model.CtrSchemaNonRef
import de.rk42.openapi.codegen.model.CtrSchemaObject
import de.rk42.openapi.codegen.model.CtrSchemaPrimitive
import de.rk42.openapi.codegen.model.NameHint

/**
 * Transforms the parsed Schemas into Java types, assigning unique and valid type names. This needs to be done before creating Java source file models for these
 * types.
 */
class SchemaToJavaTypeTransformer(private val configuration: CliConfiguration) {

  private val modelPackage = "${configuration.sourcePackage}.model"
  private val schemaToTypeLookup: MutableMap<CtrSchemaNonRef, JavaAnyType> = mutableMapOf()
  private val uniqueNameFinder = UniqueNameFinder()

  /*
   * Using the schemaToTypeLookup is necessary for correctness, as calling toJavaType multiple times for the same schema would otherwise generate a new type 
   * name for generated types each time. 
   */
  fun toJavaType(schema: CtrSchemaNonRef): JavaAnyType =
      schemaToTypeLookup.getOrPut(schema) {
        when (schema) {
          is CtrSchemaObject    -> toGeneratedJavaType(schema, false)
          is CtrSchemaEnum      -> toGeneratedJavaType(schema, true)
          is CtrSchemaArray     -> toJavaCollectionType(schema)
          is CtrSchemaMap       -> toJavaMapType(schema)
          is CtrSchemaPrimitive -> toJavaBuiltInType(schema)
        }
      }

  private fun toGeneratedJavaType(schema: CtrSchemaNonRef, isEnum: Boolean): JavaType {
    val typeName = determineName(schema)
    val validations = if (isEnum) emptyList() else listOf(ValidatedValidation)

    return JavaType(typeName, modelPackage, validations)
  }

  private fun determineName(schema: CtrSchemaNonRef): String {
    val parentSchema = schema.embeddedIn
    
    val name = if (parentSchema == null) {
      configuration.modelPrefix + suggestName(schema)
    } else {
      val parentType = toJavaType(parentSchema)
      parentType.name + suggestName(schema, schema.nameHint.removePrefix(parentSchema.nameHint))
    }
    
    return uniqueNameFinder.toUniqueName(name)
  }

  private fun suggestName(schema: CtrSchemaNonRef, nameHint: NameHint = schema.nameHint) = 
      (schema.title ?: nameHint.path.joinToString("/")).toJavaTypeIdentifier()

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

  /**
   * TODO: Support the following "string" formats:
   *  - "byte": base64 encoded characters
   *  - "binary": any sequence of octets
   */
  private fun toJavaBuiltInType(schema: CtrSchemaPrimitive): JavaType = when (schema.type) {

    BOOLEAN -> JavaType("Boolean", "java.lang")

    INTEGER -> {
      val validations = integralValidations(schema)

      when (schema.format) {
        "int32" -> JavaType("Integer", "java.lang", validations)
        "int64" -> JavaType("Long", "java.lang", validations)
        else    -> JavaType("BigInteger", "java.math", validations)
      }
    }

    NUMBER  -> {
      val validations = decimalValidations(schema)

      when (schema.format) {
        "float"  -> JavaType("Float", "java.lang", validations)
        "double" -> JavaType("Double", "java.lang", validations)
        else     -> JavaType("BigDecimal", "java.math", validations)
      }
    }

    STRING  -> when (schema.format) {
      "date"      -> JavaType("LocalDate", "java.time")
      "date-time" -> JavaType("OffsetDateTime", "java.time")
      else        -> JavaType("String", "java.lang", sizeValidations(schema.minLength, schema.maxLength) + patternValidations(schema))
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