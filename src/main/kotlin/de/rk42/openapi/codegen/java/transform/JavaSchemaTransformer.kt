package de.rk42.openapi.codegen.java.transform

import de.rk42.openapi.codegen.CliConfiguration
import de.rk42.openapi.codegen.java.Identifiers.toJavaConstant
import de.rk42.openapi.codegen.java.Identifiers.toJavaIdentifier
import de.rk42.openapi.codegen.java.Identifiers.toJavaTypeIdentifier
import de.rk42.openapi.codegen.java.model.EnumConstant
import de.rk42.openapi.codegen.java.model.JavaBasicReference
import de.rk42.openapi.codegen.java.model.JavaClass
import de.rk42.openapi.codegen.java.model.JavaCollectionReference
import de.rk42.openapi.codegen.java.model.JavaEnum
import de.rk42.openapi.codegen.java.model.JavaMapReference
import de.rk42.openapi.codegen.java.model.JavaProperty
import de.rk42.openapi.codegen.java.model.JavaReference
import de.rk42.openapi.codegen.java.model.JavaType
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
 * Transforms the parsed schemas into Java-specific type representations, appropriate for code generation.
 */
class JavaSchemaTransformer(private val configuration: CliConfiguration, allSchemas: List<CtrSchemaNonRef>) {

  private val modelPackage = "${configuration.sourcePackage}.model"
  private val referencesLookup: Map<CtrSchemaNonRef, JavaReference> = allSchemas.associateWith { toJavaReference(it) }

  private var uniqueNameCounter: Int = 1
  
  val typesToGenerate: List<JavaType> = allSchemas.mapNotNull(::toJavaType)

  fun lookupReference(schema: CtrSchema): JavaReference {
    if (schema is CtrSchemaRef) {
      throw IllegalStateException("Specification must not contain any CtrSchemaRef instances, but was: $schema")
    }

    return referencesLookup[schema] ?: throw IllegalArgumentException("Schema not in referencesLookup: $schema")
  }

  private fun toJavaReference(schema: CtrSchemaNonRef): JavaReference = when (schema) {
    is CtrSchemaObject -> toJavaBasicReference(schema.referencedBy?.referencedName() ?: schema.title, true)
    is CtrSchemaEnum -> toJavaBasicReference(schema.referencedBy?.referencedName() ?: schema.title, false)
    is CtrSchemaArray -> toJavaCollectionReference(schema)
    is CtrSchemaMap -> toJavaMapReference(schema)
    is CtrSchemaPrimitive -> toJavaBuiltInReference(schema)
  }

  private fun toJavaBasicReference(name: String?, isRegularClass: Boolean): JavaReference {
    val typeName = name?.toJavaTypeIdentifier() ?: createUniqueTypeName()
    val finalTypeName = configuration.modelPrefix + typeName

    return JavaBasicReference(finalTypeName, modelPackage, isRegularClass)
  }

  private fun toJavaCollectionReference(schema: CtrSchemaArray): JavaCollectionReference {
    val elementSchema = schema.itemSchema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $schema")
    val elementReference = toJavaReference(elementSchema)
    return JavaCollectionReference("List", "java.util", elementReference)
  }

  private fun toJavaMapReference(schema: CtrSchemaMap): JavaMapReference {
    val valuesSchema = schema.valuesSchema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $schema")
    val valuesReference = toJavaReference(valuesSchema)
    return JavaMapReference("Map", "java.util", valuesReference)
  }

  /**
   * TODO: Support the following "string" formats:
   *  - "byte": base64 encoded characters
   *  - "binary": any sequence of octets
   */
  private fun toJavaBuiltInReference(schema: CtrSchemaPrimitive): JavaReference = when (schema.type) {
    BOOLEAN -> JavaBasicReference("Boolean", "java.lang", false)
    INTEGER -> when (schema.format) {
      "int32" -> JavaBasicReference("Integer", "java.lang", false)
      "int64" -> JavaBasicReference("Long", "java.lang", false)
      else -> JavaBasicReference("BigInteger", "java.math", false)
    }
    NUMBER -> when (schema.format) {
      "float" -> JavaBasicReference("Float", "java.lang", false)
      "double" -> JavaBasicReference("Double", "java.lang", false)
      else -> JavaBasicReference("BigDecimal", "java.math", false)
    }
    STRING -> when (schema.format) {
      "date" -> JavaBasicReference("LocalDate", "java.time", false)
      "date-time" -> JavaBasicReference("OffsetDateTime", "java.time", false)
      else -> JavaBasicReference("String", "java.lang", false)
    }
  }

  private fun createUniqueTypeName(): String = "Type$uniqueNameCounter"

  private fun toJavaType(schema: CtrSchemaNonRef): JavaType? = when (schema) {
    is CtrSchemaObject -> toJavaClass(schema)
    is CtrSchemaEnum -> toJavaEnum(schema)
    is CtrSchemaArray -> null
    is CtrSchemaMap -> null
    is CtrSchemaPrimitive -> null
  }

  private fun toJavaClass(schema: CtrSchemaObject): JavaClass {
    val reference = referencesLookup[schema] ?: throw IllegalArgumentException("Schema not in referencesLookup: $schema")
    val className = reference.typeName
    val properties = schema.properties.map(::toJavaProperty)
    return JavaClass(className, TransformerHelper.toJavadoc(schema), properties)
  }

  private fun toJavaProperty(property: CtrSchemaProperty): JavaProperty {
    val schema = property.schema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $property")
    return JavaProperty(
        property.name.toJavaIdentifier(), TransformerHelper.toJavadoc(schema), property.name, property.required,
        toJavaReference(schema)
    )
  }

  private fun toJavaEnum(schema: CtrSchemaEnum): JavaEnum {
    val className = referencesLookup[schema]?.typeName ?: throw IllegalArgumentException("Schema not in referencesLookup: $schema")
    val constants = schema.values.map { EnumConstant(it, it.toJavaConstant()) }
    return JavaEnum(className, TransformerHelper.toJavadoc(schema), constants)
  }
}