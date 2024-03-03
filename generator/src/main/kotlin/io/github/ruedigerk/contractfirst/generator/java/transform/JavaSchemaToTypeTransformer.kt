package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import io.github.ruedigerk.contractfirst.generator.java.model.*
import io.github.ruedigerk.contractfirst.generator.java.model.NumericValidationType.MAX
import io.github.ruedigerk.contractfirst.generator.java.model.NumericValidationType.MIN
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.model.*
import io.github.ruedigerk.contractfirst.generator.model.PrimitiveType.*

/**
 * Transforms the parsed Schemas into Java types, creating unique and valid type names for generated types. This needs to be done before creating Java source
 * file models for these types.
 */
class JavaSchemaToTypeTransformer(
    private val log: Log,
    private val schemas: Map<SchemaId, Schema>,
    configuration: JavaConfiguration,
    operationMethodNames: Map<Operation.PathAndMethod, String>
) {

  private val nameGenerator = JavaTypeNameGenerator(log, configuration, operationMethodNames)
  private val typeNameUniquifier = TypeNameUniquifier()
  private val types = mutableMapOf<SchemaId, JavaAnyType>()

  fun transform(): Map<SchemaId, JavaAnyType> = schemas.mapValues { (id, _) -> toJavaType(id) }

  private fun toJavaType(schemaId: SchemaId): JavaAnyType {
    log.debug { "toJavaType ${schemaId.position}" }

    return types.getOrPut(schemaId) {
      when (val schema = schemas[schemaId]!!) {
        is ObjectSchema -> toGeneratedJavaType(schema, false)
        is EnumSchema -> toGeneratedJavaType(schema, true)
        is ArraySchema -> toJavaCollectionType(schema)
        is MapSchema -> toJavaMapType(schema)
        is PrimitiveSchema -> toJavaBuiltInType(schema)
      }
    }
  }

  private fun toGeneratedJavaType(schema: Schema, isEnum: Boolean): JavaType {
    // Objects without properties seem to be used in the wild. Special-case to java.lang.Object.
    if (schema is ObjectSchema && schema.properties.isEmpty()) {
      return JavaType(JavaTypeName.OBJECT)
    }

    val typeName = generateName(schema)
    val validations = if (isEnum) emptyList() else listOf(ValidatedValidation)

    return JavaType(typeName, validations)
  }

  private fun generateName(schema: Schema): JavaTypeName {
    val name = nameGenerator.determineName(schema.position)
    return typeNameUniquifier.toUniqueName(name)
  }

  private fun toJavaCollectionType(schema: ArraySchema): JavaCollectionType {
    val elementType = toJavaType(schema.itemSchema)
    val typeName = if (schema.uniqueItems) JavaTypeName.SET else JavaTypeName.LIST
    val elementValidations = if (ValidatedValidation in elementType.validations) listOf(ValidatedValidation) else emptyList()

    return JavaCollectionType(typeName, elementType, elementValidations + sizeValidations(schema.minItems, schema.maxItems))
  }

  private fun toJavaMapType(schema: MapSchema): JavaMapType {
    val valuesType = toJavaType(schema.valuesSchema)
    val elementValidations = if (ValidatedValidation in valuesType.validations) listOf(ValidatedValidation) else emptyList()

    return JavaMapType(valuesType, elementValidations + sizeValidations(schema.minItems, schema.maxItems))
  }

  private fun toJavaBuiltInType(schema: PrimitiveSchema): JavaType = when (schema.type) {

    BOOLEAN -> JavaType(JavaTypeName.BOOLEAN)

    INTEGER -> {
      val validations = integralValidations(schema)
      when (schema.format) {
        "int32" -> JavaType(JavaTypeName.INTEGER, validations)
        "int64" -> JavaType(JavaTypeName.LONG, validations)
        else -> JavaType(JavaTypeName.BIG_INTEGER, validations)
      }
    }

    NUMBER -> {
      val validations = decimalValidations(schema)
      when (schema.format) {
        "float" -> JavaType(JavaTypeName.FLOAT, validations)
        "double" -> JavaType(JavaTypeName.DOUBLE, validations)
        else -> JavaType(JavaTypeName.BIG_DECIMAL, validations)
      }
    }

    STRING -> when (schema.format) {
      "date" -> JavaType(JavaTypeName.LOCAL_DATE)
      "date-time" -> JavaType(JavaTypeName.OFFSET_DATE_TIME)
      "binary" -> JavaType(JavaTypeName.INPUT_STREAM)
      else -> JavaType(JavaTypeName.STRING, sizeValidations(schema.minLength, schema.maxLength) + patternValidations(schema))
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