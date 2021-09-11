package org.contractfirst.generator.java

object Identifiers {

  @JvmStatic
  fun String.toJavaIdentifier(): String = toCamelCase(false).prefixUnderscoreIfFirstCharIsNotValid()

  @JvmStatic
  fun String.toJavaTypeIdentifier(): String = toCamelCase().prefixUnderscoreIfFirstCharIsNotValid()

  @JvmStatic
  fun String.toJavaConstant(): String = toUpperSnakeCase().prefixUnderscoreIfFirstCharIsNotValid()

  @JvmStatic
  fun String.mediaTypeToJavaIdentifier(): String = this
      .replace("*", "Star")
      .toJavaTypeIdentifier()

  private fun String.prefixUnderscoreIfFirstCharIsNotValid(): String = if (this.isEmpty() || this[0].isJavaIdentifierStart()) this else "_$this"

  @JvmStatic
  fun String.capitalize(): String = if (this.isEmpty() || !this[0].isLowerCase()) this else this[0].uppercase() + this.substring(1)

  @JvmStatic
  fun String.toCamelCase(uppercaseFirstLetter: Boolean = true): String {
    var firstTransition = true

    return rewriteCase { char, transition, _ ->
      val stringToInsert = if (transition && (!firstTransition || uppercaseFirstLetter)) char.uppercase() else char.lowercase()

      if (transition) {
        firstTransition = false
      }
      
      stringToInsert
    }
  }

  private fun String.toUpperSnakeCase() = rewriteCase { char, transition, index ->
    val upper = char.uppercase()
    if (transition && index > 0) "_$upper" else upper
  }

  private fun String.rewriteCase(transformer: (Char, Boolean, Int) -> String): String {
    val builder = StringBuilder(this.length)

    var lastWasLetter = false
    var lastWasUppercase = false

    for (index in this.indices) {
      val char = this[index]

      val valid = char != '_' && Character.isJavaIdentifierPart(char)
      val letter = valid && char.isLetter()
      val uppercase = letter && char.isUpperCase()

      if (valid) {
        val nextIsLowercase = index < this.lastIndex && this[index + 1].isLowerCase()
        val transition = !lastWasLetter && letter || !lastWasUppercase && uppercase || lastWasUppercase && uppercase && nextIsLowercase
        val stringToInsert = transformer(char, transition, index)
        
        builder.append(stringToInsert)
      }

      lastWasLetter = letter
      lastWasUppercase = uppercase
    }

    return builder.toString()
  }
}
