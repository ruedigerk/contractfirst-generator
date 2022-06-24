package io.github.ruedigerk.contractfirst.generator

import groovy.io.FileType
import io.github.ruedigerk.contractfirst.generator.logging.Slf4jLogAdapter
import org.slf4j.LoggerFactory

/**
 * Harness for running the Contractfirst-Generator in tests to compare the generated code to some reference.
 */
class GeneratorHarness {

  private static final String NO_REFERENCE_FILES = "NO_REFERENCE_FILES"

  static {
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info")
  }

  // Tests are being run with the maven module directory as the working directory.
  private static final String OUTPUT_DIR = "target/generatedTestOutput"

  final List<String> relativePathNames
  final List<File> referenceFiles
  final List<File> generatedFiles

  private final String referenceDir
  private final String generatedDir
  private final String inputContractFile
  private final String outputJavaBasePackage
  private final String outputJavaModelNamePrefix
  private final boolean outputJavaModelUseJsr305NullabilityAnnotations
  private final GeneratorType generatorType

  private generatorRan = false

  GeneratorHarness(String inputContractFile, String outputJavaBasePackage, GeneratorType generatorType, String outputJavaModelNamePrefix = "", boolean outputJavaModelUseJsr305NullabilityAnnotations = false) {
    this.inputContractFile = inputContractFile
    this.outputJavaBasePackage = outputJavaBasePackage
    this.generatorType = generatorType
    this.outputJavaModelNamePrefix = outputJavaModelNamePrefix
    this.outputJavaModelUseJsr305NullabilityAnnotations = outputJavaModelUseJsr305NullabilityAnnotations

    referenceDir = "src/test/java/$outputJavaBasePackage"
    generatedDir = "$OUTPUT_DIR/$outputJavaBasePackage"

    relativePathNames = discoverFiles(referenceDir)

    referenceFiles = relativePathNames.collect {
      new File(referenceDir, it)
    }
    generatedFiles = relativePathNames.collect {
      new File(generatedDir, it)
    }
  }

  def runGenerator() {
    if (!generatorRan) {
      generatorRan = true

      deleteOutputDirectory()
      executeGenerator()

      assert relativePathNames != [NO_REFERENCE_FILES]: "There are no reference files in $referenceDir"
      
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
            generatorType,
            OUTPUT_DIR,
            true,
            "$outputJavaBasePackage/openapi.yaml",
            outputJavaBasePackage,
            outputJavaModelNamePrefix,
            outputJavaModelUseJsr305NullabilityAnnotations
        )
    )
  }

  private static List<String> discoverFiles(String dir) {
    def directory = new File(dir)

    List<String> result = []
    int prefixLength = dir.length()

    if (directory.exists()) {
      directory.eachFileRecurse(FileType.FILES) {
        def relativePath = it.toString().substring(prefixLength)
        result.add(relativePath)
      }
    }

    if (result.isEmpty()) {
      return [NO_REFERENCE_FILES]
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
