package io.truthencode.gradle.plugin.taxi.util

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency

object GradleDependencyHelper {
    /**
     * Convenience method to get a list of dependencies from a list of names.
     * @param names The names of the dependencies to get. Such as taxi-core, taxi-cli, etc.
     * @param group The group of the dependencies. Such as org.taxilang
     * @param version The version of the dependencies. Such as 1.56.0
     * @param project The project to get the dependency handler from.
     * @return A list of dependencies.
     */
    fun getDependencyBundle(
        names: List<String>,
        group: String,
        version: String,
        project: Project,
    ): List<Dependency> = names.map { name -> project.dependencies.create("$group:$name:$version") }
}
