package io.truthencode.gradle.plugin.taxi

import org.gradle.api.DefaultTask

@SuppressWarnings("UnnecessaryAbstractClass")
abstract class TaxiBaseTask : DefaultTask() {
    init {
        group = "taxi"
    }
}
