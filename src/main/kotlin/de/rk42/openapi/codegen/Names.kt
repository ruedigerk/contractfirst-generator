package de.rk42.openapi.codegen

import java.util.regex.Pattern
import javax.lang.model.SourceVersion

object Names {

  private val invalidJavaIdentifierPattern = Regex("[^_a-zA-Z0-9]")
  private val consecutiveUnderscoresPattern = Regex("[_]{2,}")

  private val whitespacePattern = Regex("\\s+")
  private val mediaTypeSpecialCharsPattern = Regex("[/;=]")

  private val camelizeSlashPattern = Pattern.compile("\\/(.?)")
  private val camelizeUppercasePattern = Pattern.compile("(\\.?)(\\w)([^\\.]*)$")
  private val camelizeUnderscorePattern = Pattern.compile("(_)(.)")
  private val camelizeHyphenPattern = Pattern.compile("(-)(.)")
  private val camelizeDollarPattern = Pattern.compile("\\$")
  private val camelizeSimpleUnderscorePattern = Pattern.compile("_")

  fun String.toJavaIdentifier(): String = cleanUpJavaIdentifier().camelize(true)

  fun String.toJavaTypeIdentifier(): String = cleanUpJavaIdentifier().camelize(false)

  private fun String.cleanUpJavaIdentifier(): String = this
      .replace(invalidJavaIdentifierPattern, "_")
      .replace(consecutiveUnderscoresPattern, "_")
      .let { if (it.first().isDigit()) "_$it" else it }

  @JvmStatic
  fun mediaTypeToJavaIdentifier(mediaType: String): String {
    val adjusted = mediaType
        .replace(whitespacePattern, "")
        .replace(mediaTypeSpecialCharsPattern, "_")
        .replace("*", "Star")
        .camelize()

    if (SourceVersion.isName(adjusted)) {
      return adjusted
    } else {
      throw IllegalArgumentException("Could not convert media type '$mediaType' to valid Java identifier, got '$adjusted'")
    }
  }

  @JvmStatic
  fun String.capitalize(): String = if (this.isEmpty() || !this[0].isLowerCase()) this else this[0].uppercase() + this.substring(1)

  /**
   * TODO: Die ganze Geschichte funktioniert komisch und sehr kompliziert (mit den Dots usw.). Umschreiben?
   * TODO: Lizenzproblematik? Original ist Apache License, Version 2.0 von OpenAPI-Generator.
   *
   * Camelize name (parameter, property, method, etc.).
   * copied from Twitter elephant bird
   * https://github.com/twitter/elephant-bird/blob/master/core/src/main/java/com/twitter/elephantbird/util/Strings.java
   *
   * @receiver the String to camelize.
   * @param lowercaseFirstLetter lower case for first letter if set to true
   * @return camelized string
   */
  @JvmStatic
  fun String.camelize(lowercaseFirstLetter: Boolean = false): String {
    var word: String = this

    // Replace all slashes with dots (package separator).
    var matcher = camelizeSlashPattern.matcher(word)
    while (matcher.find()) {
      word = matcher.replaceFirst("." + matcher.group(1))
      matcher = camelizeSlashPattern.matcher(word)
    }

    // Capitalize dot-separated words.
    word = word.split("\\.").joinToString("") { it.capitalize() }
    matcher = camelizeSlashPattern.matcher(word)
    while (matcher.find()) {
      word = matcher.replaceFirst("" + Character.toUpperCase(matcher.group(1)[0]) + matcher.group(1).substring(1))
      matcher = camelizeSlashPattern.matcher(word)
    }

    // Uppercase the class name.
    matcher = camelizeUppercasePattern.matcher(word)
    if (matcher.find()) {
      var rep: String = matcher.group(1) + matcher.group(2).uppercase() + matcher.group(3)
      rep = camelizeDollarPattern.matcher(rep).replaceAll("\\\\\\$")
      word = matcher.replaceAll(rep)
    }

    // Remove all underscores (underscore_case to camelCase).
    matcher = camelizeUnderscorePattern.matcher(word)
    while (matcher.find()) {
      val original = matcher.group(2)
      val upperCase = original.uppercase()
      word = if (original == upperCase) {
        camelizeSimpleUnderscorePattern.matcher(word).replaceFirst("")
      } else {
        matcher.replaceFirst(upperCase)
      }
      matcher = camelizeUnderscorePattern.matcher(word)
    }

    // Remove all hyphens (hyphen-case to camelCase).
    matcher = camelizeHyphenPattern.matcher(word)
    while (matcher.find()) {
      word = matcher.replaceFirst(matcher.group(2).uppercase())
      matcher = camelizeHyphenPattern.matcher(word)
    }
    if (lowercaseFirstLetter && word.isNotEmpty()) {
      var i = 0
      var charAt = word[i]
      while (i + 1 < word.length && !(charAt in 'a'..'z' || charAt in 'A'..'Z')) {
        i += 1
        charAt = word[i]
      }
      i += 1
      word = word.substring(0, i).lowercase() + word.substring(i)
    }

    // Remove all underscores.
    return camelizeSimpleUnderscorePattern.matcher(word).replaceAll("")
  }
}