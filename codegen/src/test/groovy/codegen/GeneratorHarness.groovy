package codegen

import de.rk42.openapi.codegen.Configuration
import de.rk42.openapi.codegen.OpenApiCodegen
import de.rk42.openapi.codegen.logging.Slf4jLogAdapter
import groovy.io.FileType
import org.slf4j.LoggerFactory

/**
 * Tests are being run with the project dir as the working directory, e.g. C:/Development/Workspace/OpenApiCodegen
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

  GeneratorHarness(String contract, String packageName, String modelPrefix = "") {
    this.contract = contract
    this.packageName = packageName
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
    def logAdapter = new Slf4jLogAdapter(LoggerFactory.getLogger("OpenApi-Generator.$packageName"))
    new OpenApiCodegen(logAdapter).generate(
        new Configuration(
            contract,
            "$packageName/openapi.yaml",
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
