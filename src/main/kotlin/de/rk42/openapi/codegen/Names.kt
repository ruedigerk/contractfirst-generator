package de.rk42.openapi.codegen

object Names {

  @JvmStatic
  fun String.toJavaIdentifier(): String = camelize(false).adjustFirstCharacter()

  @JvmStatic
  fun String.toJavaTypeIdentifier(): String = camelize().adjustFirstCharacter()

  private fun String.adjustFirstCharacter(): String = if (this.isEmpty() || this[0].isJavaIdentifierStart()) this else "_$this"

  @JvmStatic
  fun String.capitalize(): String = if (this.isEmpty() || !this[0].isLowerCase()) this else this[0].uppercase() + this.substring(1)

  @JvmStatic
  fun mediaTypeToJavaIdentifier(mediaType: String): String = mediaType
      .replace("*", "Star")
      .toJavaTypeIdentifier()

  @JvmStatic
  fun String.camelize(uppercaseFirstLetter: Boolean = true): String {
    val builder = StringBuilder(this.length)

    var lastWasLetter = false
    var nextIsUpper = uppercaseFirstLetter

    for (char in this) {
      val valid = char != '_' && Character.isJavaIdentifierPart(char)
      val letter = valid && char.isLetter()

      if (valid) {
        val charToInsert = if (lastWasLetter) {
          char
        } else {
          if (nextIsUpper) char.uppercase() else char.lowercase()
        }
        
        builder.append(charToInsert)
      }

      if (letter) {
        nextIsUpper = false
      }

      if (!letter && lastWasLetter) {
        nextIsUpper = true
      }

      lastWasLetter = letter
    }

    return builder.toString()
  }
}