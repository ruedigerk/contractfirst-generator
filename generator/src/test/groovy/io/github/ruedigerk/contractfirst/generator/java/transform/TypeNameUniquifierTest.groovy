package io.github.ruedigerk.contractfirst.generator.java.transform

import io.github.ruedigerk.contractfirst.generator.java.model.JavaTypeName
import spock.lang.Specification

class TypeNameUniquifierTest extends Specification {

  def "toUniqueTypeName"() {
    given:
    TypeNameUniquifier uniqueNameFinder = new TypeNameUniquifier()

    expect:
    uniqueNameFinder.toUniqueName(new JavaTypeName("package", "a")) == new JavaTypeName("package", "a")
    uniqueNameFinder.toUniqueName(new JavaTypeName("package", "b")) == new JavaTypeName("package", "b")
    uniqueNameFinder.toUniqueName(new JavaTypeName("package", "c")) == new JavaTypeName("package", "c")
    uniqueNameFinder.toUniqueName(new JavaTypeName("package", "a")) == new JavaTypeName("package", "a2")
    uniqueNameFinder.toUniqueName(new JavaTypeName("package", "a")) == new JavaTypeName("package", "a3")
  }
}
