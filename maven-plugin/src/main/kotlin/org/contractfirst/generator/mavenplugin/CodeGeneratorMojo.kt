package org.contractfirst.generator.mavenplugin

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.contractfirst.generator.Configuration
import org.contractfirst.generator.ContractfirstGenerator
import org.contractfirst.generator.GeneratorType
import org.contractfirst.generator.NotSupportedException
import org.contractfirst.generator.parser.ParserException

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
  @Parameter(name = "inputContractFile", property = "openapi.generator.maven.plugin.inputContractFile", required = true)
  private var inputContractFile: String? = null

  /**
   * the type of generator to use for code generation; allowed values are: "server", "client"
   */
  @Parameter(name = "generator", property = "openapi.generator.maven.plugin.generator", required = true)
  private var generator: String? = null

  /**
   * the target directory for writing the generated sources to
   */
  @Parameter(
      name = "outputDir",
      property = "openapi.generator.maven.plugin.outputDir",
      defaultValue = "\${project.build.directory}/generated-sources/contractfirst-generator"
  )
  private var outputDir: String? = null

  /**
   * whether to output the parsed contract as an all-in-one contract
   */
  @Parameter(name = "outputContract", property = "openapi.generator.maven.plugin.outputContract", defaultValue = "false")
  private var outputContract: Boolean = false

  /**
   * the file name of the all-in-one contract file to output; only used when outputContract is true
   */
  @Parameter(name = "outputContractFile", property = "openapi.generator.maven.plugin.outputContractFile", defaultValue = "openapi.yaml")
  private var outputContractFile: String? = null

  /**
   * the Java package to put generated classes into
   */
  @Parameter(name = "outputJavaBasePackage", property = "openapi.generator.maven.plugin.outputJavaBasePackage", required = true)
  private var outputJavaBasePackage: String? = null

  /**
   * the prefix for file names of the Java model
   */
  @Parameter(name = "outputJavaNamePrefix", property = "openapi.generator.maven.plugin.outputJavaNamePrefix", defaultValue = "")
  private var outputJavaNamePrefix: String? = null

  /**
   * skip execution of this plugin
   */
  @Parameter(name = "skip", property = "openapi.generator.maven.plugin.skip", defaultValue = "false")
  private var skip: Boolean = false

  /**
   * For adding the generated sources root.
   */
  @Parameter(defaultValue = "\${project}", readonly = true)
  private val project: MavenProject? = null

  @Throws(MojoExecutionException::class, MojoFailureException::class)
  override fun execute() {
    if (skip) {
      log.info("Skipped execution")
      return
    }

    val config = determineConfiguration()

    log.info("Running code generation for contract '${config.inputContractFile}'...")

    addGeneratedSourcesRoot(config)
    runGenerator(config)
  }

  @Throws(MojoExecutionException::class)
  private fun determineConfiguration(): Configuration {
    return Configuration(
        inputContractFile.require(),
        determineGenerator(),
        outputDir.require(),
        outputContract,
        outputContractFile.require(),
        outputJavaBasePackage.require(),
        outputJavaNamePrefix.require()
    )
  }

  private fun determineGenerator(): GeneratorType = when (generator) {
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
      ContractfirstGenerator(logAdapter).generate(config)
    } catch (e: ParserException) {
      throw MojoFailureException("Could not parse contract: ${e.messages.joinToString("\n")}")
    } catch (e: NotSupportedException) {
      throw MojoFailureException("Contract contains usage of unsupported feature: ${e.message}")
    }
  }

  @Throws(MojoExecutionException::class)
  private fun <T> T?.require(): T = this ?: throw MojoExecutionException("Mojo not properly initialized, parameter is null")
}
