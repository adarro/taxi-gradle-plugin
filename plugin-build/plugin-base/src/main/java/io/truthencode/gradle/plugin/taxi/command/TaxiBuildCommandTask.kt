package io.truthencode.gradle.plugin.taxi.command

import org.gradle.api.tasks.TaskAction

open class TaxiBuildCommandTask : TaxiCliBaseTask() {
    init {

        description = "Builds the taxi plugin"
    }

    @TaskAction
    override fun execute() {
        logger.lifecycle("Building the taxi plugin (not really)")
        TODO("Not yet implemented")
    }
}
