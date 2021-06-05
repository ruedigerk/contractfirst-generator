package codegen

import codegen.testframework.GeneratorHarness
import spock.lang.Specification

class GeneratorTest extends Specification {

  static def simpleHarness = new GeneratorHarness("src/test/contract/petstore-simple.yaml", "petstoresimple")
  
  def "Test petstore-simple.yaml"() {
    when:
    simpleHarness.runGenerator()
    
    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    referenceFile << simpleHarness.referenceFiles
    generatedFile << simpleHarness.generatedFiles
  }
}
