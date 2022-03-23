package io.github.ruedigerk.contractfirst.generator.java.model

import java.math.BigDecimal
import java.math.BigInteger

/**
 * Represents any kind of validation performed on a value.
 */
sealed interface TypeValidation

/**
 * Represents a validation performed on integral values like int and BigInteger.
 */
data class IntegralValidation(
    val type: NumericValidationType,
    val value: BigInteger
) : TypeValidation {
  
  fun toDecimalValidation(): DecimalValidation = DecimalValidation(type, value.toBigDecimal(), true)
}

/**
 * Represents a validation performed on decimal values like double and BigDecimal.
 */
data class DecimalValidation(
    val type: NumericValidationType,
    val value: BigDecimal,
    val inclusive: Boolean
) : TypeValidation

/**
 * Represents the type of numeric validation.
 */
enum class NumericValidationType {
  MIN,
  MAX
}

/**
 * Represents a validation on values with a size, e.g. Strings or Arrays.
 */
data class SizeValidation(
    val min: Int?,
    val max: Int?,
) : TypeValidation {

  init {
    if (min == null && max == null) {
      throw IllegalArgumentException("Not both min and max may be null at the same time")
    }
  }
}

/**
 * Represents a validation on Strings using regular expressions.
 */
data class PatternValidation(val pattern: String) : TypeValidation

/**
 * Represents a validation on complex values that themselves are defined with validations on their properties. 
 */
object ValidatedValidation : TypeValidation