package codegen

import spock.lang.Specification

class GeneratorTest extends Specification {

  static def simpleHarness = new GeneratorHarness("src/test/contract/petstore-simple.yaml", "petstoresimple")
  
  def "Test petstore-simple: #fileName"() {
    when:
    simpleHarness.runGenerator()
    
    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << simpleHarness.relativePathNames
    referenceFile << simpleHarness.referenceFiles
    generatedFile << simpleHarness.generatedFiles
  }
}
