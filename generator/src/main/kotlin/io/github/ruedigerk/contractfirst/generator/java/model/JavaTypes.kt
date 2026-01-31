package io.github.ruedigerk.contractfirst.generator.java.model

/**
 * Represents any kind of type used in generated Java code. Contains all data necessary to reference a type, including the constraints set on a type via
 * BeanValidation-Annotations. Does not represent the actual definition of that type. The definition of a type is JavaSourceFile.
 */
sealed interface JavaAnyType {

  val name: JavaTypeName
  val validations: List<TypeValidation>

  val isGenericType: Boolean
    get() = when (this) {
      is JavaType -> false
      is JavaCollectionType, is JavaMapType -> true
    }

  /**
   * Rewrites a JavaType or the element type of a JavaCollectionType if the existing type name matches.
   */
  fun rewriteSimpleType(replacedType: JavaTypeName, replacementType: JavaTypeName): JavaAnyType = when (this) {
    is JavaType if name == replacedType -> copy(name = replacementType)
    is JavaCollectionType if elementType is JavaType && elementType.name == replacedType -> copy(elementType = elementType.copy(name = replacementType))
    else -> this
  }
}

/**
 * Represent a non-generic Java type like java.lang.String or a generated class.
 */
data class JavaType(
  override val name: JavaTypeName,
  override val validations: List<TypeValidation> = emptyList(),
) : JavaAnyType {

  override fun toString(): String = "$name"
}

/**
 * Represent a generic collection type, with a type parameter for the element type, like List<String> or Set<Entity>.
 */
data class JavaCollectionType(
  override val name: JavaTypeName,
  val elementType: JavaAnyType,
  override val validations: List<TypeValidation>,
) : JavaAnyType {

  override fun toString(): String = "$name<$elementType>"
}

/**
 * Represent a Map type, with the keys having type String and the type of the values being represented as a type parameter, e.g., Map<String, Integer> or
 * Map<String, Entity>.
 */
data class JavaMapType(
  val valuesType: JavaAnyType,
  override val validations: List<TypeValidation>,
) : JavaAnyType {

  override val name: JavaTypeName
    get() = JavaTypeName.MAP

  override fun toString(): String = "$name<String, $valuesType>"
}
