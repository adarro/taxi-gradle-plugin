package io.truthencode.gradle.plugin.taxi.command.external

import io.truthencode.gradle.plugin.taxi.TaxiClientType
import io.truthencode.gradle.plugin.taxi.command.TaxiCliBaseTask
import io.truthencode.gradle.plugin.taxi.util.EnvironmentHelper
import org.gradle.api.tasks.Exec

open class TaxiExternalExecTask :
    Exec(),
    TaxiCliBaseTask {
    init {
        group = "taxi"
        description = "Runs the external taxi command"
    }

    override val clientType: String
        get() = TaxiClientType.ExternalOnly.name

    override fun executeInternal() {
        TODO("Not yet implemented")
    }

    override fun executeExternal() {
        TODO("Not yet implemented")
    }

    init {
        description = "Runs the external taxi command"
        EnvironmentHelper.verifyRepositories(project)
    }
}
