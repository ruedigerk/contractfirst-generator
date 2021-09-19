package org.contractfirst.generator

import groovy.io.FileType
import org.contractfirst.generator.logging.Slf4jLogAdapter
import org.slf4j.LoggerFactory

/**
 * Harness for running the Contractfirst-Generator in tests to compare the generated code to some reference.
 */
class GeneratorHarness {

  // Tests are being run with the maven module directory as the working directory.
  private static final String OUTPUT_DIR = "target/generatedTestOutput"

  final List<String> relativePathNames
  final List<File> referenceFiles
  final List<File> generatedFiles
  
  private final String referenceDir
  private final String generatedDir
  private final String inputContractFile
  private final String outputJavaBasePackage
  private final String outputJavaNamePrefix
  private final boolean generateServer

  private generatorRan = false

  GeneratorHarness(String inputContractFile, String outputJavaBasePackage, boolean generateServer, String outputJavaNamePrefix = "") {
    this.inputContractFile = inputContractFile
    this.outputJavaBasePackage = outputJavaBasePackage
    this.generateServer = generateServer
    this.outputJavaNamePrefix = outputJavaNamePrefix

    referenceDir = "src/test/java/$outputJavaBasePackage"
    generatedDir = "$OUTPUT_DIR/$outputJavaBasePackage"

    relativePathNames = discoverFiles(referenceDir)

    referenceFiles = relativePathNames.collect {
      new File(referenceDir, it)
    }
    generatedFiles = relativePathNames.collect {
      new File(generatedDir, it)
    }

    assert !relativePathNames.isEmpty()
  }

  def runGenerator() {
    if (!generatorRan) {
      generatorRan = true

      deleteOutputDirectory()
      executeGenerator()
      checkForAdditionalFiles()
    }
  }

  private void deleteOutputDirectory() {
    def outputDir = new File(generatedDir)

    if (outputDir.exists()) {
      outputDir.deleteDir()
    }
  }

  private executeGenerator() {
    def logAdapter = new Slf4jLogAdapter(LoggerFactory.getLogger("Contractfirst-Generator.$outputJavaBasePackage"))
    new ContractfirstGenerator(logAdapter).generate(
        new Configuration(
            inputContractFile,
            generateServer ? GeneratorType.SERVER : GeneratorType.CLIENT,
            OUTPUT_DIR,
            true,
            "$outputJavaBasePackage/openapi.yaml",
            outputJavaBasePackage,
            outputJavaNamePrefix
        )
    )
  }

  private static List<String> discoverFiles(String dir) {
    List<String> result = []
    int prefixLength = dir.length()

    new File(dir).eachFileRecurse(FileType.FILES) {
      def relativePath = it.toString().substring(prefixLength)
      result.add(relativePath)
    }

    result
  }

  private checkForAdditionalFiles() {
    def actuallyGeneratedFiles = discoverFiles(generatedDir)

    if (actuallyGeneratedFiles.size() > referenceFiles.size()) {
      def referenceFiles = discoverFiles(referenceDir)
      def additionalFiles = (actuallyGeneratedFiles as Set) - referenceFiles
      
      // This will throw as we already checked that the sizes are different.
      assert additionalFiles.isEmpty()
    }
  }
}
