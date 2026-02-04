package io.github.ruedigerk.contractfirst.generator.mavenplugin;

import com.google.common.base.Throwables;
import io.github.ruedigerk.contractfirst.generator.ContractfirstGenerator;
import io.github.ruedigerk.contractfirst.generator.NotSupportedException;
import io.github.ruedigerk.contractfirst.generator.ParserException;
import io.github.ruedigerk.contractfirst.generator.configuration.Configuration;
import io.github.ruedigerk.contractfirst.generator.configuration.GeneratorType;
import io.github.ruedigerk.contractfirst.generator.configuration.GeneratorVariant;
import io.github.ruedigerk.contractfirst.generator.configuration.InvalidConfigurationException;
import io.github.ruedigerk.contractfirst.generator.configuration.ModelVariant;
import io.github.ruedigerk.contractfirst.generator.logging.LogAdapter;
import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Goal for generating sources from an OpenAPI contract.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
@SuppressWarnings("FieldMayBeFinal")
public class CodeGeneratorMojo extends AbstractMojo {

  // See https://developer.okta.com/blog/2019/09/23/tutorial-build-a-maven-plugin
  // See https://medium.com/swlh/step-by-step-guide-to-developing-a-custom-maven-plugin-b6e3a0e09966

  /**
   * The path to the file containing the OpenAPI contract to use as input; in case of the model-only generator, this should point to a single JSON-Schema file
   * in YAML or JSON format, or to a directory which is recursively searched for JSON-Schema files
   */
  @Parameter(name = "inputContractFile", property = "openapi.generator.maven.plugin.inputContractFile", required = true)
  private String inputContractFile;

  /**
   * The type of generator to use for code generation; allowed values are: "server", "client", "model-only"
   */
  @Parameter(name = "generator", property = "openapi.generator.maven.plugin.generator", required = true)
  private String generator;

  /**
   * The variant of the generator to use for code generation; allowed values depend on the selected generator:
   * <pre>
   *   - server generator: "jax-rs" (default), "spring-web"
   *   - client generator: "okhttp" (default)
   *   - model-only generator: "model-only" (default)
   * </pre>
   */
  @Parameter(name = "generatorVariant", property = "openapi.generator.maven.plugin.generatorVariant", required = false)
  private String generatorVariant;

  /**
   * The variant of the model to use for code generation; allowed values are: "gson", "jackson"
   */
  @Parameter(name = "modelVariant", property = "openapi.generator.maven.plugin.modelVariant", required = false, defaultValue = "gson")
  private String modelVariant;

  /**
   * The target directory for writing the generated sources to
   */
  @Parameter(
      name = "outputDir",
      property = "openapi.generator.maven.plugin.outputDir",
      defaultValue = "${project.build.directory}/generated-sources/contractfirst-generator"
  )
  private String outputDir;

  /**
   * Whether to output the parsed contract as an all-in-one contract
   */
  @Parameter(name = "outputContract", property = "openapi.generator.maven.plugin.outputContract", defaultValue = "false")
  private boolean outputContract = false;

  /**
   * The file name of the all-in-one contract file to output; only used when outputContract is true
   */
  @Parameter(name = "outputContractFile", property = "openapi.generator.maven.plugin.outputContractFile", defaultValue = "openapi.yaml")
  private String outputContractFile;

  /**
   * The Java package to put generated classes into
   */
  @Parameter(name = "outputJavaBasePackage", property = "openapi.generator.maven.plugin.outputJavaBasePackage", required = true)
  private String outputJavaBasePackage;

  /**
   * whether the Java packages of the generated model files are mirroring the directory structure of the schema files
   */
  @Parameter(name = "outputJavaPackageMirrorsSchemaDirectory", property = "openapi.generator.maven.plugin.outputJavaPackageMirrorsSchemaDirectory", defaultValue = "false")
  private boolean outputJavaPackageMirrorsSchemaDirectory = false;

