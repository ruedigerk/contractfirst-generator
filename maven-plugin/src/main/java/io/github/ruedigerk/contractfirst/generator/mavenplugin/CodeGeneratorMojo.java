package io.github.ruedigerk.contractfirst.generator.mavenplugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import io.github.ruedigerk.contractfirst.generator.Configuration;
import io.github.ruedigerk.contractfirst.generator.ContractfirstGenerator;
import io.github.ruedigerk.contractfirst.generator.GeneratorType;
import io.github.ruedigerk.contractfirst.generator.NotSupportedException;
import io.github.ruedigerk.contractfirst.generator.logging.LogAdapter;
import io.github.ruedigerk.contractfirst.generator.parser.ParserException;

/**
 * Goal for generating sources from an OpenAPI contract.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class CodeGeneratorMojo extends AbstractMojo {

  // See https://developer.okta.com/blog/2019/09/23/tutorial-build-a-maven-plugin
  // See https://medium.com/swlh/step-by-step-guide-to-developing-a-custom-maven-plugin-b6e3a0e09966

  /**
   * the path to the file containing the OpenAPI contract to use as input
   */
  @Parameter(name = "inputContractFile", property = "openapi.generator.maven.plugin.inputContractFile", required = true)
  private String inputContractFile;

  /**
   * the type of generator to use for code generation; allowed values are: "server", "client"
   */
  @Parameter(name = "generator", property = "openapi.generator.maven.plugin.generator", required = true)
  private String generator;

  /**
   * the target directory for writing the generated sources to
   */
  @Parameter(
      name = "outputDir",
      property = "openapi.generator.maven.plugin.outputDir",
      defaultValue = "${project.build.directory}/generated-sources/contractfirst-generator"
  )
  private String outputDir;

  /**
   * whether to output the parsed contract as an all-in-one contract
   */
  @Parameter(name = "outputContract", property = "openapi.generator.maven.plugin.outputContract", defaultValue = "false")
  private boolean outputContract = false;

  /**
   * the file name of the all-in-one contract file to output; only used when outputContract is true
   */
  @Parameter(name = "outputContractFile", property = "openapi.generator.maven.plugin.outputContractFile", defaultValue = "openapi.yaml")
  private String outputContractFile;

  /**
   * the Java package to put generated classes into
   */
  @Parameter(name = "outputJavaBasePackage", property = "openapi.generator.maven.plugin.outputJavaBasePackage", required = true)
  private String outputJavaBasePackage;

  /**
   * the prefix for file names of the Java model
   */
  @Parameter(name = "outputJavaNamePrefix", property = "openapi.generator.maven.plugin.outputJavaNamePrefix", defaultValue = "")
  private String outputJavaNamePrefix;

  /**
   * skip execution of this plugin
   */
  @Parameter(name = "skip", property = "openapi.generator.maven.plugin.skip", defaultValue = "false")
  private boolean skip = false;

  /**
   * For adding the generated sources root.
   */
  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (skip) {
      getLog().info("Skipped execution");
      return;
    }

    Configuration config = determineConfiguration();

    getLog().info("Running code generation for contract '" + config.getInputContractFile() + "'...");

    addGeneratedSourcesRoot(config);
    runGenerator(config);
  }

  private Configuration determineConfiguration() throws MojoExecutionException {
    return new Configuration(
        require(inputContractFile),
        determineGenerator(),
        require(outputDir),
        outputContract,
        require(outputContractFile),
        require(outputJavaBasePackage),
        require(outputJavaNamePrefix)
    );
  }

  private GeneratorType determineGenerator() throws MojoExecutionException {
    switch (generator) {
      case "client":
        return GeneratorType.CLIENT;
      case "server":
        return GeneratorType.SERVER;
      default:
        throw new MojoExecutionException("Configuration 'generator' has invalid value: '" + generator + "', allowed values are 'client', 'server'.");
    }
  }

  private void addGeneratedSourcesRoot(Configuration config) throws MojoExecutionException {
    require(project).addCompileSourceRoot(config.getOutputDir());
  }

  private void runGenerator(Configuration config) throws MojoFailureException {
    try {
      LogAdapter logAdapter = new MavenLogAdapter(getLog());
      new ContractfirstGenerator(logAdapter).generate(config);
    } catch (ParserException e) {
      throw new MojoFailureException("Could not parse contract: " + String.join("\n", e.getMessages()));
    } catch (NotSupportedException e) {
      throw new MojoFailureException("Contract contains usage of unsupported feature: " + e.getMessage());
    }
  }

  private <T> T require(T input) throws MojoExecutionException {
    if (input == null) {
      throw new MojoExecutionException("Mojo not properly initialized, parameter is null");
    }

    return input;
  }
}
