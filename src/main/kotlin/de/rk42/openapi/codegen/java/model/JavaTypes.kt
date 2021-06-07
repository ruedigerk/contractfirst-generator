package de.rk42.openapi.codegen.java.model

sealed interface JavaType

data class JavaClass(
    val className: String,
    val javadoc: String?,
    val properties: List<JavaProperty>,
) : JavaType

data class JavaProperty(
    val javaIdentifier: String,
    val javadoc: String?,
    val name: String,
    val required: Boolean,
    var type: JavaReference
)

data class JavaEnum(
    val className: String,
    val javadoc: String?,
    val values: List<EnumConstant>
) : JavaType

data class EnumConstant(
    val originalName: String,
    val javaIdentifier: String
)

data class JavaBuiltIn(
    val typeName: String
) : JavaType

sealed interface JavaReference {
    
    val typeName: String
    val packageName: String

    /** Returns whether this reference points to a type that needs to be validated. */
    val isValidated: Boolean
}

data class JavaBasicReference(
    override val typeName: String,
    override val packageName: String,
    override val isValidated: Boolean,
) : JavaReference

data class JavaCollectionReference(
    override val typeName: String,
    override val packageName: String,
    val elementType: JavaReference,
) : JavaReference {
    
    override val isValidated: Boolean
      get() = false
}

data class JavaMapReference(
    override val typeName: String,
    override val packageName: String,
    val valuesType: JavaReference,
) : JavaReference {
    
    override val isValidated: Boolean
      get() = false
}