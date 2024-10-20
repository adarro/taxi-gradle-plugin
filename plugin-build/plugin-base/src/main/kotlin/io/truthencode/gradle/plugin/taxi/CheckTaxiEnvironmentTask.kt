package io.truthencode.gradle.plugin.taxi

import arrow.core.Either
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

/**
 * Checks the taxi environment for some common issues.
 * Perhaps this can be enhanced with a validation model.
 * [Arrow Validation](https://arrow-kt.io/learn/typed-errors/either-and-ior/)
 */
open class CheckTaxiEnvironmentTask : DefaultTask() {
    init {
        group = "taxi"
        description = "Checks the taxi environment"
        dependsOn(project.tasks.withType(VerifyTaxiEnvironmentTask::class.java))
    }

    @get:Input
    val warningAsErrors: Boolean = false

    @TaskAction
    fun execute() {
        logger.lifecycle("Checking taxi environment")
        // pmv = Poor Mans Validation
        val validations: Map<String, () -> Boolean> =
            mapOf(
                "At least one repository declared (Taxi is NOT currently published to Maven Central)" to
                    { project.repositories.isNotEmpty() },
                "OrbitalHQ Repository Missing (Artifact Resolution may" +
                    "fail if https://repo.orbitalhq.com/release is not available" to {
                        project.repositories
                            .filterIsInstance<MavenArtifactRepository>()
                            .none { it.url.toString().contains("orbitalhq.com") }
                    },
            )

        val pmv = validations.map { (name, validation) -> if (validation()) Either.Right(true) else Either.Left(name) }
        val errors = pmv.filter { it.isLeft() }.map { (it.swap().getOrNull()) }
        if (errors.isNotEmpty()) {
            logger.lifecycle("Taxi environment may not be valid because:")
            errors.forEach {
                logger.lifecycle(it)
            }
            check(!warningAsErrors) {
                throw IllegalStateException("Taxi environment is not valid")
            }
        } else {
            logger.lifecycle("Taxi environment is valid")
        }
    }
}
