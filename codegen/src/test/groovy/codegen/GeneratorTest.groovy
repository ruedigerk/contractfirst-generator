package codegen

import spock.lang.Specification
import spock.lang.Unroll

class GeneratorTest extends Specification {

  static def petstoreHarness = new GeneratorHarness("src/test/contract/petstore.yaml", "petstore", true)
  static def usptoHarness = new GeneratorHarness("src/test/contract/uspto.yaml", "uspto", true)
  static def serverHarness = new GeneratorHarness("src/test/contract/testsuite.yaml", "server", true)
  static def clientHarness = new GeneratorHarness("src/test/contract/testsuite.yaml", "client", false)

  @Unroll
  def "Test petstore: #fileName"() {
    when:
    petstoreHarness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << petstoreHarness.relativePathNames
    referenceFile << petstoreHarness.referenceFiles
    generatedFile << petstoreHarness.generatedFiles
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

  @Unroll
  def "Test testsuite server: #fileName"() {
    when:
    serverHarness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << serverHarness.relativePathNames
    referenceFile << serverHarness.referenceFiles
    generatedFile << serverHarness.generatedFiles
  }

  @Unroll
  def "Test testsuite client: #fileName"() {
    when:
    clientHarness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << clientHarness.relativePathNames
    referenceFile << clientHarness.referenceFiles
    generatedFile << clientHarness.generatedFiles
  }
}
