package io.truthencode.gradle.plugin.taxi.command

import io.truthencode.gradle.plugin.taxi.command.internal.TaxiInternalJavaExecTask

open class TaxiBuildCommandTask : TaxiInternalJavaExecTask() {
    init {

        description = "Builds the taxi plugin"
    }

    override val mainClass: String
        get() = this.javaClass.name

    override fun executeInternal() {
        TODO("Not yet implemented")
    }

    override fun executeExternal() {
        TODO("Not yet implemented")
    }
}
