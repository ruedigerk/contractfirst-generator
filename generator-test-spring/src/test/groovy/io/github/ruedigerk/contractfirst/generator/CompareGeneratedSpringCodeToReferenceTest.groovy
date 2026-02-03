package io.github.ruedigerk.contractfirst.generator

import spock.lang.Specification

import static io.github.ruedigerk.contractfirst.generator.configuration.GeneratorVariant.SERVER_SPRING_WEB

/**
 * Compares generated code to the reference in the src/test/java source root.
 */
class CompareGeneratedSpringCodeToReferenceTest extends Specification {

  static def serverHarness = new GeneratorHarness("testsuite.yaml", "server_spring", SERVER_SPRING_WEB, "Rest")
  static def combinationsHarness = new GeneratorHarness("content-type-combinations.yaml", "combinations_server_spring", SERVER_SPRING_WEB)
  static def parametersHarness = new GeneratorHarness("equally-named-parameters.yaml", "parameters_server_spring", SERVER_SPRING_WEB)
  static def validationsHarness = new GeneratorHarness("validations.yaml", "validations_server_spring", SERVER_SPRING_WEB)
  static def jsr305Harness = new GeneratorHarness("testsuite.yaml", "jsr305_server_spring", SERVER_SPRING_WEB, "", true)

  def "Testsuite (Spring server): #fileName"() {
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

  def "Content type combinations (Spring server): #fileName"() {
    when:
    combinationsHarness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << combinationsHarness.relativePathNames
    referenceFile << combinationsHarness.referenceFiles
    generatedFile << combinationsHarness.generatedFiles
  }

  def "Operations with equally named parameters (Spring server): #fileName"() {
    when:
    parametersHarness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << parametersHarness.relativePathNames
    referenceFile << parametersHarness.referenceFiles
    generatedFile << parametersHarness.generatedFiles
  }

  def "Validation annotations (Spring server): #fileName"() {
    when:
    validationsHarness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << validationsHarness.relativePathNames
    referenceFile << validationsHarness.referenceFiles
    generatedFile << validationsHarness.generatedFiles
  }

  def "Testsuite (Spring server) with JSR-305: #fileName"() {
    when:
    jsr305Harness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << jsr305Harness.relativePathNames
    referenceFile << jsr305Harness.referenceFiles
    generatedFile << jsr305Harness.generatedFiles
  }
}
