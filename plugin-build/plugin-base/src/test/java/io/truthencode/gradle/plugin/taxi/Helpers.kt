package io.truthencode.gradle.plugin.taxi

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File

enum class DslLanguages {
    Kotlin,
    Groovy,
}

const val BASE_TAXI_PLUGIN_ID = "io.truthencode.taxi"

fun generateBuildFile(
    config: String,
    languages: DslLanguages = DslLanguages.Kotlin,
): String =
    when (languages) {
        DslLanguages.Kotlin ->
            """
            plugins {
                id("$BASE_TAXI_PLUGIN_ID")
            }
            taxi {
              $config
            }
            """.trimIndent()

        DslLanguages.Groovy ->
            """
            plugins {
                id '$BASE_TAXI_PLUGIN_ID'
            }
            taxi {
                $config
            }
            """.trimIndent()
    }

fun File.removeRecursively() =
    this
        .walkBottomUp()
        .filter { it != this }
        .forEach { it.deleteRecursively() }

operator fun File.div(s: String): File = this.resolve(s)

fun executeGradleRun(
    task: String,
    projDir: File,
): BuildResult =
    GradleRunner
        .create()
        .withProjectDir(projDir)
        .withArguments(task)
        .withPluginClasspath()
        .build()
