package io.truthencode.gradle.plugin.taxi

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.encodeToConfig
import lang.taxi.packages.TaxiPackageProject
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

class KotlinXTest {
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun `test kotlinx serialization`() {
        val tp = TaxiPackageProject(name = "io.truthencode/test", version = "1.0.0")
        val executable = Executable { Hocon.encodeToConfig(tp) }
        assertThrows(SerializationException::class.java, executable)
    }
}
