package de.rk42.openapi.codegen

import de.rk42.openapi.codegen.Names.toJavaConstant
import de.rk42.openapi.codegen.Names.toJavaIdentifier
import de.rk42.openapi.codegen.Names.toJavaTypeIdentifier
import de.rk42.openapi.codegen.model.contract.CtrOperation
import de.rk42.openapi.codegen.model.contract.CtrParameter
import de.rk42.openapi.codegen.model.contract.CtrPrimitiveType.BOOLEAN
import de.rk42.openapi.codegen.model.contract.CtrPrimitiveType.INTEGER
import de.rk42.openapi.codegen.model.contract.CtrPrimitiveType.NUMBER
import de.rk42.openapi.codegen.model.contract.CtrPrimitiveType.STRING
import de.rk42.openapi.codegen.model.contract.CtrResponse
import de.rk42.openapi.codegen.model.contract.CtrResponseContent
import de.rk42.openapi.codegen.model.contract.CtrSchema
import de.rk42.openapi.codegen.model.contract.CtrSchemaArray
import de.rk42.openapi.codegen.model.contract.CtrSchemaEnum
import de.rk42.openapi.codegen.model.contract.CtrSchemaNonRef
import de.rk42.openapi.codegen.model.contract.CtrSchemaObject
import de.rk42.openapi.codegen.model.contract.CtrSchemaPrimitive
import de.rk42.openapi.codegen.model.contract.CtrSchemaProperty
import de.rk42.openapi.codegen.model.contract.CtrSpecification
import de.rk42.openapi.codegen.model.java.EnumConstant
import de.rk42.openapi.codegen.model.java.JavaClass
import de.rk42.openapi.codegen.model.java.JavaEnum
import de.rk42.openapi.codegen.model.java.JavaOperation
import de.rk42.openapi.codegen.model.java.JavaOperationGroup
import de.rk42.openapi.codegen.model.java.JavaParameter
import de.rk42.openapi.codegen.model.java.JavaProperty
import de.rk42.openapi.codegen.model.java.JavaReference
import de.rk42.openapi.codegen.model.java.JavaResponse
import de.rk42.openapi.codegen.model.java.JavaResponseContent
import de.rk42.openapi.codegen.model.java.JavaSpecification
import de.rk42.openapi.codegen.model.java.JavaType

/**
 * Transforms the model into a Java-specific model for code generation.
 */
class JavaTransformer(configuration: CliConfiguration) {

  private val schemaTransformer = JavaSchemaTransformer(configuration)

  fun transform(specification: CtrSpecification): JavaSpecification {
    val javaTypes = schemaTransformer.parseSchemas(specification.schemas)

    return JavaSpecification(
        groupOperations(specification.operations),
        javaTypes
    )
  }

  private fun groupOperations(operations: List<CtrOperation>): List<JavaOperationGroup> = operations
      .groupBy { it.tags.firstOrNull() ?: DEFAULT_GROUP_NAME }
      .mapKeys { it.key.toJavaTypeIdentifier() + GROUP_NAME_SUFFIX }
      .mapValues { it.value.map(::toJavaOperation) }
      .map { (groupJavaIdentifier, operations) -> JavaOperationGroup(groupJavaIdentifier, operations) }

  private fun toJavaOperation(operation: CtrOperation): JavaOperation {
    return JavaOperation(
        operation.operationId.toJavaIdentifier(),
        operation.path,
        operation.method,
        operation.tags,
        operation.summary,
        operation.description,
        operation.parameters.map(::toJavaParameter),
        operation.responses.map(::toJavaResponse)
    )
  }

  private fun toJavaParameter(parameter: CtrParameter): JavaParameter = JavaParameter(
      parameter.name.toJavaIdentifier(),
      parameter.name,
      parameter.location,
      parameter.description,
      parameter.required,
      toJavaReference(parameter.schema)
  )

  private fun toJavaResponse(response: CtrResponse): JavaResponse = JavaResponse(
      response.statusCode,
      response.content.map(::toJavaResponseContent)
  )

  private fun toJavaResponseContent(responseContent: CtrResponseContent): JavaResponseContent = JavaResponseContent(
      responseContent.mediaType,
      toJavaReference(responseContent.schema)
  )

  private fun toJavaReference(schema: CtrSchema): JavaReference = schemaTransformer.lookupReference(schema)

