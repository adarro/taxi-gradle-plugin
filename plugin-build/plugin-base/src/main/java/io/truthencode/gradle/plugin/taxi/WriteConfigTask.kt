package io.truthencode.gradle.plugin.taxi

import io.github.config4k.toConfig
import io.truthencode.gradle.plugin.taxi.util.LazyLogging
import kotlinx.serialization.ExperimentalSerializationApi
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.io.path.Path

abstract class WriteConfigTask :
    TaxiBaseTask(),
    LazyLogging {
    init {
        description = "Writes configuration to taxi.conf"
        val taxi = project.extensions.getByName(TaxiExtension.TAXI_EXTENSION_NAME) as TaxiExtension
        outputFile.convention(project.objects.fileProperty().fileValue(File(taxi.configPath.get())))
    }

    private val log = logger()

    /**
     * If true, overwrite the taxi.conf file if it exists.
     * NOTE: Most settings specified in build file will not be reflected in the taxi.conf file if overwrite is false.
     */
    @get:Input
    val overwrite: Property<Boolean> = project.objects.property(Boolean::class.java).convention(true)

    /**
     * Location of the taxi.conf file.
     * This should be a file, not a directory
     */
    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @OptIn(ExperimentalSerializationApi::class)
    @TaskAction
    fun writeConfig() {
        logger.info("here is where we would write our taxi.conf")
        val taxi = project.extensions.getByName(TaxiExtension.TAXI_EXTENSION_NAME) as TaxiExtension
        var cp =
            Path(taxi.configPath.get())
                .let {
                    if (it.isAbsolute) {
                        it
                    } else {
                        Path(project.projectDir.absolutePath).resolve(
                            it,
                        )
                    }
                }.toFile()
        log.error("taxi.conf path is $cp")

        log.error("taxi.conf exists: ${cp.exists()}")
        if (cp.isFile && (!cp.exists() || overwrite.get())) {
            if (log.isDebugEnabled) {
                val msg = if (cp.exists()) "overwriting" else "creating"
                log.debug("$msg taxi.conf")
            }

            val tPackage = taxi.taxiConfig.get().toConfig("taxi")
            val data =
                tPackage
                    .root()
                    .toMap()["taxi"]
                    ?.render(optHocon)
            log.error("writing to file: \n$data")
            File(cp.toString()).printWriter().use { out -> out.println(data) }
        } else {
            log.error("taxi.conf exists, not overwriting")
        }
    }

    fun findConfig(path: String) {
        val cp = Path(path)
        if (cp.isAbsolute) {
            log.error("taxi.conf path is $cp")
        }
    }
}
