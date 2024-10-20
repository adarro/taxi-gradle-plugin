package io.truthencode.gradle.plugin.taxi.command

import org.gradle.api.Task
import org.gradle.api.tasks.Input

/**
 * Base class for all taxi cli tasks
 * Should invoke the wonderful Taxi CLI
 */
@SuppressWarnings("UnnecessaryAbstractClass")
interface TaxiCliBaseTask : Task {
    //    @Nested
//    @Optional
//    lateinit var javaLauncher: Property<JavaLauncher>
//

    @get:Input
    val clientType: String

    /**
     * Executes task using the internal taxi API
     */
    fun executeInternal()

    /**
     * Executes task using the external taxi cli
     */
    fun executeExternal()
}
