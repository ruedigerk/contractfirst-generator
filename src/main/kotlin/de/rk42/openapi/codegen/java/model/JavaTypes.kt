package de.rk42.openapi.codegen.java.model

sealed interface JavaAnyType {
    
    val name: String
    val packageName: String

    /** Returns whether this is a type that needs to be validated. */
    val validated: Boolean
}

data class JavaType(
    override val name: String,
    override val packageName: String,
    override val validated: Boolean,
) : JavaAnyType

data class JavaCollectionType(
    override val name: String,
    override val packageName: String,
    val elementType: JavaAnyType,
) : JavaAnyType {
    
    override val validated: Boolean
      get() = false
}

data class JavaMapType(
    override val name: String,
    override val packageName: String,
    val valuesType: JavaAnyType,
) : JavaAnyType {
    
    override val validated: Boolean
      get() = false
}