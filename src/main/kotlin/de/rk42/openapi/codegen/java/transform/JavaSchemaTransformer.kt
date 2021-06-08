package de.rk42.openapi.codegen.java.transform

import de.rk42.openapi.codegen.CliConfiguration
import de.rk42.openapi.codegen.java.Identifiers.toJavaConstant
import de.rk42.openapi.codegen.java.Identifiers.toJavaIdentifier
import de.rk42.openapi.codegen.java.Identifiers.toJavaTypeIdentifier
import de.rk42.openapi.codegen.java.model.DecimalValidation
import de.rk42.openapi.codegen.java.model.EnumConstant
import de.rk42.openapi.codegen.java.model.IntegralValidation
import de.rk42.openapi.codegen.java.model.JavaAnyType
import de.rk42.openapi.codegen.java.model.JavaClassFile
import de.rk42.openapi.codegen.java.model.JavaCollectionType
import de.rk42.openapi.codegen.java.model.JavaEnumFile
import de.rk42.openapi.codegen.java.model.JavaMapType
import de.rk42.openapi.codegen.java.model.JavaProperty
import de.rk42.openapi.codegen.java.model.JavaSourceFile
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
import de.rk42.openapi.codegen.model.CtrSchema
import de.rk42.openapi.codegen.model.CtrSchemaArray
import de.rk42.openapi.codegen.model.CtrSchemaEnum
import de.rk42.openapi.codegen.model.CtrSchemaMap
import de.rk42.openapi.codegen.model.CtrSchemaNonRef
import de.rk42.openapi.codegen.model.CtrSchemaObject
import de.rk42.openapi.codegen.model.CtrSchemaPrimitive
import de.rk42.openapi.codegen.model.CtrSchemaProperty
import de.rk42.openapi.codegen.model.CtrSchemaRef

/**
 * Transforms the parsed schemas into Java source file representations, appropriate for code generation.
 */
class JavaSchemaTransformer(
    configuration: CliConfiguration,
    private val allSchemas: List<CtrSchemaNonRef>
) {

  private val referenceTransformer = JavaSchemaToTypeTransformer(configuration, allSchemas)

  fun transformJavaModelFiles(): List<JavaSourceFile> = allSchemas.mapNotNull(::toJavaSourceFile)

  fun lookupJavaTypeFor(schema: CtrSchema): JavaAnyType = referenceTransformer.lookupJavaTypeFor(schema)

  private fun toJavaSourceFile(schema: CtrSchemaNonRef): JavaSourceFile? = when (schema) {
    is CtrSchemaObject -> toJavaClassFile(schema)
    is CtrSchemaEnum -> toJavaEnumFile(schema)
    is CtrSchemaArray -> null
    is CtrSchemaMap -> null
    is CtrSchemaPrimitive -> null
  }

  private fun toJavaClassFile(schema: CtrSchemaObject): JavaClassFile {
    val type = referenceTransformer.lookupJavaTypeFor(schema)
    val className = type.name
    val properties = schema.properties.map(::toJavaProperty)

    return JavaClassFile(className, TransformerHelper.toJavadoc(schema), properties)
  }

  private fun toJavaProperty(property: CtrSchemaProperty): JavaProperty {
    val schema = property.schema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $property")

    return JavaProperty(
        property.name.toJavaIdentifier(),
        TransformerHelper.toJavadoc(schema),
        property.name,
        property.required,
        referenceTransformer.lookupJavaTypeFor(schema)
    )
  }

  private fun toJavaEnumFile(schema: CtrSchemaEnum): JavaEnumFile {
    val type = referenceTransformer.lookupJavaTypeFor(schema)
    val constants = schema.values.map { EnumConstant(it.toJavaConstant(), it) }

    return JavaEnumFile(type.name, TransformerHelper.toJavadoc(schema), constants)
  }
}

/**
 * Transforms the parsed Schemas into Java types, assigning unique and valid type names. This needs to be done before creating Java source file models for these
 * types.
 */
private class JavaSchemaToTypeTransformer(private val configuration: CliConfiguration, allSchemas: List<CtrSchemaNonRef>) {

  private val modelPackage = "${configuration.sourcePackage}.model"
  private val referencesLookup: Map<CtrSchemaNonRef, JavaAnyType> = allSchemas.associateWith(::toJavaType)

  private var uniqueNameCounter: Int = 1

  fun lookupJavaTypeFor(schema: CtrSchema): JavaAnyType {
    if (schema is CtrSchemaRef) {
      throw IllegalStateException("Specification must not contain any CtrSchemaRef instances, but was: $schema")
    }

    return referencesLookup[schema] ?: throw IllegalArgumentException("Schema not in referencesLookup: $schema")
  }

  private fun toJavaType(schema: CtrSchemaNonRef): JavaAnyType = when (schema) {
    is CtrSchemaObject -> toJavaSimpleType(schema.referencedBy?.referencedName() ?: schema.title, true)
    is CtrSchemaEnum -> toJavaSimpleType(schema.referencedBy?.referencedName() ?: schema.title, false)
    is CtrSchemaArray -> toJavaCollectionType(schema)
    is CtrSchemaMap -> toJavaMapType(schema)
    is CtrSchemaPrimitive -> toJavaBuiltInType(schema)
  }

  private fun toJavaSimpleType(name: String?, isRegularClass: Boolean): JavaType {
    val typeName = name?.toJavaTypeIdentifier() ?: createUniqueTypeName()
    val finalTypeName = configuration.modelPrefix + typeName
    val validations = if (isRegularClass) listOf(ValidatedValidation) else emptyList()

    return JavaType(finalTypeName, modelPackage, validations)
  }

  private fun createUniqueTypeName(): String = "Type$uniqueNameCounter".also { uniqueNameCounter++ }
  
  private fun toJavaCollectionType(schema: CtrSchemaArray): JavaCollectionType {
    val elementSchema = schema.itemSchema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $schema")
    val elementType = toJavaType(elementSchema)
    val typeName = if (schema.uniqueItems) "Set" else "List"
    
    return JavaCollectionType(typeName, "java.util", elementType, sizeValidations(schema.minItems, schema.maxItems))
  }

  private fun toJavaMapType(schema: CtrSchemaMap): JavaMapType {
    val valuesSchema = schema.valuesSchema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $schema")
    val valuesType = toJavaType(valuesSchema)
    return JavaMapType("Map", "java.util", valuesType, sizeValidations(schema.minItems, schema.maxItems))
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
