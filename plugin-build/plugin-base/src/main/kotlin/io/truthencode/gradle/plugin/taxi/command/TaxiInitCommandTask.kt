package io.truthencode.gradle.plugin.taxi.command

import io.truthencode.gradle.plugin.taxi.NYI
import io.truthencode.gradle.plugin.taxi.command.internal.TaxiInternalJavaExecTask
import org.gradle.api.tasks.TaskAction

open class TaxiInitCommandTask : TaxiInternalJavaExecTask() {
    init {

        description = "runs the taxi init command"
    }

    @TaskAction
    override fun execute() {
        logger.lifecycle("initialize the taxi project (not really)")
        TODO(NYI)
    }

    override val mainClass: String
        get() = TODO(NYI)

    override fun executeInternal() {
        TODO(NYI)
    }

    override fun executeExternal() {
        TODO(NYI)
    }
}
