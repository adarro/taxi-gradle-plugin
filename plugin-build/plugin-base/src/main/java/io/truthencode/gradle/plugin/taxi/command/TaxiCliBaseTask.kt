package io.truthencode.gradle.plugin.taxi.command

import io.truthencode.gradle.plugin.taxi.TaxiBaseTask
import org.gradle.api.tasks.TaskAction

/**
 * Base class for all taxi cli tasks
 * Should invoke the wonderful Taxi CLI
 */
@SuppressWarnings("UnnecessaryAbstractClass")
abstract class TaxiCliBaseTask : TaxiBaseTask() {
    @TaskAction
    open fun execute() {
        logger.lifecycle("executing ${this.name} (not really)")
        TODO("Not yet implemented")
    }
}
