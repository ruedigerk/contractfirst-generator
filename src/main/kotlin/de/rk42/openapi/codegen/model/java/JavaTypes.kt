package de.rk42.openapi.codegen.model.java

sealed interface JavaType

data class JavaClass(
    val javaIdentifier: String,
    val title: String,
    val properties: List<JavaProperty>,
) : JavaType

data class JavaProperty(
    val javaIdentifier: String,
    val name: String,
    val required: Boolean,
    var type: JavaType
)

data class JavaCollection(
    val title: String,
    val collectionType: String,
    var itemType: JavaType
) : JavaType

data class JavaEnum(
    val javaIdentifier: String,
    val title: String,
    val values: List<String>
) : JavaType

data class JavaBuiltIn(
    val typeName: String
) : JavaType
