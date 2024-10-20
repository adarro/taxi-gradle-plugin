package io.truthencode.gradle.plugin.taxi.command

import org.gradle.api.tasks.TaskAction

fun interface TaxiExecutable {
    @TaskAction
    fun execute()
}
