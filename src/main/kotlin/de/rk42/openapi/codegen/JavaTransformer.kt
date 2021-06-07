package de.rk42.openapi.codegen

import de.rk42.openapi.codegen.Names.toJavaConstant
import de.rk42.openapi.codegen.Names.toJavaIdentifier
import de.rk42.openapi.codegen.Names.toJavaTypeIdentifier
import de.rk42.openapi.codegen.TransformerHelper.toJavadoc
import de.rk42.openapi.codegen.model.contract.CtrContent
import de.rk42.openapi.codegen.model.contract.CtrOperation
import de.rk42.openapi.codegen.model.contract.CtrParameter
import de.rk42.openapi.codegen.model.contract.CtrPrimitiveType.BOOLEAN
import de.rk42.openapi.codegen.model.contract.CtrPrimitiveType.INTEGER
import de.rk42.openapi.codegen.model.contract.CtrPrimitiveType.NUMBER
import de.rk42.openapi.codegen.model.contract.CtrPrimitiveType.STRING
import de.rk42.openapi.codegen.model.contract.CtrRequestBody
import de.rk42.openapi.codegen.model.contract.CtrResponse
import de.rk42.openapi.codegen.model.contract.CtrSchema
import de.rk42.openapi.codegen.model.contract.CtrSchemaArray
import de.rk42.openapi.codegen.model.contract.CtrSchemaEnum
import de.rk42.openapi.codegen.model.contract.CtrSchemaMap
import de.rk42.openapi.codegen.model.contract.CtrSchemaNonRef
import de.rk42.openapi.codegen.model.contract.CtrSchemaObject
import de.rk42.openapi.codegen.model.contract.CtrSchemaPrimitive
import de.rk42.openapi.codegen.model.contract.CtrSchemaProperty
import de.rk42.openapi.codegen.model.contract.CtrSpecification
import de.rk42.openapi.codegen.model.java.EnumConstant
import de.rk42.openapi.codegen.model.java.JavaBasicReference
import de.rk42.openapi.codegen.model.java.JavaBodyParameter
import de.rk42.openapi.codegen.model.java.JavaClass
import de.rk42.openapi.codegen.model.java.JavaCollectionReference
import de.rk42.openapi.codegen.model.java.JavaContent
import de.rk42.openapi.codegen.model.java.JavaEnum
import de.rk42.openapi.codegen.model.java.JavaMapReference
import de.rk42.openapi.codegen.model.java.JavaOperation
import de.rk42.openapi.codegen.model.java.JavaOperationGroup
import de.rk42.openapi.codegen.model.java.JavaParameter
import de.rk42.openapi.codegen.model.java.JavaProperty
import de.rk42.openapi.codegen.model.java.JavaReference
import de.rk42.openapi.codegen.model.java.JavaRegularParameterLocation
import de.rk42.openapi.codegen.model.java.JavaResponse
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
    val requestBodySchemas = operation.requestBody?.contents?.map { it.schema }?.toSet() ?: emptySet()
    if (requestBodySchemas.size > 1) {
      throw NotSupportedException("Different response body schemas for a single operation are not supported: $operation")
    }

    val requestBodyMediaTypes = operation.requestBody?.contents?.map { it.mediaType }?.toSet() ?: emptySet()
    val bodyParameter = operation.requestBody?.let { listOf(it).map(::toBodyParameter) } ?: emptyList()

    return JavaOperation(
        operation.operationId.toJavaIdentifier(),
        operation.description ?: operation.summary,
        operation.path,
        operation.method,
        requestBodyMediaTypes.toList(),
        operation.parameters.map(::toJavaParameter) + bodyParameter,
        operation.responses.map(::toJavaResponse)
    )
  }

  private fun toBodyParameter(requestBody: CtrRequestBody): JavaParameter {
    if (requestBody.contents.isEmpty()) {
      throw NotSupportedException("Empty request body content is not supported: $requestBody")
    }

    // Currently, all body contents must have the same schema. This is enforced by toJavaOperation.
    val schema = requestBody.contents.first().schema

    return JavaParameter(
        "requestBody",
        JavaBodyParameter,
        requestBody.description,
        requestBody.required,
        toJavaReference(schema)
    )
  }

  private fun toJavaParameter(parameter: CtrParameter): JavaParameter = JavaParameter(
      parameter.name.toJavaIdentifier(),
      JavaRegularParameterLocation(parameter.name, parameter.location),
      parameter.description,
      parameter.required,
      toJavaReference(parameter.schema)
  )

  private fun toJavaResponse(response: CtrResponse): JavaResponse = JavaResponse(
      response.statusCode,
      response.contents.map(::toJavaResponseContent)
  )

  private fun toJavaResponseContent(content: CtrContent): JavaContent = JavaContent(
      content.mediaType,
      toJavaReference(content.schema)
  )

  private fun toJavaReference(schema: CtrSchema): JavaReference = schemaTransformer.lookupReference(schema)

  companion object {

    private const val GROUP_NAME_SUFFIX = "Api"
    private const val DEFAULT_GROUP_NAME = "Default"
  }
}

private class JavaSchemaTransformer(private val configuration: CliConfiguration) {

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
    is CtrSchemaObject -> toJavaReference(schema.referencedBy?.referencedName() ?: schema.title, true)
    is CtrSchemaEnum -> toJavaReference(schema.referencedBy?.referencedName() ?: schema.title, false)
    is CtrSchemaArray -> toJavaCollectionReference(schema)
    is CtrSchemaMap -> toJavaMapReference(schema)
    is CtrSchemaPrimitive -> toJavaBuiltInReference(schema)
  }

  private fun toJavaReference(name: String?, isGeneratedClass: Boolean): JavaReference {
    val typeName = name?.toJavaTypeIdentifier() ?: createUniqueTypeName()
    val finalTypeName = configuration.modelPrefix + typeName

    return JavaBasicReference(finalTypeName, modelPackage, isGeneratedClass)
  }

  private fun toJavaCollectionReference(schema: CtrSchemaArray): JavaCollectionReference {
    val elementSchema = schema.itemSchema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $schema")
    val elementReference = toReference(elementSchema)
    return JavaCollectionReference("List", "java.util", elementReference)
  }

  private fun toJavaMapReference(schema: CtrSchemaMap): JavaMapReference {
    val valuesSchema = schema.valuesSchema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $schema")
    val valuesReference = toReference(valuesSchema)
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
    return JavaClass(className, toJavadoc(schema), properties)
  }

  private fun toJavaProperty(property: CtrSchemaProperty): JavaProperty {
    val schema = property.schema as? CtrSchemaNonRef ?: throw IllegalArgumentException("Unexpected SchemaRef in $property")
    return JavaProperty(property.name.toJavaIdentifier(), toJavadoc(schema), property.name, property.required, toReference(schema))
  }

  private fun toJavaEnum(schema: CtrSchemaEnum): JavaEnum {
    val className = referencesLookup[schema]?.typeName ?: throw IllegalArgumentException("Schema not in referencesLookup: $schema")
    val constants = schema.values.map { EnumConstant(it, it.toJavaConstant()) }
    return JavaEnum(className, toJavadoc(schema), constants)
  }
}

private object TransformerHelper {
  
  fun toJavadoc(schema: CtrSchemaNonRef): String? = schema.description ?: schema.title
}