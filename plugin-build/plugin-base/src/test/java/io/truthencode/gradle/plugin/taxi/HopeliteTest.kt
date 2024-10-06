package io.truthencode.gradle.plugin.taxi

import com.typesafe.config.Config
import com.typesafe.config.ConfigRenderOptions
import io.github.config4k.toConfig
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

class HopeliteTest {
    @Test
    fun `display() with wrong argument command should fail`() {
        // Given a wrong argument
        val arg = "FAKE_ID"

        // When we call display() with the wrong argument
        val executable = Executable { throw IllegalArgumentException("FAKE_ID") }

        // Then it should throw an IllegalArgumentException
        assertThrows(IllegalArgumentException::class.java, executable)
    }

    @Test
    fun makeOutPut() {
        println(
            Plugin(id = "sksamule", url = "https://github.com/sksamuel/hoplite")
                .toConfig("plugin")
                .root()
                .render(ConfigRenderOptions.concise().setJson(true).setFormatted(true)),
        )
    }

    val pluginJson =
        """
        {
            "plugin" : {
                "id" : "plugin-base",
                "url" : "https://github.com/plugin-base/plugin-base"
            }
        }
        """.trimIndent()

    data class DataClassWithConfigAsValue(
        val foo: String,
        val cfg: Config,
    )

    data class Plugin(
        val id: String,
        val url: String,
    )
}
