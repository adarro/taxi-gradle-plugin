package io.truthencode.gradle.plugin.taxi

import io.truthencode.gradle.plugin.taxi.command.TaxiBuildCommandTask
import io.truthencode.gradle.plugin.taxi.command.TaxiInitCommandTask
import io.truthencode.gradle.plugin.taxi.command.TaxiInstallCommandTask
import io.truthencode.gradle.plugin.taxi.command.TaxiOrbitalCommandTask
import io.truthencode.gradle.plugin.taxi.command.TaxiPublishCommandTask
import io.truthencode.gradle.plugin.taxi.command.TaxiPublishPluginCommandTask
import io.truthencode.gradle.plugin.taxi.command.TaxiSetVersionCommandTask
import io.truthencode.gradle.plugin.taxi.command.TaxiVersionBumpCommandTask
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("UnnecessaryAbstractClass")
abstract class TaxiPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Add the 'taxi' extension object, will keep reference once needed tasks are implemented
        project.extensions.create("taxi", TaxiExtension::class.java, project)

        project.tasks.apply {
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
            register(TAXI_PUBLISH_PLUGIN_TASK_NAME, TaxiPublishPluginCommandTask::class.java) {
                // Task configuration here
            }
            // General Taxi Tasks
            register(TAXI_WRITE_CONFIG_TASK_NAME, WriteConfigTask::class.java) {
                // Task configuration here
            }
        }
    }
}
