package io.github.ruedigerk.contractfirst.generator

import spock.lang.Specification

/**
 * Compares generated code to the reference in the src/test/java source root.
 */
class CompareGeneratedCodeToReferenceTest extends Specification {

  static def serverHarness = new GeneratorHarness("src/test/contract/testsuite.yaml", "server", GeneratorType.SERVER)
  static def clientHarness = new GeneratorHarness("src/test/contract/testsuite.yaml", "client", GeneratorType.CLIENT)
  static def combinationsServerHarness = new GeneratorHarness("src/test/contract/content-type-combinations.yaml", "combinations_server", GeneratorType.SERVER)
  static def combinationsClientHarness = new GeneratorHarness("src/test/contract/content-type-combinations.yaml", "combinations_client", GeneratorType.CLIENT)
  static def selfReferentialHarness = new GeneratorHarness("src/test/contract/self-referential-model.yaml", "selfreferential", GeneratorType.CLIENT)
  static def multipartHarness = new GeneratorHarness("src/test/contract/multipart-request-body.yaml", "multipart", GeneratorType.CLIENT)
  static def parametersServerHarness = new GeneratorHarness("src/test/contract/equally-named-parameters.yaml", "parameters_server", GeneratorType.SERVER)
  static def parametersClientHarness = new GeneratorHarness("src/test/contract/equally-named-parameters.yaml", "parameters_client", GeneratorType.CLIENT)
  static def validationsHarness = new GeneratorHarness("src/test/contract/validations.yaml", "validations", GeneratorType.SERVER)
  static def modelOnlyHarness = new GeneratorHarness("src/test/contract/modelOnlySchemas", "model_only", GeneratorType.MODEL_ONLY)

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

  def "Multipart request body: #fileName"() {
    when:
    multipartHarness.runGenerator()

    then:
    generatedFile.exists()

    and:
    generatedFile.text == referenceFile.text

    where:
    fileName << multipartHarness.relativePathNames
    referenceFile << multipartHarness.referenceFiles
    generatedFile << multipartHarness.generatedFiles
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
}
