package io.truthencode.gradle.plugin.taxi

import io.truthencode.gradle.plugin.taxi.command.TaxiInitCommandTask
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Test Taxi Init command from gradle build
 */
class CliCommandInitTest {
    @JvmField
    @Rule
    var testProjectDir: TemporaryFolder = TemporaryFolder()

    @Test
    fun `plugin contains init task`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(BASE_TAXI_PLUGIN_ID)

        assert(project.tasks.getByName("taxiInit") is TaxiInitCommandTask)
    }
    // Taxi CLI Initialization Test
}
