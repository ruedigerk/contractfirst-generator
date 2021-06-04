package de.rk42.openapi.codegen.model.java

sealed interface JavaType

data class JavaClass(
    val className: String,
    val title: String,
    val properties: List<JavaProperty>,
) : JavaType

data class JavaProperty(
    val javaIdentifier: String,
    val name: String,
    val required: Boolean,
    var type: JavaReference
)

data class JavaEnum(
    val javaIdentifier: String,
    val title: String,
    val values: List<String>
) : JavaType

data class JavaBuiltIn(
    val typeName: String
) : JavaType

data class JavaReference(
    val typeName: String,
    val packageName: String,
    val isGeneratedType: Boolean,
    val typeParameter: JavaReference? = null,
)