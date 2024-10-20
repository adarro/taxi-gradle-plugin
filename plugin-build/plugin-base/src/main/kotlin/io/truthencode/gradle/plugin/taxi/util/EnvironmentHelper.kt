package io.truthencode.gradle.plugin.taxi.util

import io.truthencode.gradle.plugin.taxi.TAXI_DEFAULT_REPOSITORY_URL
import io.truthencode.gradle.plugin.taxi.TAXI_DEFAULT_REPOSITORY_URL_RELEASE
import io.truthencode.gradle.plugin.taxi.TAXI_DEFAULT_REPOSITORY_URL_SNAPSHOT
import io.truthencode.gradle.plugin.taxi.TaxiExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import java.net.URI

object EnvironmentHelper {
    fun verifyRepositories(project: Project) {
        val ext = project.extensions.getByType(TaxiExtension::class.java)
        val rOpts = ext.repositories.get()
        if ((rOpts.release || rOpts.snapshot) &&
            project.repositories
                .filterIsInstance<MavenArtifactRepository>()
                .none {
                    it.url.toString().contains(TAXI_DEFAULT_REPOSITORY_URL)
                }
        ) {
            project.repositories.maven {
                when {
                    rOpts.snapshot -> {
                        it.name = "Taxi Default Snapshot Repository" // OrbitalHQ snapshot
                        it.url = URI(TAXI_DEFAULT_REPOSITORY_URL_SNAPSHOT)
                    }

                    rOpts.release -> {
                        it.name = "Taxi Default Repository" // OrbitalHQ release
                        it.url = URI(TAXI_DEFAULT_REPOSITORY_URL_RELEASE)
                    }
                }
            }
            project.repositories.mavenCentral()
            project.repositories.google()
        }
    }
}
