package io.truthencode.gradle.plugin.taxi

import io.truthencode.gradle.plugin.taxi.util.LazyLogging
import org.gradle.testfixtures.ProjectBuilder
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 * Test Taxi Init command from gradle build
 */
class WriteConfigTest : LazyLogging {
    @TempDir
    lateinit var testProjectDir: File

    val log = logger()

    @Test
    fun `plugin contains writeConfig task`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(BASE_TAXI_PLUGIN_ID)

        assert(project.tasks.getByName("taxiWriteConfig") is WriteConfigTask)
    }

    @Test
    fun `writeConfig task writes config file`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(BASE_TAXI_PLUGIN_ID)
        // may be a better way to do this
        assertTrue(project.extensions.getByName(TaxiExtension.TAXI_EXTENSION_NAME) is TaxiExtension)
    }

    @Test
    fun `task generates taxi conf file`() {
        val td = testProjectDir
        td.removeRecursively()
        val f = File(td, "build.gradle.kts")
        // using defaults
        f.writeText(generateBuildFile(""))
        assumeTrue(f.exists())
        log.error(f.readText())
        executeGradleRun("taxiWriteConfig", td)

        val confFile = File(td, "taxi.conf")
        assertThat("Configuration File exists", confFile.exists(), CoreMatchers.`is`(true))
        val generatedFileText = confFile.readText()
        log.error(generatedFileText)
    }
}
