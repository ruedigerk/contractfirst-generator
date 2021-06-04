package de.rk42.openapi.codegen

import spock.lang.Specification

class NamesTest extends Specification {

  def "mediaTypeToJavaIdentifier"() {
    expect:
    Names.mediaTypeToJavaIdentifier("application/json") == "ApplicationJson"
    Names.mediaTypeToJavaIdentifier("application/xml") == "ApplicationXml"
    Names.mediaTypeToJavaIdentifier("application/*") == "ApplicationStar"
    Names.mediaTypeToJavaIdentifier("*/*") == "StarStar"
    Names.mediaTypeToJavaIdentifier("application/json; charset=UTF-8") == "ApplicationJsonCharsetUTF8"
  }
  
  def "capitalize"() {
    expect:
    Names.capitalize("test") == "Test"
    Names.capitalize(" test") == " test"
    Names.capitalize("") == ""
    Names.capitalize("Test") == "Test"
  }
  
  def "camelize"() {
    expect:
    Names.camelize("abcd", false) == "Abcd"
    Names.camelize("some-value", false) == "SomeValue"
    Names.camelize("some_value", false) == "SomeValue"
    Names.camelize("\$type", false) == "\$Type"
    Names.camelize("abcd", true) == "abcd"
    Names.camelize("some-value", true) == "someValue"
    Names.camelize("some_value", true) == "someValue"
    Names.camelize("Abcd", true) == "abcd"
    Names.camelize("\$type", true) == "\$type"
    Names.camelize("123", true) == "123"
    Names.camelize("\$123", true) == "\$123"
  }
}
