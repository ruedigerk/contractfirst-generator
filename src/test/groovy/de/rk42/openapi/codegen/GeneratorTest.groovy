package de.rk42.openapi.codegen

import groovy.io.FileType
import spock.lang.Shared
import spock.lang.Specification

class GeneratorTest extends Specification {

  // Test is being run with the project dir as the working directory, e.g. C:/Development/Workspace/OpenApiCodegen
  static final String referenceDir = "src/test/java/generated"
  static final String generatedDir = "build/generatedTestOutput/generated"
  
  @Shared
  List<String> referenceFiles

  def setupSpec() {
    runGenerator()
    referenceFiles = discoverFiles(new File(referenceDir))
  }

  def "Compare generated code and reference"() {
    expect:
    def reference = new File(referenceDir, file)
    def generated = new File(generatedDir, file)

    generated.exists()
    
    and:
    def expected = reference.text
    def actual = generated.text

    assert actual == expected
    
    where:
    file << referenceFiles
  }

  private static runGenerator() {
    MainKt.main(["--contract", "src/test/contract/petstore-simple.yaml", "--output-dir", "build/generatedTestOutput", "--package", "generated"] as String[])
  }

  private static List<String> discoverFiles(File dir) {
    List<String> result = []
    int prefixLength = dir.toString().length()

    dir.eachFileRecurse(FileType.FILES) {
      def relativePath = it.toString().substring(prefixLength)
      result.add(relativePath)

      println relativePath
    }

    result
  }
}