  /**
   * The path prefix to cut from the schema file directories when determining Java packages for model files; defaults to the directory of the inputContractFile;
   * this is only used, when outputJavaPackageMirrorsSchemaDirectory is true
   */
  @Parameter(name = "outputJavaPackageSchemaDirectoryPrefix", property = "openapi.generator.maven.plugin.outputJavaPackageSchemaDirectoryPrefix")
  private String outputJavaPackageSchemaDirectoryPrefix;

  /**
   * The prefix for Java model class names; defaults to the empty String
   */
  @Parameter(name = "outputJavaModelNamePrefix", property = "openapi.generator.maven.plugin.outputJavaModelNamePrefix")
  private String outputJavaModelNamePrefix = "";

  /**
   * Whether to generate JSR-305 nullability annotations for the getter and setter methods of the model classes
   */
  @Parameter(name = "outputJavaModelUseJsr305NullabilityAnnotations", property = "openapi.generator.maven.plugin.outputJavaModelUseJsr305NullabilityAnnotations", defaultValue = "false")
  private boolean outputJavaModelUseJsr305NullabilityAnnotations = false;

  /**
   * Whether to add the generated sources directory as a test source directory instead of a main compile source directory; defaults to false
   */
  @Parameter(name = "addAsTestSource", property = "openapi.generator.maven.plugin.add-as-test-source", defaultValue = "false")
  private boolean addAsTestSource = false;

  /**
   * Skip execution of this plugin; defaults to false
   */
  @Parameter(name = "skip", property = "openapi.generator.maven.plugin.skip", defaultValue = "false")
  private boolean skip = false;

  /**
   * Dependency on the Maven project instance for adding the generated sources root.
   */
  @Parameter(defaultValue = "${project}", readonly = true)
  private MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    if (skip) {
      getLog().info("Skipped execution");
      return;
    }

    getLog().debug(getConfigurationAsString());

    Configuration config = determineConfiguration();

    getLog().info("Running code generation for contract '" + config.getInputContractFile() + "'");

