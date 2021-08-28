package codegen

import spock.lang.Specification
import spock.lang.Unroll

class GeneratorTest extends Specification {

  static def simpleHarness = new GeneratorHarness("src/test/contract/petstore-simple.yaml", "petstoresimple", true)
  static def usptoHarness = new GeneratorHarness("src/test/contract/uspto.yaml", "uspto", true)
  static def serverHarness = new GeneratorHarness("src/test/contract/client-server.yaml", "server", true)
  static def clientHarness = new GeneratorHarness("src/test/contract/client-server.yaml", "client", false)

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

  @Unroll
  def "Test server: #fileName"() {
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
  def "Test client: #fileName"() {
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
