package io.github.ruedigerk.contractfirst.generator.java.model

/**
 * Represents any kind of type used in generated Java code. Contains all data necessary to reference a type, including the constraints set on a type via
 * BeanValidation-Annotations. Does not represent the actual definition of that type. The definition of a type is JavaSourceFile.
 */
sealed interface JavaAnyType {

  val name: JavaTypeName

  /** Returns whether this is a type that needs to be validated. */
  val validations: List<TypeValidation>

  val isGenericType: Boolean
    get() = this !is JavaType
}

/**
 * Represent a non-generic Java type.
 */
data class JavaType(
    override val name: JavaTypeName,
    override val validations: List<TypeValidation> = emptyList()
) : JavaAnyType {

  override fun toString(): String = "$name"

  companion object {

    operator fun invoke(typeName: String, packageName: String, validations: List<TypeValidation> = emptyList()): JavaType = 
        JavaType(JavaTypeName(packageName, typeName), validations)
  }
}

/**
 * Represent a collection type, like List and Set.
 */
data class JavaCollectionType(
    override val name: JavaTypeName,
    val elementType: JavaAnyType,
    override val validations: List<TypeValidation>
) : JavaAnyType {

  override fun toString(): String = "$name<$elementType>"
}

/**
 * Represent a collection of type Map.
 */
data class JavaMapType(
    override val name: JavaTypeName,
    val valuesType: JavaAnyType,
    override val validations: List<TypeValidation>
) : JavaAnyType {

  override fun toString(): String = "$name<String, $valuesType>"
}
