package io.truthencode.gradle.plugin.taxi

import io.github.config4k.toConfig
import io.truthencode.gradle.plugin.taxi.util.LazyLogging
import kotlinx.serialization.ExperimentalSerializationApi
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.io.path.Path

abstract class WriteConfigTask :
    TaxiBaseTask(),
    LazyLogging {
    init {
        description = "Writes configuration to taxi.conf"
        val taxi = project.extensions.getByName("taxi") as TaxiExtension
        outputFile.convention(project.objects.fileProperty().fileValue(File(taxi.configPath.get())))
    }

    private val log = logger()

    @get:Input
    val overwrite: Property<Boolean> = project.objects.property(Boolean::class.java).convention(true)

    @get:OutputDirectory
    abstract val outputFile: RegularFileProperty

    @OptIn(ExperimentalSerializationApi::class)
    @TaskAction
    fun writeConfig() {
        logger.info("here is where we would write our taxi.conf")
        val taxi = project.extensions.getByName("taxi") as TaxiExtension
        val cp = Path(taxi.configPath.get())
        val sib = cp.resolve("taxi.conf")
        log.info("taxi.conf path is $cp")
        log.info("taxi.conf sibling is $sib")
        if (cp.toFile().isDirectory && !sib.toFile().exists()) {
            log.info("taxi.conf does not exists, creating")
            val tPackage = taxi.taxiConfig.get().toConfig("taxi")
            val data =
                tPackage
                    .root()
                    .toMap()["taxi"]
                    ?.render(optHocon)

            File(sib.toString()).printWriter().use { out -> out.println(data) }
        } else {
            log.info("taxi.conf exists, not overwriting")
        }
    }
}
