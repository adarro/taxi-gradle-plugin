package io.truthencode.gradle.plugin.taxi

import com.sksamuel.hoplite.ConfigException
import io.truthencode.gradle.plugin.taxi.util.LazyLogging
import lang.taxi.packages.TaxiPackageProject
import lang.taxi.packages.TaxiProjectLoader
import org.gradle.api.Incubating
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.exists

/**
 * Extension class for interacting and configuration a local Taxi project.
 */
open class TaxiExtension
    @Inject
    constructor(
        val project: Project,
        objects: ObjectFactory,
    ) : LazyLogging {
        companion object {
            const val TAXI_EXTENSION_NAME = "taxi"

            /**
             * The default version of taxi to use.
             * I would like to use the latest version of taxi, or a configuration file at some point.
             */
            const val TAXI_DEFAULT_VERSION = "1.56.0"
        }

        private val log = logger()

        /**
         * Optionally adds required repositories (Currently OrbitalHQ) to the project.
         * Defaults to Release = true, Snapshot = false
         */
        val repositories: Property<RepositoryOptions> =
            objects
                .property(
                    RepositoryOptions::class.java,
                ).convention(RepositoryOptions(false, true))

        /**
         * The location of the taxi config file.
         * If a taxi.conf is found, it will be used for the taxi config.
         * By default, this is the project directory.
         */
        val configPath: Property<String> =
            objects
                .property(String::class.java)
                .convention(project.projectDir.absolutePath)

        /**
         * Whether to use an embedded taxi client, or an external one.
         * We should default to internal, and use an external one only if explicitly requested.
         * This is because we want to be able to use the taxi client without having to install it.
         * Also, we avoid calling processBuilder if we can avoid it.
         */
        val taxiClientType: Property<TaxiClientType> =
            objects.property(TaxiClientType::class.java).convention(
                TaxiClientType.ExternalThenInternal,
            )

        /**
         * The version of taxi to use.
         *
         */
        val taxiVersion: Property<String> = objects.property(String::class.java).convention(TAXI_DEFAULT_VERSION)

        /**
         * Attempts to load a taxi.conf from the configPath
         */
        private fun projectFromPath(path: String): TaxiPackageProject? {
            log.debug("looking for taxi.conf on path $path")
            if (Path(path).exists() && Path("$path/taxi.conf").exists()) {
                try {
                    val config =
                        TaxiProjectLoader(Path(path).resolve("taxi.conf")).load()
                    log.info("Successfully loaded $config")
                    return config
                } catch (e: ConfigException) {
                    log.error("could not load taxi.conf", e)
                }
            } else {
                log.debug("could not load taxi.conf as path: ($path) does not exist ")
            }
            return null
        }

        /**
         * Attempts to load a default set of taxi config values.
         * Attempts to read from [[configPath]], then applies default values using [[project]] name and version.
         * These values should be able to be overridden via the gradle extension.
         */
        private fun defaultProject(): TaxiPackageProject =
            projectFromPath(configPath.get()) ?: TaxiPackageProject(
                name = "${project.group}/${project.name}",
                version = project.version.toString(),
                sourceRoot = DEFAULT_TAXI_SOURCE_ROOT,
            )

        /**
         * Allows overriding / manipulating the taxi config. (taxi.conf) via the gradle build file.
         */
        @Incubating
        val taxiConfig: Property<TaxiPackageProject> =
            objects.property(TaxiPackageProject::class.java).convention(defaultProject())
    }
