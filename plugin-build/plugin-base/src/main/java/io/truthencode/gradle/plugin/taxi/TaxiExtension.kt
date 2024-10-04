package io.truthencode.gradle.plugin.taxi

import com.sksamuel.hoplite.ConfigException
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import io.truthencode.gradle.plugin.taxi.util.LazyLogging
import lang.taxi.packages.TaxiPackageProject
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
            const val TAXI_EXTENSION_NAME = "Taxi"
        }

        private val log = logger()

        /**
         * The location of the taxi config file.
         * If a taxi.conf is found, it will be used for the taxi config.
         */
        val configPath: Property<String> =
            objects
                .property(String::class.java)
                .convention(project.projectDir.absolutePath)

        val taxiClientType: Property<TaxiClientType> =
            objects.property(TaxiClientType::class.java).convention(
                TaxiClientType.ExternalThenInternal,
            )

        /**
         * Attempts to load a taxi.conf from the configPath
         */
        fun projectFromPath(path: String): TaxiPackageProject? {
            log.info("looking for taxi.conf on path $path")
            if (Path(path).exists() && Path("$path/taxi.conf").exists()) {
                try {
                    val config =
                        ConfigLoaderBuilder
                            .default()
                            .addResourceSource("$path/taxi.conf")
                            .build()
                            .loadConfigOrThrow<TaxiPackageProject>()
                    log.info("Successfully loaded $config")
                    return config
                } catch (e: ConfigException) {
                    log.error("could not load taxi.conf", e)
                }
            } else {
                log.warn("could not load taxi.conf as path: ($path) does not exist ")
            }
            return null
        }

        /**
         * Attempts to load a default set of taxi config values.
         * Attempts to read from [[configPath]], then applies default values using [[project]] name and version.
         * These values should be able to be overridden via the gradle extension.
         */
        fun defaultProject(): TaxiPackageProject =
            projectFromPath(configPath.get()) ?: TaxiPackageProject(
                name = "${project.group}/${project.name}",
                version = project.version.toString(),
                sourceRoot = DEFAULT_TAXI_SOURCE_ROOT,
            )

        /**
         * Allows overriding the taxi config. (taxi.conf)
         */
        val taxiConfig: Property<TaxiPackageProject> =
            objects.property(TaxiPackageProject::class.java).convention(defaultProject())
    }
