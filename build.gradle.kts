import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val generatedTestSourcesDir = "${buildDir}/generatedTestSources"

plugins {
  // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM, see https://kotlinlang.org/docs/reference/using-gradle.html
  kotlin("jvm").version("1.5.10")

  // Apply the groovy plugin to add support for Groovy/Spock
  groovy
}

repositories {
  mavenCentral()
}

dependencies {
  // Use the Kotlin JDK 8 standard library.
  implementation(kotlin("stdlib-jdk8"))

  // For reading command line arguments
  implementation("com.xenomachina:kotlin-argparser:2.0.7")
  
  // For parsing OpenAPI specifications 
  implementation("io.swagger.parser.v3:swagger-parser:2.0.25")
  
  // For serializing YAML output
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.3")
  
  // For generating Java source files
  implementation("com.squareup:javapoet:1.13.0")
  
  // For Logging
  implementation("ch.qos.logback:logback-classic:1.2.3")

  // Testing with Groovy/Spock
  testImplementation("org.spockframework:spock-core:2.0-groovy-3.0")
  testImplementation("org.codehaus.groovy:groovy:3.0.8")
  
  // Dependencies of the generated Code
  testImplementation("javax.ws.rs:javax.ws.rs-api:2.1.1")
  testImplementation("javax.validation:validation-api:1.1.0.Final")
  testImplementation("com.google.code.gson:gson:2.8.7")

  // Allows mocking of classes in Spock (in addition to interfaces).
  testRuntimeOnly("net.bytebuddy:byte-buddy:1.11.0")
  
  // Allows mocking of classes without default constructor in Spock (together with ByteBuddy).
  testRuntimeOnly("org.objenesis:objenesis:3.2")
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
  kotlinOptions.freeCompilerArgs = listOf("-progressive")
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}

// Workaround for Bug in IntelliJ Kotlin Plugin and Gradle 7
tasks.withType<org.gradle.jvm.tasks.Jar>{
  duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// For now, disable generating test output into generated sources.
//
//sourceSets {
//  test {
//    java {
//      // Add the generated test sources directory to the "java test" source set
//      srcDir(generatedTestSourcesDir)
//    }
//  }
//}
//
//// This task uses the generator itself to generate the test sources, supports up-to-date checking
//tasks.register<JavaExec>("generateTestSources") {
//
//  val contractSourcesDir = "src/test/contract"
//
//  inputs.files(fileTree(contractSourcesDir))
//  inputs.property("generatedTestSourcesDir", generatedTestSourcesDir)
//  outputs.dir(generatedTestSourcesDir)
//
//  classpath = sourceSets.main.get().runtimeClasspath
//  main = "de.rk42.openapi.codegen.MainKt"
//
//  args = listOf("--contract", "$contractSourcesDir/petstore-simple.yaml", "--output-dir", generatedTestSourcesDir, "--package", "generated")
//}
//
//// Make sure to generate the test sources before test compilation
//tasks.named<KotlinCompile>("compileTestKotlin").configure {
//  dependsOn.add(tasks.named("generateTestSources"))
//}

tasks.wrapper {
  gradleVersion = "7.1"
  distributionType = Wrapper.DistributionType.ALL
}
