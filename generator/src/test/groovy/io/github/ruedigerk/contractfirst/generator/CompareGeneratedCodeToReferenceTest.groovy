package io.github.ruedigerk.contractfirst.generator


import spock.lang.Specification

import static io.github.ruedigerk.contractfirst.generator.configuration.GeneratorVariant.*

/**
 * Compares generated code to the reference in the src/test/java source root.
 */
class CompareGeneratedCodeToReferenceTest extends Specification {

  static def serverHarness = new GeneratorHarness("src/test/contract/testsuite.yaml", "server", SERVER_JAX_RS)
  static def clientHarness = new GeneratorHarness("src/test/contract/testsuite.yaml", "client", CLIENT_OKHTTP)
  static def combinationsServerHarness = new GeneratorHarness("src/test/contract/content-type-combinations.yaml", "combinations_server", SERVER_JAX_RS)
  static def combinationsClientHarness = new GeneratorHarness("src/test/contract/content-type-combinations.yaml", "combinations_client", CLIENT_OKHTTP)
  static def selfReferentialHarness = new GeneratorHarness("src/test/contract/self-referential-model.yaml", "selfreferential", CLIENT_OKHTTP)
  static def parametersServerHarness = new GeneratorHarness("src/test/contract/equally-named-parameters.yaml", "parameters_server", SERVER_JAX_RS)
  static def parametersClientHarness = new GeneratorHarness("src/test/contract/equally-named-parameters.yaml", "parameters_client", CLIENT_OKHTTP)
  static def validationsHarness = new GeneratorHarness("src/test/contract/validations.yaml", "validations", SERVER_JAX_RS)
  static def modelOnlyHarness = new GeneratorHarness("src/test/contract/modelOnlySchemas", "model_only", MODEL_ONLY)
  static def serverJsr305Harness = new GeneratorHarness("src/test/contract/testsuite.yaml", "server_jsr305", SERVER_JAX_RS, "", true)
  static def clientJsr305Harness = new GeneratorHarness("src/test/contract/testsuite.yaml", "client_jsr305", CLIENT_OKHTTP, "", true)
  static def modelOnlyJsr305Harness = new GeneratorHarness("src/test/contract/modelOnlySchemas", "model_only_jsr305", MODEL_ONLY, "", true)

  def "Testsuite (server): #fileName"() {
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

  def "Testsuite (client): #fileName"() {
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

  def "Content type combinations (server): #fileName"() {
    when:
    combinationsServerHarness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << combinationsServerHarness.relativePathNames
    referenceFile << combinationsServerHarness.referenceFiles
    generatedFile << combinationsServerHarness.generatedFiles
  }

  def "Content type combinations (client): #fileName"() {
    when:
    combinationsClientHarness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << combinationsClientHarness.relativePathNames
    referenceFile << combinationsClientHarness.referenceFiles
    generatedFile << combinationsClientHarness.generatedFiles
  }

  def "Self referential data model: #fileName"() {
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

  def "Operations with equally named parameters (server): #fileName"() {
    when:
    parametersServerHarness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << parametersServerHarness.relativePathNames
    referenceFile << parametersServerHarness.referenceFiles
    generatedFile << parametersServerHarness.generatedFiles
  }

  def "Operations with equally named parameters (client): #fileName"() {
    when:
    parametersClientHarness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << parametersClientHarness.relativePathNames
    referenceFile << parametersClientHarness.referenceFiles
    generatedFile << parametersClientHarness.generatedFiles
  }

  def "Validation annotations: #fileName"() {
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

  def "Model-Only mode: #fileName"() {
    when:
    modelOnlyHarness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << modelOnlyHarness.relativePathNames
    referenceFile << modelOnlyHarness.referenceFiles
    generatedFile << modelOnlyHarness.generatedFiles
  }

  def "Testsuite (server) with JSR-305: #fileName"() {
    when:
    serverJsr305Harness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << serverJsr305Harness.relativePathNames
    referenceFile << serverJsr305Harness.referenceFiles
    generatedFile << serverJsr305Harness.generatedFiles
  }

  def "Testsuite (client) with JSR-305: #fileName"() {
    when:
    clientJsr305Harness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << clientJsr305Harness.relativePathNames
    referenceFile << clientJsr305Harness.referenceFiles
    generatedFile << clientJsr305Harness.generatedFiles
  }

  def "Model-Only mode with JSR-305: #fileName"() {
    when:
    modelOnlyJsr305Harness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << modelOnlyJsr305Harness.relativePathNames
    referenceFile << modelOnlyJsr305Harness.referenceFiles
    generatedFile << modelOnlyJsr305Harness.generatedFiles
  }
}
