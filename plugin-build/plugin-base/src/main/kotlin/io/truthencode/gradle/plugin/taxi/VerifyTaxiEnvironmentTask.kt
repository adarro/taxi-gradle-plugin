package io.truthencode.gradle.plugin.taxi

import io.truthencode.gradle.plugin.taxi.util.EnvironmentHelper.verifyRepositories
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class VerifyTaxiEnvironmentTask : DefaultTask() {
    init {
        group = "taxi"
        description = "Verifies the taxi environment applying defaults where needed"
    }

    private val ext = project.extensions.getByType(TaxiExtension::class.java)

    @TaskAction
    fun execute() {
        logger.lifecycle("Verifying taxi environment")
        verifyRepositories(project)
    }
}
