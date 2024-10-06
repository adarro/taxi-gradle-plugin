package io.truthencode.gradle.plugin.taxi

import io.truthencode.gradle.plugin.taxi.command.TaxiInitCommandTask
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Test Taxi Init command from gradle build
 */
class CliCommandInitTest {
    @TempDir
    lateinit var testProjectDir: File

    @Test
    @DisplayName("plugin contains init task")
    fun taskExistsTest() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(BASE_TAXI_PLUGIN_ID)

        assert(project.tasks.getByName("taxiInit") is TaxiInitCommandTask)
    }
    // Taxi CLI Initialization Test
}
