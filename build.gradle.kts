val awssdkVersion = "2.31.66"
val postgresVersion = "42.7.9"
val flywayPostgresVersion = "11.20.3"
val jimmerVersion = "0.9.120"

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        // Defines the JDBC driver and Flyway extension for the Gradle build tool itself
        classpath("org.postgresql:postgresql:42.7.9")
        classpath("org.flywaydb:flyway-database-postgresql:11.20.3")
    }
}

plugins {
    id("io.micronaut.application") version "4.6.1"
    id("com.gradleup.shadow") version "8.3.9"
    id("io.micronaut.test-resources") version "4.6.1"
    id("io.micronaut.aot") version "4.6.1"
    id("org.flywaydb.flyway") version "11.20.3"
}

version = "1.0.0"
group = "com.doruk"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")
    annotationProcessor("io.micronaut.security:micronaut-security-annotations")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")

    // for jimmer and graalvm
    annotationProcessor("org.babyfish.jimmer:jimmer-apt:$jimmerVersion")
    annotationProcessor("io.micronaut:micronaut-graal")

    // --- BOMs (Dependency Management) ---
    implementation(platform("software.amazon.awssdk:bom:$awssdkVersion"))

    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-management")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.nats:micronaut-nats")
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.redis:micronaut-redis-lettuce")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.validation:micronaut-validation")

    // third party deps
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("io.micrometer:context-propagation")

    // Jimmer
    implementation("org.babyfish.jimmer:jimmer-sql:$jimmerVersion")

    // Utilities
    implementation("nl.basjes.parse.useragent:yauaa:7.32.0")
    implementation("net.coobird:thumbnailator:0.4.21")
    implementation("software.amazon.awssdk:s3")
    implementation("de.mkammerer:argon2-jvm:2.12")
    implementation("com.fasterxml.uuid:java-uuid-generator:5.2.0")
    // compression
    implementation("com.aayushatharva.brotli4j:brotli4j:1.20.0")
    runtimeOnly("com.aayushatharva.brotli4j:native-linux-x86_64:1.20.0")

    // logging
    runtimeOnly("ch.qos.logback:logback-classic")
    // Log4j 2 Bridge (Required for Yauaa to log via Logback)
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.25.3")
    implementation("org.apache.logging.log4j:log4j-api:2.25.3")

    // --- Compile Only / Provided ---
    compileOnly("io.micronaut.openapi:micronaut-openapi-annotations")
    compileOnly("org.projectlombok:lombok")

    compileOnly("io.micronaut.validation:micronaut-validation-processor")

    // --- Runtime Only ---
    runtimeOnly("org.flywaydb:flyway-database-postgresql:$flywayPostgresVersion")
    runtimeOnly("org.postgresql:postgresql:$postgresVersion")
    runtimeOnly("org.yaml:snakeyaml")
    // Native transport
    runtimeOnly("io.netty:netty-transport-native-io_uring::linux-x86_64")
    runtimeOnly("io.netty:netty-transport-native-epoll::linux-x86_64")

    developmentOnly("io.micronaut.controlpanel:micronaut-control-panel-management")
    developmentOnly("io.micronaut.controlpanel:micronaut-control-panel-ui")

    aotPlugins(platform("io.micronaut.platform:micronaut-platform:4.10.7"))
    aotPlugins("io.micronaut.security:micronaut-security-aot")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


application {
    mainClass = "com.doruk.Application"
}
java {
    sourceCompatibility = JavaVersion.toVersion("25")
    targetCompatibility = JavaVersion.toVersion("25")
}

// Logic to replicate Maven Profiles:
// By default, don't generate docs. If `-Pdocs` is passed to gradle, enable them.
val generateDocs = project.hasProperty("docs")

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf(
        "-Amicronaut.processing.group=com.doruk",
        "-Amicronaut.processing.module=microsample"
    ))

    if (generateDocs) {
        options.compilerArgs.add("-Amicronaut.openapi.views.spec=swagger-ui.enabled=true,swagger-ui.theme=material")
    }
}

// --- Flyway Configuration
flyway {
    url = "jdbc:postgresql://localhost:5432/microsample"
    user = "doruk"
    password = "dorukdb"
    schemas = arrayOf("sample")
    locations = arrayOf("filesystem:src/main/resources/db/migration")
}

graalvmNative.toolchainDetection = false

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.doruk.*")
    }
    testResources {
        enabled = false
        additionalModules.add("jdbc-postgresql")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
        configurationProperties.put("micronaut.security.jwks.enabled","false")
    }
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "25"
}


