package org.contractfirst.generator.java.model

import java.math.BigDecimal

sealed interface TypeValidation

data class IntegralValidation(
    val type: NumericValidationType,
    val value: Long
) : TypeValidation

data class DecimalValidation(
    val type: NumericValidationType,
    val value: BigDecimal,
    val inclusive: Boolean
) : TypeValidation

enum class NumericValidationType {
  MIN,
  MAX
}

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

data class PatternValidation(val pattern: String) : TypeValidation

object ValidatedValidation : TypeValidation