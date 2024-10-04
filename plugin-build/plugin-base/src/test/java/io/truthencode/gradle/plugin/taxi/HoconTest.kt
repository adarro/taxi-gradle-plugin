package io.truthencode.gradle.plugin.taxi

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import io.github.config4k.toConfig
import io.truthencode.gradle.plugin.taxi.util.LazyLogging
import lang.taxi.packages.TaxiPackageProject
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

class HoconTest : LazyLogging {
    val log = logger()
    val optJson =
        ConfigRenderOptions
            .concise()
            .setOriginComments(false)
            .setJson(true)
            .setFormatted(true)
    val optHocon =
        ConfigRenderOptions
            .concise()
            .setOriginComments(false)
            .setJson(false)
            .setFormatted(true)

    @Test
    fun `test hocon`() {
        val tPackage = taxiPackage()
        val taxi = tPackage.toConfig("taxi")
        val opt =
            ConfigRenderOptions
                .defaults()
                .setOriginComments(false)
                .setJson(false)
                .setFormatted(true)
        val data = taxi.root().render(opt)
        log.debug(data)
        assertThat(data, containsString("io.truthencode/taxigradle"))
    }

    @Test
    fun `test hocon as json`() {
        val tPackage = taxiPackage()
        val taxi = tPackage.toConfig("taxi")

        val expected = ConfigFactory.parseString(nakedConfig).root().render(optJson)
        val actual = taxi.root().toMap()["taxi"]?.render(optJson)
        // using optJson one-off comparison as HOCON libraries mangle option particulars such as order or '=' vs ':' etc.
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT)
    }

    @Test
    fun `test long road`() {
        val tPackage = taxiPackage()
        val data = dataClassMapper().writer().withRootName("").writeValueAsString(tPackage)
        log.error(data)
        val hoconData =
            ConfigFactory
                .parseString(data)
                .root()
                .render(
                    ConfigRenderOptions
                        .concise()
                        .setOriginComments(false)
                        .setJson(false)
                        .setFormatted(true),
                )
        log.debug(hoconData)
    }

    @Test
    fun `test taxi_conf read`() {
        val rslt =
            ConfigFactory
                .parseString(defaultConfig)
                .root()
                .render(
                    ConfigRenderOptions
                        .concise()
                        .setOriginComments(false)
                        .setJson(false)
                        .setFormatted(true),
                )
        log.debug(rslt)
    }

    @Test
    fun `test HOCON Json parent hack`() {
        val wrapping = rootMapper()
        var actualJSON = wrapping.writer().withRootName("something").writeValueAsString(Bean())
        assertThat("contents not equal", "{\"something\":{\"a\":3}}", equalTo(actualJSON))
        actualJSON = wrapping.writer().withRootName("").writeValueAsString(Bean())
        val expectedJSON = "{\"a\":3}"
        JSONAssert.assertEquals(expectedJSON, actualJSON, JSONCompareMode.STRICT)
    }

    @Test
    fun `writeTask contains default init configuration`() {
        val expectedJSON = ConfigFactory.parseString(defaultConfig).root().render(optJson)
        val actualJSON = ConfigFactory.parseString(nakedConfig).root().render(optJson)
        JSONAssert.assertEquals(expectedJSON, actualJSON, JSONCompareMode.LENIENT)
    }

    data class Bean(
        val a: Int = 3,
    )

    private fun dataClassMapper(): ObjectMapper {
        val mapper = jacksonObjectMapper()
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true)
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true)
        return mapper
    }

    private fun rootMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true)
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true)
        return mapper
    }

    fun taxiPackage(): TaxiPackageProject = TaxiPackageProject(name = "io.truthencode/taxigradle", version = "0.1.0", sourceRoot = "src/")

    val nakedConfig =
        """
        additionalSources {}
        credentials=[]
        dependencies {}
        linter {}
        name="io.truthencode/taxigradle"
        output="dist/"
        pluginSettings {
        localCache="~/.taxi/plugins"
        repositories=[]
        }
        plugins {}
        repositories=[]
        sourceRoot="src/"
        taxiHome="/home/adarro/.taxi"
        version="0.1.0"
        """.trimIndent()

    val defaultConfig = """
        name: io.truthencode/taxigradle
        version: 0.1.0
        sourceRoot: src/
        additionalSources: {}
        dependencies: {}
        """
}