  companion object {

    private const val GROUP_NAME_SUFFIX = "Api"
    private const val DEFAULT_GROUP_NAME = "Default"
  }
}

private class JavaSchemaTransformer(configuration: CliConfiguration) {

  private val modelPackage = "${configuration.sourcePackage}.model"
  private val referencesLookup: MutableMap<CtrSchema, JavaReference> = mutableMapOf()

  private var uniqueNameCounter: Int = 1

  fun parseSchemas(schemas: List<CtrSchemaNonRef>): List<JavaType> {
    referencesLookup.putAll(createReferencesToSchemaMap(schemas))
    return schemas.mapNotNull(::toJavaType)
  }

  fun lookupReference(schema: CtrSchema): JavaReference =
      referencesLookup[schema] ?: throw IllegalArgumentException("Schema not in referencesLookup: $schema")

  private fun createReferencesToSchemaMap(schemas: List<CtrSchemaNonRef>): Map<out CtrSchema, JavaReference> =
      schemas.associateWith { toReference(it) }

  private fun toReference(schema: CtrSchemaNonRef): JavaReference = when (schema) {
    is CtrSchemaObject -> toJavaReference(schema.reference?.referencedName() ?: schema.title, true)
    is CtrSchemaEnum -> toJavaReference(schema.reference?.referencedName() ?: schema.title, false)
    is CtrSchemaArray -> toJavaCollectionReference(schema)
    is CtrSchemaPrimitive -> toJavaBuiltInReference(schema)
  }

  private fun toJavaReference(name: String, isClass: Boolean): JavaReference {
    val typeName = if (name.isEmpty()) {
      createUniqueTypeName()
    } else {
      name.toJavaTypeIdentifier()
    }

    return JavaReference(typeName, modelPackage, isClass)
  }

  private fun toJavaCollectionReference(schema: CtrSchemaArray): JavaReference {
    val itemSchema = schema.itemSchema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $schema")
    val itemReference = toReference(itemSchema)
    return JavaReference("List", "java.util", false, itemReference)
  }

  /**
   * TODO: Support the following string formats:
   *  - "byte": base64 encoded characters
   *  - "binary": any sequence of octets
   */
  private fun toJavaBuiltInReference(schema: CtrSchemaPrimitive): JavaReference = when (schema.type) {
    BOOLEAN -> JavaReference("Boolean", "java.lang", false)
    INTEGER -> when (schema.format) {
      "int32" -> JavaReference("Integer", "java.lang", false)
      "int64" -> JavaReference("Long", "java.lang", false)
      else -> JavaReference("BigInteger", "java.math", false)
    }
    NUMBER -> when (schema.format) {
      "float" -> JavaReference("Float", "java.lang", false)
      "double" -> JavaReference("Double", "java.lang", false)
      else -> JavaReference("BigDecimal", "java.math", false)
    }
    STRING -> when (schema.format) {
      "date" -> JavaReference("LocalDate", "java.time", false)
      "date-time" -> JavaReference("OffsetDateTime", "java.time", false)
      else -> JavaReference("String", "java.lang", false)
    }
  }

  private fun createUniqueTypeName(): String = "Type$uniqueNameCounter"

  private fun toJavaType(schema: CtrSchemaNonRef): JavaType? = when (schema) {
    is CtrSchemaObject -> toJavaClass(schema)
    is CtrSchemaEnum -> toJavaEnum(schema)
    is CtrSchemaArray -> null
    is CtrSchemaPrimitive -> null
  }

  private fun toJavaClass(schema: CtrSchemaObject): JavaClass {
    val reference = referencesLookup[schema] ?: throw IllegalArgumentException("Schema not in referencesLookup: $schema")
    val className = reference.typeName
    val properties = schema.properties.map(::toJavaProperty)
    return JavaClass(className, schema.title, properties)
  }

  private fun toJavaProperty(property: CtrSchemaProperty): JavaProperty {
    val schema = property.schema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $property")
    return JavaProperty(property.name.toJavaIdentifier(), property.name, property.required, toReference(schema))
  }

  private fun toJavaEnum(schema: CtrSchemaEnum): JavaEnum {
    val className = referencesLookup[schema]?.typeName ?: throw IllegalArgumentException("Schema not in referencesLookup: $schema")
    val constants = schema.values.map { EnumConstant(it, it.toJavaConstant()) }
    return JavaEnum(className, schema.title, constants)
  }
}
