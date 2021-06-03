package de.rk42.openapi.codegen

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
class JavaTransformer {

  private val schemaTransformer = JavaSchemaTransformer()

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

private class JavaSchemaTransformer {

  private var uniqueNameCounter: Int = 1
  private val referencesLookup: MutableMap<CtrSchema, JavaReference> = mutableMapOf()

  fun parseSchemas(schemas: List<CtrSchemaNonRef>): List<JavaType> {
    referencesLookup.putAll(createReferencesToSchemaMap(schemas))
    return schemas.mapNotNull(::toJavaType)
  }

  fun lookupReference(schema: CtrSchema): JavaReference = 
      referencesLookup[schema] ?: throw IllegalArgumentException("Schema not in referencesLookup: $schema")

  private fun createReferencesToSchemaMap(schemas: List<CtrSchemaNonRef>): Map<out CtrSchema, JavaReference> =
      schemas.associateWith { toReference(it) }

  private fun toReference(schema: CtrSchemaNonRef): JavaReference = when (schema) {
    is CtrSchemaObject -> toJavaReference(schema.reference?.referencedName() ?: schema.title)
    is CtrSchemaEnum -> toJavaReference(schema.reference?.referencedName() ?: schema.title)
    is CtrSchemaArray -> toJavaCollectionReference(schema)
    is CtrSchemaPrimitive -> toJavaBuiltInReference(schema)
  }

  private fun toJavaReference(name: String): JavaReference = JavaReference(
      if (name.isEmpty()) {
        createUniqueTypeName()
      } else {
        name.toJavaTypeIdentifier()
      }
  )

  private fun toJavaCollectionReference(schema: CtrSchemaArray): JavaReference {
    val itemSchema = schema.itemSchema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $schema")
    val itemReference = toReference(itemSchema)
    return JavaReference("java.util.List", itemReference.typeName)
  }

  private fun toJavaBuiltInReference(schema: CtrSchemaPrimitive): JavaReference = JavaReference(
      when (schema.type) {
        BOOLEAN -> "java.lang.Boolean"
        INTEGER -> "java.lang.Integer"
        NUMBER -> "java.lang.Double"
        STRING -> "java.lang.String"
      }
  )

  private fun createUniqueTypeName(): String = "Type$uniqueNameCounter"

  private fun toJavaType(schema: CtrSchemaNonRef): JavaType? = when (schema) {
    is CtrSchemaObject -> toJavaClass(schema)
    is CtrSchemaEnum -> toJavaEnum(schema)
    is CtrSchemaArray -> null
    is CtrSchemaPrimitive -> null
  }

  private fun toJavaClass(schema: CtrSchemaObject): JavaClass {
    val className = referencesLookup[schema]?.typeName ?: throw IllegalArgumentException("Schema not in referencesLookup: $schema")
    val properties = schema.properties.map(::toJavaProperty)
    return JavaClass(className, schema.title, properties)
  }

  private fun toJavaProperty(property: CtrSchemaProperty): JavaProperty {
    val schema = property.schema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $property")
    return JavaProperty(property.name.toJavaIdentifier(), property.name, property.required, toReference(schema))
  }

  private fun toJavaEnum(schema: CtrSchemaEnum): JavaEnum {
    val className = referencesLookup[schema]?.typeName ?: throw IllegalArgumentException("Schema not in referencesLookup: $schema")
    val constants = schema.values.map { it.toJavaIdentifier().uppercase() }
    return JavaEnum(className, schema.title, constants)
  }
}

private val INVALID_IDENTIFIER_PATTERN = Regex("[^_a-zA-Z0-9]")
private val CONSECUTIVE_UNDERSCORES = Regex("[_]{2,}")

private fun String.toJavaIdentifier(): String = this
    .replace(INVALID_IDENTIFIER_PATTERN, "_")
    .replace(CONSECUTIVE_UNDERSCORES, "_")
    .let { if (it.first().isDigit()) "_$it" else it }

private fun String.toJavaTypeIdentifier(): String = this
    .toJavaIdentifier()
    .replaceFirstChar(Char::uppercase)