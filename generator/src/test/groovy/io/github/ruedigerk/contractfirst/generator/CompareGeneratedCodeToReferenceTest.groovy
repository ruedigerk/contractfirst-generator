package io.github.ruedigerk.contractfirst.generator

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Compares generated code to the reference in the src/test/java source root.
 */
class CompareGeneratedCodeToReferenceTest extends Specification {

  static def serverHarness = new GeneratorHarness("src/test/contract/testsuite.yaml", "server", true)
  static def clientHarness = new GeneratorHarness("src/test/contract/testsuite.yaml", "client", false)
  static def selfReferentialHarness = new GeneratorHarness("src/test/contract/self-referential-model.yaml", "selfreferential", true)

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

  @Unroll
  def "Test self-referential data model: #fileName"() {
    when:
    selfReferentialHarness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << selfReferentialHarness.relativePathNames
    referenceFile << selfReferentialHarness.referenceFiles
    generatedFile << selfReferentialHarness.generatedFiles
  }
}
