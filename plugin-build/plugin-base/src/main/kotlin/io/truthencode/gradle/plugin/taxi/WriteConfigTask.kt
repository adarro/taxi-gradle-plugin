package io.truthencode.gradle.plugin.taxi

import io.github.config4k.toConfig
import io.truthencode.gradle.plugin.taxi.util.LazyLogging
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

abstract class WriteConfigTask :
    DefaultTask(),
    LazyLogging {
    init {
        description = "Writes configuration to taxi.conf"
        val taxi = project.extensions.getByName(TaxiExtension.TAXI_EXTENSION_NAME) as TaxiExtension
        val taxiConfig = resolveTaxiConfig(taxi.configPath.get())
        outputFile.convention(project.objects.fileProperty().fileValue(File(taxiConfig)))
    }

    private fun resolveTaxiConfig(dir: String): String {
        val isDirectory = Path(dir).isDirectory()
        val isFile = Path(dir).isRegularFile()
        val isDefaultConfigFile = isFile && Path(dir).toFile().name.equals(DEFAULT_TAXI_CONFIG_NAME, true)
        require(isDirectory || isDefaultConfigFile || isFile) {
            "Directory or file does not exist: $dir"
        }
        return if (Path(dir).isDirectory()) {
            Path(dir).resolve("taxi.conf").toString()
        } else {
            val f = Path(dir).toFile()
            if (!f.name.equals("taxi.conf", true)) {
                log.warn(
                    "File $dir does not appear to be a taxi.conf file." +
                        "  Taxi config may not be generated correctly.",
                )
            }
            f.absolutePath
        }
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

    @TaskAction
    fun writeConfig() {
        logger.info("executing write taxi.conf task")
        val taxi = project.extensions.getByName(TaxiExtension.TAXI_EXTENSION_NAME) as TaxiExtension
        val cp = outputFile.get().asFile
        log.debug("taxi.conf path is {}", cp)
        log.debug("outputFile: ${outputFile.get().asFile.absolutePath}")
        log.debug("taxi.conf exists: ${cp.exists()}")
        log.debug("isFile:{}\nexists:{}\noverwrite:{}", cp.isFile, cp.exists(), overwrite.get())
        if (!cp.exists() || (cp.isFile() && overwrite.get())) {
            if (log.isDebugEnabled) {
                val msg = if (cp.exists()) "overwriting" else "creating"
                log.debug("$msg taxi.conf")
            }

            val tPackage = taxi.taxiConfig.get().toConfig("taxi")
            val data = tPackage.root().toMap()["taxi"]?.render(optHocon)
            log.debug("writing to file: \n$data")
            File(cp.toString()).printWriter().use { out -> out.println(data) }
        } else {
            log.debug("taxi.conf exists, not overwriting")
        }
    }
}
