package de.rk42.openapi.codegen.java.model

sealed interface JavaAnyType {

  val name: String
  val packageName: String

  /** Returns whether this is a type that needs to be validated. */
  val validations: List<TypeValidation>
}

data class JavaType(
    override val name: String,
    override val packageName: String,
    override val validations: List<TypeValidation> = emptyList()
) : JavaAnyType

data class JavaCollectionType(
    override val name: String,
    override val packageName: String,
    val elementType: JavaAnyType,
    override val validations: List<TypeValidation>
) : JavaAnyType

data class JavaMapType(
    override val name: String,
    override val packageName: String,
    val valuesType: JavaAnyType,
    override val validations: List<TypeValidation>
) : JavaAnyType
