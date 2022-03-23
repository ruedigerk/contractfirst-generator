package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.Configuration
import io.github.ruedigerk.contractfirst.generator.java.Identifiers.toJavaTypeIdentifier
import io.github.ruedigerk.contractfirst.generator.java.model.*
import io.github.ruedigerk.contractfirst.generator.java.model.NumericValidationType.MAX
import io.github.ruedigerk.contractfirst.generator.java.model.NumericValidationType.MIN
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.model.*
import io.github.ruedigerk.contractfirst.generator.model.PrimitiveType.*

/**
 * Transforms the parsed Schemas into Java types, assigning unique and valid type names. This needs to be done before creating Java source file models for these
 * types.
 */
class SchemaToJavaTypeTransformer(
    private val log: Log,
    private val configuration: Configuration,
    private val schemaRefLookup: SchemaRefLookup
) {

  private val modelPackage = "${configuration.outputJavaBasePackage}.model"
  private val schemaToTypeLookup: MutableMap<ActualSchema, JavaAnyType> = mutableMapOf()
  private val uniqueNameFinder = UniqueNameFinder()

  /*
   * Using the schemaToTypeLookup is necessary for correctness, as calling toJavaType multiple times for the same schema would otherwise generate a new type 
   * name for generated types each time. 
   */
  fun toJavaType(schema: ActualSchema): JavaAnyType {
    log.debug { "toJavaType ${schema.nameHint}" }

    return schemaToTypeLookup.getOrPut(schema) {
      when (schema) {
        is ObjectSchema -> toGeneratedJavaType(schema, false)
        is EnumSchema -> toGeneratedJavaType(schema, true)
        is ArraySchema -> toJavaCollectionType(schema)
        is MapSchema -> toJavaMapType(schema)
        is PrimitiveSchema -> toJavaBuiltInType(schema)
      }
    }
  }

  private fun toGeneratedJavaType(schema: ActualSchema, isEnum: Boolean): JavaType {
    // Objects without properties seem to be used in the wild. Special-case to java.lang.Object.
    if (schema is ObjectSchema && schema.properties.isEmpty()) {
      return JavaType("Object", "java.lang")
    }

    val typeName = determineName(schema)
    val validations = if (isEnum) emptyList() else listOf(ValidatedValidation)

    return JavaType(typeName, modelPackage, validations)
  }

  private fun determineName(schema: ActualSchema): String {
    val name = when (val parent = schema.embeddedIn) {
      is ObjectSchema -> toJavaType(parent).name + suggestName(schema, schema.nameHint.removePrefix(parent.nameHint))
      else -> configuration.outputJavaModelNamePrefix + suggestName(schema)
    }

    return uniqueNameFinder.toUniqueName(name)
  }

  private fun suggestName(schema: ActualSchema, nameHint: NameHint = schema.nameHint) =
      nameHint.path.joinToString("/").toJavaTypeIdentifier()

  private fun toJavaCollectionType(schema: ArraySchema): JavaCollectionType {
    val elementType = toJavaType(schemaRefLookup.lookupIfRef(schema.itemSchema))
    val typeName = if (schema.uniqueItems) "Set" else "List"
    val elementValidations = if (ValidatedValidation in elementType.validations) listOf(ValidatedValidation) else emptyList()

    return JavaCollectionType(typeName, "java.util", elementType, elementValidations + sizeValidations(schema.minItems, schema.maxItems))
  }

  private fun toJavaMapType(schema: MapSchema): JavaMapType {
    val valuesType = toJavaType(schemaRefLookup.lookupIfRef(schema.valuesSchema))
    val elementValidations = if (ValidatedValidation in valuesType.validations) listOf(ValidatedValidation) else emptyList()

    return JavaMapType("Map", "java.util", valuesType, elementValidations + sizeValidations(schema.minItems, schema.maxItems))
  }

  private fun toJavaBuiltInType(schema: PrimitiveSchema): JavaType = when (schema.type) {

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

  private fun integralValidations(schema: PrimitiveSchema): List<TypeValidation> {
    val validations = mutableListOf<TypeValidation>()

    if (schema.minimum != null) {
      validations.add(IntegralValidation(MIN, schema.minimum.toBigInteger()))
    }
    if (schema.maximum != null) {
      validations.add(IntegralValidation(MAX, schema.maximum.toBigInteger()))
    }

    return validations.toList()
  }

  private fun decimalValidations(schema: PrimitiveSchema): List<TypeValidation> {
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

  private fun patternValidations(schema: PrimitiveSchema): List<TypeValidation> = if (schema.pattern != null) {
    listOf(PatternValidation(schema.pattern))
  } else {
    emptyList()
  }
}