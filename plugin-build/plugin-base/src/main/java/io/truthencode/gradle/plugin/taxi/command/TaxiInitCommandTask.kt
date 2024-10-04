package io.truthencode.gradle.plugin.taxi.command

import org.gradle.api.tasks.TaskAction

open class TaxiInitCommandTask : TaxiCliBaseTask() {
    init {

        description = "runs the taxi init command"
    }

    @TaskAction
    override fun execute() {
        logger.lifecycle("initialize the taxi project (not really)")
        TODO("Not yet implemented")
    }
}
