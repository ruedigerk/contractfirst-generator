package codegen.testframework

import de.rk42.openapi.codegen.MainKt
import groovy.io.FileType

/**
 * Test are being run with the project dir as the working directory, e.g. C:/Development/Workspace/OpenApiCodegen
 */
class GeneratorHarness {

  static final String OUTPUT_DIR = "build/generatedTestOutput"

  final List<File> referenceFiles
  final List<File> generatedFiles
  
  private final String referenceDir
  private final String generatedDir
  private final String contract
  private final String packageName

  private generatorRan = false

  GeneratorHarness(String contract, String packageName) {
    this.contract = contract
    this.packageName = packageName
    referenceDir = "src/test/java/$packageName"
    generatedDir = "$OUTPUT_DIR/$packageName"

    def referenceFilePaths = discoverFiles(referenceDir)
    
    referenceFiles = referenceFilePaths.collect {new File(referenceDir, it) }
    generatedFiles = referenceFilePaths.collect {new File(generatedDir, it) }
    
    assert !referenceFilePaths.isEmpty()
  }

  def runGenerator() {
    if (!generatorRan) {
      generatorRan = true
      MainKt.main(["--contract", contract, "--output-dir", OUTPUT_DIR, "--package", packageName] as String[])
    }
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
