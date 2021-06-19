package de.rk42.openapi.codegen.java.transform


import spock.lang.Specification 

class UniqueNameFinderTest extends Specification {

  def "toUniqueTypeName"() {
    given:
    UniqueNameFinder uniqueNameFinder = new UniqueNameFinder()
    
    expect:
    uniqueNameFinder.toUniqueName("a") == "a"
    uniqueNameFinder.toUniqueName("b") == "b"
    uniqueNameFinder.toUniqueName("c") == "c"
    uniqueNameFinder.toUniqueName("a") == "a2"
    uniqueNameFinder.toUniqueName("a") == "a3"
  }
}
