package codegen


import spock.lang.Specification
import spock.lang.Unroll

class GeneratorTest extends Specification {

  static def simpleHarness = new GeneratorHarness("src/test/contract/petstore-simple.yaml", "petstoresimple")
  static def usptoHarness = new GeneratorHarness("src/test/contract/uspto.yaml", "uspto")

  @Unroll
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

  @Unroll
  def "Test uspto-simple: #fileName"() {
    when:
    usptoHarness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << usptoHarness.relativePathNames
    referenceFile << usptoHarness.referenceFiles
    generatedFile << usptoHarness.generatedFiles
  }
}
