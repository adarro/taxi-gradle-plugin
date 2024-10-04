pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("org.jetbrains.kotlinx.kover.aggregation") version "0.8.3"
        id("com.gradle.develocity") version "3.18.1"
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            name = "OrbitalHQ"
            url = uri("https://repo.orbitalhq.com/release")
        }
    }

    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

plugins {
    id("com.gradle.develocity")
    id("org.jetbrains.kotlinx.kover.aggregation")
}

develocity {
    buildScan.termsOfUseUrl = "https://gradle.com/terms-of-service"
    buildScan.termsOfUseAgree = "yes"
    buildScan.publishing.onlyIf {
        System.getenv("GITHUB_ACTIONS") == "true" &&
            it.buildResult.failures.isNotEmpty()
    }
}

kover {
    enableCoverage()
}

rootProject.name = ("taxi-gradle-plugins")

include(":plugin-base", ":plugin-avro")
