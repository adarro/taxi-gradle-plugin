package io.truthencode.gradle.plugin.taxi

import io.truthencode.gradle.plugin.taxi.util.LazyLogging
import org.gradle.testfixtures.ProjectBuilder
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.notANumber
import org.junit.Assume.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.math.sqrt

/**
 * Test Taxi Init command from gradle build
 */
class WriteConfigTest : LazyLogging {
    @JvmField
    @Rule
    var testProjectDir: TemporaryFolder = TemporaryFolder()

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
        assumeTrue(project.extensions.getByName("taxi") is TaxiExtension)
        val taxi = project.extensions.getByName("taxi") as TaxiExtension
    }

    @Test
    fun testSquareRootOfMinusOneIsNotANumber() {
        assertThat(sqrt(-1.0), CoreMatchers.`is`(notANumber()))
    }

    @Test
    fun `task generates taxi conf file`() {
        testProjectDir.root.removeRecursively()
        File(testProjectDir.root, "build.gradle.kts")
            .writeText(
                // using defaults
                generateBuildFile(""),
            )

        val gradleResult = executeGradleRun("taxiWriteConfig", testProjectDir.root)
        log.error("output: ${gradleResult.output}")
        val confFile = File(testProjectDir.root, "taxi.conf")
        assertThat("Configuration File exists", confFile.exists(), CoreMatchers.`is`(true))
        val generatedFileText = confFile.readText()
        log.info(generatedFileText)
    }
}
