package io.truthencode.gradle.plugin.taxi.command.internal

import io.truthencode.gradle.plugin.taxi.TaxiClientType
import io.truthencode.gradle.plugin.taxi.command.TaxiCliBaseTask
import io.truthencode.gradle.plugin.taxi.command.TaxiExecutable
import io.truthencode.gradle.plugin.taxi.util.EnvironmentHelper.verifyRepositories
import org.apache.commons.lang3.SystemUtils.JAVA_IO_TMPDIR
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Base class for all taxi cli tasks executed via JavaExec using taxi version specified in the project
 */
abstract class TaxiInternalJavaExecTask :
    JavaExec(),
    TaxiCliBaseTask,
    TaxiExecutable {
    init {
        group = "taxi"
        description = "Runs the internal taxi command"
        verifyRepositories(project)
    }

    @TaskAction
    override fun execute() {
        logger.lifecycle("executing ${this.name} (not really)")
        executeExternal()
    }

    @Inject
    lateinit var getExecOperations: ExecOperations

    protected abstract val mainClass: String

    /**
     * Executes task using the external taxi cli
     */
    override fun executeInternal() {
        val cliArgs = mutableListOf<String>()

        logger.info("Running Taxi API with arguments: $cliArgs")
        logger.error("classpath: size {}", computeClasspath().files.size)
        getExecOperations.javaexec { spec ->
            {
                spec.classpath = computeClasspath()
                spec.mainClass.set(mainClass)
                spec.args(cliArgs)
                spec.systemProperty(JAVA_IO_TMPDIR, temporaryDir.absolutePath)
                spec.environment(environment)
                val javaLauncher: Provider<JavaLauncher> = javaLauncher
                if (javaLauncher.isPresent) {
                    spec.executable(javaLauncher.get().executablePath.asFile)
                }
            }
        }
    }

    override val clientType: String
        get() = TaxiClientType.InternalOnly.name

    override fun executeExternal(): Unit =
        throw UnsupportedOperationException("Not supported yet.  Can not invoke internal taxi api externally")

    @Inject
    lateinit var getObjects: ObjectFactory

    @Classpath
    val taxiClasspath: ConfigurableFileCollection = project.files(project.configurations.getByName("taxi").files)

    fun computeClasspath(): FileCollection {
        val classpath: ConfigurableFileCollection = getObjects.fileCollection()
        classpath.from(taxiClasspath)

        return classpath
    }
}
