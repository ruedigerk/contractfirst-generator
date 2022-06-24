package io.github.ruedigerk.contractfirst.generator.java.generator

import io.github.ruedigerk.contractfirst.generator.java.JavaConfiguration
import java.io.BufferedOutputStream
import java.io.File
import java.io.InputStream

/**
 * Used for writing template source files. Template source files are static resource files that get added a package statement, when being written.
 */
class TemplateFileWriter(private val configuration: JavaConfiguration) {

  fun writeTemplateFile(destinationPackage: String, templateFileName: String) {
    val packageAsDirectory = destinationPackage.replace('.', '/')
    val destinationDirectory = File(configuration.outputDir).resolve(packageAsDirectory)
    val templateInputStream = loadResource("/io/github/ruedigerk/contractfirst/generator/templates/${templateFileName}")
    
    destinationDirectory.mkdirs()

    destinationDirectory.resolve(templateFileName).outputStream().buffered().use { outputStream ->
      outputStream.writePackageStatement(destinationPackage)
      templateInputStream.use { it.copyTo(outputStream) }
    }
  }

  private fun loadResource(location: String): InputStream = javaClass.getResourceAsStream(location)
      ?: throw IllegalStateException("Resource file $location not found")

  private fun BufferedOutputStream.writePackageStatement(destinationPackage: String) = with(this.writer()) {
    write("package $destinationPackage;\n\n")
    flush()
  }
}