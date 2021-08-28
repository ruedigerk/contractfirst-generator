package de.rk42.openapi.codegen.mavenplugin

import de.rk42.openapi.codegen.Configuration
import de.rk42.openapi.codegen.GeneratorType
import de.rk42.openapi.codegen.NotSupportedException
import de.rk42.openapi.codegen.OpenApiCodegen
import de.rk42.openapi.codegen.parser.ParserException
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject

/**
 * Goal for generating sources from an OpenAPI contract.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
class CodeGeneratorMojo : AbstractMojo() {

  // See https://developer.okta.com/blog/2019/09/23/tutorial-build-a-maven-plugin
  // See https://medium.com/swlh/step-by-step-guide-to-developing-a-custom-maven-plugin-b6e3a0e09966

  /**
   * the path to the file containing the OpenAPI contract to use as input
   */
  @Parameter(name = "contractFile", property = "openapi.codegen.maven.plugin.contractFile", required = true)
  private var contractFile: String? = null

  /**
   * the target directory for writing the generated sources to
   */
  @Parameter(
      name = "outputDir",
      property = "openapi.codegen.maven.plugin.outputDir",
      defaultValue = "\${project.build.directory}/generated-sources/openapi-codegen"
  )
  private var outputDir: String? = null

  /**
   * the Java package to put generated classes into
   */
  @Parameter(name = "sourcePackage", property = "openapi.codegen.maven.plugin.sourcePackage", required = true)
  private var sourcePackage: String? = null

  /**
   * whether to output the parsed contract as an all-in-one contract
   */
  @Parameter(name = "outputContract", property = "openapi.codegen.maven.plugin.outputContract", defaultValue = "false")
  private var outputContract: Boolean = false

  /**
   * the file name of the all-in-one contract file to output; only used when outputContract is true
   */
  @Parameter(name = "contractOutputFile", property = "openapi.codegen.maven.plugin.contractOutputFile", defaultValue = "openapi.yaml")
  private var contractOutputFile: String? = null

  /**
   * the type of generator to use for code generation; allowed values are: "server", "client"
   */
  @Parameter(name = "generator", property = "openapi.codegen.maven.plugin.contractOutputFile", required = true)
  private var generator: String? = null

  /**
   * The prefix for model file names
   */
  @Parameter(name = "modelPrefix", property = "openapi.codegen.maven.plugin.modelPrefix", defaultValue = "")
  private var modelPrefix: String? = null

  /**
   * skip execution of this plugin
   */
  @Parameter(name = "skip", property = "openapi.codegen.maven.plugin.skip", defaultValue = "false")
  private var skip: Boolean = false

  /**
   * For adding the generated sources root.
   */
  @Parameter(defaultValue = "\${project}")
  private val project: MavenProject? = null

  @Throws(MojoExecutionException::class, MojoFailureException::class)
  override fun execute() {
    if (skip) {
      log.info("Skipped execution")
      return
    }

    val config = determineConfiguration()

    log.info("Generating code for contract '${config.contractFile}' in output directory '${config.outputDir}', package '${config.sourcePackage}'")

    addGeneratedSourcesRoot(config)
    runGenerator(config)
  }

  @Throws(MojoExecutionException::class)
  private fun determineConfiguration(): Configuration {
    return Configuration(
        contractFile.require(),
        contractOutputFile.require(),
        determineGenerator(),
        outputDir.require(),
        outputContract,
        sourcePackage.require(),
        modelPrefix.require()
    )
  }

  private fun determineGenerator(): GeneratorType = when(generator) {
    "client" -> GeneratorType.CLIENT
    "server" -> GeneratorType.SERVER
    else -> throw MojoExecutionException("Configuration 'generator' has invalid value: '$generator', allowed values are 'client', 'server'.")
  }

  private fun addGeneratedSourcesRoot(config: Configuration) {
    project.require().addCompileSourceRoot(config.outputDir)
  }

  @Throws(MojoFailureException::class)
  private fun runGenerator(config: Configuration) {
    try {
      val logAdapter = MavenLogAdapter(log)
      OpenApiCodegen(logAdapter).generate(config)
    } catch (e: ParserException) {
      throw MojoFailureException("Could not parse contract: ${e.messages.joinToString("\n")}")
    } catch (e: NotSupportedException) {
      throw MojoFailureException("Contract contains usage of unsupported feature: ${e.message}")
    }
  }

  @Throws(MojoExecutionException::class)
  private fun <T> T?.require(): T = this ?: throw MojoExecutionException("Mojo not properly initialized, parameter is null")
}