    addGeneratedSourcesRoot(config);
    runGenerator(config);
  }

  private String getConfigurationAsString() {
    return "Mojo Configuration:" +
        "\n\tinputContractFile='" + inputContractFile + '\'' +
        "\n\tgenerator='" + generator + '\'' +
        "\n\tgeneratorVariant='" + generatorVariant + '\'' +
        "\n\tmodelVariant='" + modelVariant + '\'' +
        "\n\toutputDir='" + outputDir + '\'' +
        "\n\toutputContract=" + outputContract +
        "\n\toutputContractFile='" + outputContractFile + '\'' +
        "\n\toutputJavaBasePackage='" + outputJavaBasePackage + '\'' +
        "\n\toutputJavaPackageMirrorsSchemaDirectory='" + outputJavaPackageMirrorsSchemaDirectory + '\'' +
        "\n\toutputJavaPackageSchemaDirectoryPrefix='" + outputJavaPackageSchemaDirectoryPrefix + '\'' +
        "\n\toutputJavaModelNamePrefix='" + outputJavaModelNamePrefix + '\'' +
        "\n\toutputJavaModelUseJsr305NullabilityAnnotations='" + outputJavaModelUseJsr305NullabilityAnnotations + '\'' +
        "\n\tskip=" + skip +
        "\n\tproject=" + project;
  }

  private Configuration determineConfiguration() throws MojoExecutionException {
    String effectiveInputContractFile = makeAbsolutePath(inputContractFile);
    String effectiveOutputJavaPackageSchemaDirectoryPrefix = determineOutputJavaPackageSchemaDirectoryPrefix(effectiveInputContractFile);

    GeneratorType generatorType = determineGenerator();
    GeneratorVariant generatorVariantType = determineGeneratorVariant(generatorType);
    ModelVariant modelVariantType = determineModelVariant();

    return new Configuration(
        effectiveInputContractFile,
        generatorType,
        generatorVariantType,
        modelVariantType,
        outputDir,
        outputContract,
        outputContractFile,
        outputJavaBasePackage,
        outputJavaPackageMirrorsSchemaDirectory,
        effectiveOutputJavaPackageSchemaDirectoryPrefix,
        outputJavaModelNamePrefix,
        outputJavaModelUseJsr305NullabilityAnnotations
    );
  }

  private String makeAbsolutePath(String path) throws MojoExecutionException {
    try {
      File file = new File(path);
      if (file.isAbsolute()) {
        return file.getCanonicalPath();
      } else {
        return new File(project.getBasedir(), path).getCanonicalPath();
      }
    } catch (IOException e) {
      throw new MojoExecutionException("Error canonicalizing path: " + e.getMessage(), e);
    }
  }

  private String determineOutputJavaPackageSchemaDirectoryPrefix(String effectiveInputContractFile) throws MojoExecutionException {
    if (outputJavaPackageSchemaDirectoryPrefix == null) {
      File inputFile = new File(effectiveInputContractFile);
      if (inputFile.isDirectory()) {
        return inputFile.getPath();
      } else {
        return inputFile.getParent();
      }
    } else {
      return makeAbsolutePath(outputJavaPackageSchemaDirectoryPrefix);
    }
  }

  private GeneratorType determineGenerator() throws MojoExecutionException {
    switch (generator) {
      case "client":
        return GeneratorType.CLIENT;
      case "server":
        return GeneratorType.SERVER;
      case "model-only":
        return GeneratorType.MODEL_ONLY;
      default:
        throw new MojoExecutionException("Configuration 'generator' has invalid value: '"
            + generator
            + "', allowed values are client, server, model-only.");
    }
  }

  private GeneratorVariant determineGeneratorVariant(GeneratorType generatorType) throws MojoExecutionException {
    if (generatorVariant == null) {
      return generatorType.getDefaultVariant();
    }

    switch (generatorVariant) {
      case "okhttp":
        return GeneratorVariant.CLIENT_OKHTTP;
      case "jax-rs":
        return GeneratorVariant.SERVER_JAX_RS;
      case "spring-web":
        return GeneratorVariant.SERVER_SPRING_WEB;
      case "model-only":
        return GeneratorVariant.MODEL_ONLY;
      default:
        throw new MojoExecutionException("Configuration 'generatorVariant' has invalid value: '"
            + generatorVariant
            + "', allowed values are okhttp, jax-rs, spring-web, model-only.");
    }
  }

  private ModelVariant determineModelVariant() throws MojoExecutionException {
    if (modelVariant == null) {
      return ModelVariant.GSON;
    }

    switch (modelVariant) {
      case "gson":
        return ModelVariant.GSON;
      case "jackson":
        return ModelVariant.JACKSON;
      default:
        throw new MojoExecutionException("Configuration 'modelVariant' has invalid value: '"
            + modelVariant
            + "', allowed values are gson, jackson.");
    }
  }

  private void addGeneratedSourcesRoot(Configuration config) {
    if (addAsTestSource) {
      project.addTestCompileSourceRoot(config.getOutputDir());
    } else {
      project.addCompileSourceRoot(config.getOutputDir());
    }
  }

  private void runGenerator(Configuration config) throws MojoFailureException {
    try {
      LogAdapter logAdapter = new MavenLogAdapter(getLog());
      new ContractfirstGenerator(logAdapter).generate(config);
    } catch (ParserException e) {
      throw new MojoFailureException("Could not parse contract: " + e.getMessage());
    } catch (NotSupportedException e) {
      throw new MojoFailureException("Contract contains usage of unsupported feature: " + e.getMessage());
    } catch (InvalidConfigurationException e) {
      throw new MojoFailureException("Invalid configuration " + e.getMessage());
    } catch (Exception e) {
      getLog().error("Generator failed with an unexpected exception: " + Throwables.getStackTraceAsString(e));
      throw new MojoFailureException("Generator failed with an unexpected exception: " + e);
    }
  }
}
