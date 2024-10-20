package io.truthencode.gradle.plugin.taxi

import io.truthencode.gradle.plugin.taxi.command.TaxiBuildCommandTask
import io.truthencode.gradle.plugin.taxi.command.TaxiInitCommandTask
import io.truthencode.gradle.plugin.taxi.command.TaxiInstallCommandTask
import io.truthencode.gradle.plugin.taxi.command.TaxiOrbitalCommandTask
import io.truthencode.gradle.plugin.taxi.command.TaxiPublishCommandTask
import io.truthencode.gradle.plugin.taxi.command.TaxiPublishPluginCommandTask
import io.truthencode.gradle.plugin.taxi.command.TaxiSetVersionCommandTask
import io.truthencode.gradle.plugin.taxi.command.TaxiVersionBumpCommandTask
import io.truthencode.gradle.plugin.taxi.util.GradleDependencyHelper.getDependencyBundle
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSetContainer
import kotlin.io.path.Path
import kotlin.io.path.notExists

@Suppress("UnnecessaryAbstractClass")
abstract class TaxiPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Add the 'taxi' extension object, will keep reference once needed tasks are implemented
        project.extensions.create(TaxiExtension.TAXI_EXTENSION_NAME, TaxiExtension::class.java, project)

        // apply the java plugin (if not already applied)
        if (!project.plugins.hasPlugin(JavaPlugin::class.java)) {
            project.plugins.apply(JavaPlugin::class.java)
        }

        project.configurations.create("taxi") { c ->
            val taxiVersion =
                project.extensions
                    .getByType(TaxiExtension::class.java)
                    .taxiVersion
                    .get()

            c.isCanBeConsumed = false
            c.isCanBeResolved = true
            // May need to add this to regular dependencies
            // https://stackoverflow.com/questions/55456832/how-to-add-dependency-to-configuration-in-gradle-plugin
            c.defaultDependencies {
                getDependencyBundle(
                    names = listOf("taxi-cli", "package-manager"),
                    group = "org.taxilang",
                    version = taxiVersion,
                    project = project,
                ).forEach { d ->
                    it.add(d)
                }
            }

            getDependencyBundle(
                names = listOf("taxi-cli", "package-manager"),
                group = "org.taxilang",
                version = taxiVersion,
                project = project,
            ).forEach { d ->
                c.dependencies.add(d)
            }
        }

        // add configuration for taxi dependencies

        setupSourceSets(project)
        project.tasks.apply {
            // Environment / configuration checks (e.g. Orbital Repository availability
            // [needed until taxi is consistently published to maven central
            register(TAXI_VERIFY_ENVIRONMENT_TASK_NAME, VerifyTaxiEnvironmentTask::class.java) {
                // Task configuration here
            }
            // Environment / configuration checks (e.g. Orbital Repository availability
            // [needed until taxi is consistently published to maven central
            register(TAXI_CHECK_ENVIRONMENT_TASK_NAME, CheckTaxiEnvironmentTask::class.java) {
                // Task configuration here
            }

            register(TAXI_PUBLISH_PLUGIN_TASK_NAME, TaxiPublishPluginCommandTask::class.java) {
                // Task configuration here
            }
            // Taxi CLI tasks
            register(TAXI_BUILD_TASK_NAME, TaxiBuildCommandTask::class.java) {
                // Task configuration here
            }
            register(TAXI_INIT_TASK_NAME, TaxiInitCommandTask::class.java) {
                // Task configuration here
            }
            register(TAXI_PUBLISH_TASK_NAME, TaxiPublishCommandTask::class.java) {
                // Task configuration here
            }
            register(TAXI_SET_VERSION_TASK_NAME, TaxiSetVersionCommandTask::class.java) {
                // Task configuration here
            }
            register(TAXI_VERSION_BUMP_TASK_NAME, TaxiVersionBumpCommandTask::class.java) {
                // Task configuration here
            }
            register(TAXI_INSTALL_TASK_NAME, TaxiInstallCommandTask::class.java) {
                // Task configuration here
            }
            register(TAXI_ORBITAL_TASK_NAME, TaxiOrbitalCommandTask::class.java) {
                // Task configuration here
            }
            // General Taxi Tasks
            register(TAXI_WRITE_CONFIG_TASK_NAME, WriteConfigTask::class.java) {
                // Task configuration here
            }
        }
    }

    /**
     * Adds the taxi source root to the project source sets
     * Unfortunately, until [Declarative Gradle](https://declarative.gradle.org/) happens,
     * we need to add this as a Java Sourceset and have the Java plugin applied.
     */
    fun setupSourceSets(project: Project) {
        val ext = project.extensions.getByType(TaxiExtension::class.java)
        val srcRoot = ext.taxiConfig.get().sourceRoot
        if (Path(srcRoot).notExists()) {
            project.logger.warn("Taxi: source root $srcRoot does not exist")
        }
        // This will set up the taxi files as a visible sourceset for the IDE.
        project.plugins.withType(JavaPlugin::class.java) { _ ->
            val sourceSets = project.properties["sourceSets"] as SourceSetContainer
            sourceSets
                .getByName("main")
                .resources.srcDirs
                .add(Path(srcRoot).toFile())
            /* add below to avro task
            // val resourcesTask = project.tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME, Copy::class.java).get()
            // resourcesTask.dependsOn(project.tasks.named("taxiGenerate"))
             */
        }
    }
}
