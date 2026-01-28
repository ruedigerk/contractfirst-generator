package io.github.ruedigerk.contractfirst.generator.java.generator

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.NameAllocator
import com.squareup.javapoet.TypeName
import java.util.*
import javax.lang.model.element.Modifier

/**
 * Used for generating the methods inherited from Object: equals, hashCode and toString.
 */
object MethodsFromObject {

  fun generateEqualsHashCodeAndToString(thisClassName: ClassName, fields: List<FieldSpec>): List<MethodSpec> =
    listOf(generateEquals(thisClassName, fields), generateHashCode(fields), generateToString(thisClassName, fields))

  private fun generateEquals(thisTypeName: TypeName, fields: List<FieldSpec>): MethodSpec {
    val localNameAllocator = NameAllocator()
    fields.forEach { localNameAllocator.newName(it.name, it) }

    val parameterName = localNameAllocator.newName("other")
    val otherName = localNameAllocator.newName("o")

    val result = MethodSpec.methodBuilder("equals")
      .addAnnotation(Override::class.java)
      .addModifiers(Modifier.PUBLIC)
      .returns(Boolean::class.javaPrimitiveType)
      .addParameter(Any::class.java, parameterName)

    if (fields.isEmpty()) {
      result.addStatement("return getClass() == \$N.getClass()", parameterName)
      return result.build()
    }

    result.addStatement("if (\$N == this) return true", parameterName)
    result.addStatement("if (\$1N == null || getClass() != \$1N.getClass()) return false", parameterName)
    result.addStatement("\$T \$N = (\$T) \$N", thisTypeName, otherName, thisTypeName, parameterName)

    val firstFieldName = localNameAllocator[fields[0]]
    result.addCode("\$[return $1T.equals($2L, $3N.$2L)", Objects::class.java, firstFieldName, otherName)

    for (index in 1..fields.lastIndex) {
      val field = fields[index]
      val fieldName = localNameAllocator[field]
      result.addCode("\n&& $1T.equals($2L, $3N.$2L)", Objects::class.java, fieldName, otherName)
    }

    result.addCode(";\n$]")

    return result.build()
  }

  private fun generateHashCode(fields: List<FieldSpec>): MethodSpec {
    val localNameAllocator = NameAllocator()
    fields.forEach { localNameAllocator.newName(it.name, it) }

    val result = MethodSpec.methodBuilder("hashCode")
      .addAnnotation(Override::class.java)
      .addModifiers(Modifier.PUBLIC)
      .returns(Int::class.javaPrimitiveType)

    if (fields.isEmpty()) {
      result.addStatement("return 0")
      return result.build()
    }

    result.addCode("\$[return Objects.hash(")

    for (index in 0 until fields.lastIndex) {
      val field = fields[index]
      result.addCode("\$N, ", field)
    }

    result.addCode("\$N", fields.last())
    result.addCode(");\n\$]")

    return result.build()
  }

  /**
   * TODO: Generate more fancy indented toString like OpenAPI-Generator? Example:
   *
   *  public String toString() {
   *    StringBuilder sb = new StringBuilder();
   *    sb.append("class Item {\n");
   *
   *    sb.append("    id: ").append(toIndentedString(id)).append("\n");
   *    sb.append("    name: ").append(toIndentedString(name)).append("\n");
   *    sb.append("}");
   *    return sb.toString();
   *  }
   *
   *  private String toIndentedString(Object o) {
   *    if (o == null) {
   *      return "null";
   *    }
   *    return o.toString().replace("\n", "\n    ");
   *  }
   */
  private fun generateToString(thisClassName: ClassName, fields: List<FieldSpec>): MethodSpec {
    val nameAllocator = NameAllocator()
    fields.forEach { nameAllocator.newName(it.name, it) }

    val result = MethodSpec.methodBuilder("toString")
      .addAnnotation(Override::class.java)
      .addModifiers(Modifier.PUBLIC)
      .returns(String::class.java)

    val builderName = nameAllocator.newName("builder")
    result.addStatement("$1T $2N = new $1T()", StringBuilder::class.java, builderName)

    for (field in fields) {
      val fieldName = nameAllocator[field]
      result.addStatement("\$N.append(\", \$N=\").append(\$L)", builderName, field.name, fieldName)
    }

    result.addStatement("return builder.replace(0, 2, \"\$L{\").append('}').toString()", thisClassName.simpleName())

    return result.build()
  }
}
