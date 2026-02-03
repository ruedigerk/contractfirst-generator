package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import io.github.ruedigerk.contractfirst.generator.java.model.DecimalValidation
import io.github.ruedigerk.contractfirst.generator.java.model.IntegralValidation
import io.github.ruedigerk.contractfirst.generator.java.model.JavaAnyType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaCollectionType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaMapType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaType
import io.github.ruedigerk.contractfirst.generator.java.model.JavaTypeFlags.Generated
import io.github.ruedigerk.contractfirst.generator.java.model.JavaTypeName
import io.github.ruedigerk.contractfirst.generator.java.model.NumericValidationType.MAX
import io.github.ruedigerk.contractfirst.generator.java.model.NumericValidationType.MIN
import io.github.ruedigerk.contractfirst.generator.java.model.PatternValidation
import io.github.ruedigerk.contractfirst.generator.java.model.SizeValidation
import io.github.ruedigerk.contractfirst.generator.java.model.TypeValidation
import io.github.ruedigerk.contractfirst.generator.java.model.ValidatedValidation
import io.github.ruedigerk.contractfirst.generator.logging.Log
import io.github.ruedigerk.contractfirst.generator.openapi.ArraySchema
import io.github.ruedigerk.contractfirst.generator.openapi.DataType
import io.github.ruedigerk.contractfirst.generator.openapi.EnumSchema
import io.github.ruedigerk.contractfirst.generator.openapi.MapSchema
import io.github.ruedigerk.contractfirst.generator.openapi.ObjectSchema
import io.github.ruedigerk.contractfirst.generator.openapi.Operation
import io.github.ruedigerk.contractfirst.generator.openapi.PrimitiveSchema
import io.github.ruedigerk.contractfirst.generator.openapi.Schema
import io.github.ruedigerk.contractfirst.generator.openapi.SchemaId

/**
 * Transforms the parsed Schemas into Java types, creating unique and valid type names for generated types. This needs to be done before creating Java
 * source file models for these types.
 */
class JavaSchemaToTypeTransformer(
  private val log: Log,
  private val schemas: Map<SchemaId, Schema>,
  configuration: JavaConfiguration,
  operationMethodNames: Map<Operation.PathAndMethod, String>,
) {

  private val nameGenerator = JavaTypeNameGenerator(log, configuration, operationMethodNames)
  private val typeNameUniquifier = TypeNameUniquifier()
  private val types = mutableMapOf<SchemaId, JavaAnyType>()

  fun transform(): Map<SchemaId, JavaAnyType> = schemas.mapValues { (id, _) -> toJavaType(id) }

  private fun toJavaType(schemaId: SchemaId): JavaAnyType {
    log.debug { "toJavaType ${schemaId.position}" }

    return types.getOrPut(schemaId) {
      when (val schema = schemaFor(schemaId)) {
        is ObjectSchema -> toGeneratedJavaType(schema, false)
        is EnumSchema -> toGeneratedJavaType(schema, true)
        is ArraySchema -> toJavaCollectionType(schema)
        is MapSchema -> toJavaMapType(schema)
        is PrimitiveSchema -> toJavaPredefinedType(schema)
      }
    }
  }

  private fun toGeneratedJavaType(schema: Schema, isEnum: Boolean): JavaType {
    // Objects without properties seem to be used in the wild. Special-case to java.lang.Object.
    if (schema is ObjectSchema && schema.properties.isEmpty()) {
      // Although the Object type from the standard library is used, it is treated as generated, because it would be if any properties had been specified.
      return JavaType(JavaTypeName.OBJECT, flags = Generated)
    }

    val typeName = generateName(schema)
    val validations = if (isEnum) emptyList() else listOf(ValidatedValidation)

    return JavaType(typeName, validations, Generated)
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

  private fun toJavaPredefinedType(schema: PrimitiveSchema): JavaType = when (schema.dataType) {
    DataType.STRING -> JavaType(JavaTypeName.STRING, sizeValidations(schema.minLength, schema.maxLength) + patternValidations(schema))
    DataType.BINARY -> JavaType(JavaTypeName.INPUT_STREAM)
    DataType.BOOLEAN -> JavaType(JavaTypeName.BOOLEAN)
    DataType.INT_32 -> JavaType(JavaTypeName.INTEGER, integralValidations(schema))
    DataType.INT_64 -> JavaType(JavaTypeName.LONG, integralValidations(schema))
    DataType.INTEGER -> JavaType(JavaTypeName.BIG_INTEGER, integralValidations(schema))
    DataType.FLOAT -> JavaType(JavaTypeName.FLOAT, decimalValidations(schema))
    DataType.DOUBLE -> JavaType(JavaTypeName.DOUBLE, decimalValidations(schema))
    DataType.NUMBER -> JavaType(JavaTypeName.BIG_DECIMAL, decimalValidations(schema))
    DataType.DATE -> JavaType(JavaTypeName.LOCAL_DATE)
    DataType.DATE_TIME -> JavaType(JavaTypeName.OFFSET_DATE_TIME)
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

  private fun schemaFor(schemaId: SchemaId): Schema = schemas[schemaId] ?: error("Unknown schema ID: $schemaId")
}
