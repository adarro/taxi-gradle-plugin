package io.truthencode.gradle.plugin.taxi

/**
 * Holds options for whether the plugin will attempt to add repositories to the project.
 */
data class RepositoryOptions(
    /**
     * If true, the taxi snapshot repository will be added to the project.
     */
    val snapshot: Boolean = false,
    /**
     * If true, the taxi release repository will be added to the project.
     */
    val release: Boolean = false,
)
