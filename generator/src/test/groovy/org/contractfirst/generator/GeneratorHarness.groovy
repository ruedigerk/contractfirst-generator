package org.contractfirst.generator

import groovy.io.FileType
import org.contractfirst.generator.Configuration
import org.contractfirst.generator.ContractfirstGenerator
import org.contractfirst.generator.GeneratorType
import org.contractfirst.generator.logging.Slf4jLogAdapter
import org.slf4j.LoggerFactory

/**
 * Harness for running the Contractfirst-Generator in tests to compare the generated code to some reference.
 * 
 * Note: tests are being run with the project directory as the working directory.
 */
class GeneratorHarness {

  static final String OUTPUT_DIR = "target/generatedTestOutput"

  final List<String> relativePathNames
  final List<File> referenceFiles
  final List<File> generatedFiles

  private final String referenceDir
  private final String generatedDir
  private final String contract
  private final String packageName
  private final String modelPrefix

  private generatorRan = false
  private final boolean generateServer

  GeneratorHarness(String contract, String packageName, boolean generateServer, String modelPrefix = "") {
    this.contract = contract
    this.packageName = packageName
    this.generateServer = generateServer
    this.modelPrefix = modelPrefix

    referenceDir = "src/test/java/$packageName"
    generatedDir = "$OUTPUT_DIR/$packageName"

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
    }
  }

  private void deleteOutputDirectory() {
    def outputDir = new File(generatedDir)

    if (outputDir.exists()) {
      outputDir.deleteDir()
    }
  }

  private executeGenerator() {
    def logAdapter = new Slf4jLogAdapter(LoggerFactory.getLogger("Contractfirst-Generator.$packageName"))
    new ContractfirstGenerator(logAdapter).generate(
        new Configuration(
            contract,
            "$packageName/openapi.yaml",
            generateServer ? GeneratorType.SERVER : GeneratorType.CLIENT,
            OUTPUT_DIR,
            true,
            packageName,
            modelPrefix
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
}
