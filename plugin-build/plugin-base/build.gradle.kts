plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    `jvm-test-suite`
    alias(libs.plugins.pluginPublish)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.dokka)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())

    implementation(libs.jackson.kotlin)

    // validation
    implementation(libs.konform.validation.jvm)
    // taxilang dependencies
//    implementation(libs.taxi.compiler)
    implementation(libs.logback.classic) {
        because("taxi-compiler depends on logback")
        version {
            strictly("[1.4.13,1.5.8[")
        }
    }
    implementation(libs.logback.core)
    implementation(libs.slf4j)
    implementation(libs.kotlin.reflect)
    implementation(libs.taxi.compiler) {
        exclude(module = "ch.qos.logback:logback-classic")
        // https://cve.mitre.org/cgi-bin/cvename.cgi?name=CVE-2023-45960
    }
    implementation(libs.taxi.packages) {
        because("contains the taxi conf classes")
    }
    implementation(libs.taxi.cli)
    // HOCON dependencies to read the taxi.conf file
    implementation(libs.lightbend.config)
    implementation(libs.hoplite)
    implementation(libs.hoplite.hocon)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.hocon)
    implementation(libs.hocon4k)
    // https://mvnrepository.com/artifact/org.junit/junit-bom
//    testImplementation(enforcedPlatform("org.junit:junit-bom:5.11.1"))
//    testImplementation(enforcedPlatform(libs.junit.bom))
//    testImplementation(libs.junit.jupiter.engine)

//    implementation(libs.lightbend.config)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
    }
}

gradlePlugin {
    plugins {
        create(property("ID").toString()) {
            id = property("ID").toString()
            implementationClass = property("IMPLEMENTATION_CLASS").toString()
            version = property("VERSION").toString()
            description = property("DESCRIPTION").toString()
            displayName = property("DISPLAY_NAME").toString()
            // Note: tags cannot include "plugin" or "gradle" when publishing
            @Suppress("UnstableApiUsage")
            tags.set(listOf("taxi", "taxilang"))
        }
    }
}

gradlePlugin {
    website.set(property("WEBSITE").toString())
    vcsUrl.set(property("VCS_URL").toString())
}

dokka {
    moduleName.set("TaxiPluginBase")
}
// Use Detekt with type resolution for check
tasks.named("check").configure {
    this.setDependsOn(
        this.dependsOn.filterNot {
            it is TaskProvider<*> && it.name == "detekt"
        } + tasks.named("detektMain"),
    )
}

tasks.register("setupPluginUploadFromEnvironment") {
    group = "publishing"
    description = "sets up and verifies the Gradle Properties necessary for uploading the plugin to the repository"
    doLast {
        val key = System.getenv("GRADLE_PUBLISH_KEY")
        val secret = System.getenv("GRADLE_PUBLISH_SECRET")

        if (key == null || secret == null) {
            throw GradleException("gradlePublishKey and/or gradlePublishSecret are not defined environment variables")
        }

        System.setProperty("gradle.publish.key", key)
        System.setProperty("gradle.publish.secret", secret)
    }
}

testing {
    suites {
        @Suppress("UnstableApiUsage")
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {

//                implementation("org.junit:junit-bom:5.11.1")
                implementation(enforcedPlatform(libs.junit.bom))
                implementation(libs.junit.jupiter.engine)
                implementation(libs.hoplite.json)
//                implementation(libs.junit.bom)
                implementation(libs.kotest.runner.junit.jvm)
                implementation(libs.hamcrest)
                implementation(libs.json.assert)
            }
        }
    }
}
