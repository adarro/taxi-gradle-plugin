package io.truthencode.gradle.plugin.taxi.command

import io.truthencode.gradle.plugin.taxi.NYI
import io.truthencode.gradle.plugin.taxi.command.internal.TaxiInternalJavaExecTask

open class TaxiVersionBumpCommandTask : TaxiInternalJavaExecTask() {
    override val mainClass: String
        get() = TODO(NYI)

    init {

        description = "Bumps the taxi project version"
    }

    override fun executeInternal() {
        TODO(NYI)
    }

    override fun executeExternal() {
        TODO(NYI)
    }
}
