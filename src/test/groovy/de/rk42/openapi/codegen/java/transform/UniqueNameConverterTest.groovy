package de.rk42.openapi.codegen.java.transform


import spock.lang.Specification 

class UniqueNameConverterTest extends Specification {

  def "toUniqueTypeName"() {
    given:
    UniqueNameConverter converter = new UniqueNameConverter()
    
    expect:
    converter.toUniqueTypeName("a") == "A"
    converter.toUniqueTypeName("b") == "B"
    converter.toUniqueTypeName("c") == "C"
    converter.toUniqueTypeName("a") == "A2"
    converter.toUniqueTypeName("a") == "A3"
  }
}
