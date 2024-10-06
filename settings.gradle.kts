pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven {
            // needed here for the plugin's dependencies to be found
            // see https://github.com/taxilang/taxilang/issues/3
            name = "OrbitalHQ"
            url = uri("https://repo.orbitalhq.com/release")
        }
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        google()
    }
}

plugins {
    id("com.gradle.develocity") version "3.18.1"
}

develocity {
    buildScan.termsOfUseUrl = "https://gradle.com/terms-of-service"
    buildScan.termsOfUseAgree = "yes"
    buildScan.publishing.onlyIf {
        System.getenv("GITHUB_ACTIONS") == "true" &&
            it.buildResult.failures.isNotEmpty()
    }
}

rootProject.name = "kotlin-gradle-plugin-template"

include(":example", ":exampleCustom", ":example-kotlin-plugin")
includeBuild("plugin-build")
