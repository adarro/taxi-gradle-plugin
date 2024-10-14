package io.truthencode.gradle.plugin.taxi

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.PropertySource
import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigRenderOptions
import io.github.config4k.Config4kException.UnSupportedType
import io.github.config4k.toConfig
import io.truthencode.gradle.plugin.taxi.HopeliteTest.DataClassWithConfigAsValue
import io.truthencode.gradle.plugin.taxi.HopeliteTest.Plugin
import io.truthencode.gradle.plugin.taxi.util.LazyLogging
import lang.taxi.packages.TaxiPackageProject
import lang.taxi.packages.TaxiProjectLoader
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.util.stream.Stream

class HoconTest : LazyLogging {
    val log = logger()
    val optJson: ConfigRenderOptions? =
        ConfigRenderOptions
            .concise()
            .setOriginComments(false)
            .setJson(true)
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
    @DisplayName("config4k can not decode class with config")
    fun deserializeClassWithConfig() {
        //  val plugin = ConfigLoader().loadConfigOrThrow<Plugin>("/plugin.yaml")
        val plugin =
            ConfigLoaderBuilder
                .default()
                .addPropertySource(PropertySource.string(pluginJsonNoParent, "json"))
                .build()
                .loadConfigOrThrow<Plugin>()
        val pluginAsConfig = plugin.toConfig("plugin")
        val cfg = DataClassWithConfigAsValue("bar", pluginAsConfig)

        val block: Executable = Executable { cfg.toConfig("cfg") }

        assertThrows(UnSupportedType::class.java, block)
    }

    @Test
    fun loadDefaultTaxiConfigViaHoplite() {
        val resource = FileResourceUtil.getFileFromResource("taxi.conf")
        assumeTrue(resource.exists())
        Assertions.assertDoesNotThrow {
            ConfigLoaderBuilder
                .default()
                .addPropertySource(PropertySource.resource("/taxi.conf"))
                .build()
                .loadConfigOrThrow<TaxiPackageProject>()
        }
    }

    @Disabled("Hoplite does not support loading dataclass when dataclass contains a Config as a value")
    @ParameterizedTest
    @MethodSource("taxiConfigurationVariations")
    fun loadDefaultGeneratedTaxiConfigViaHoplite(
        label: String,
        input: String,
    ) {
        log.error("running $label")
        val resource = FileResourceUtil.getFileFromResource(input)
        assumeTrue(resource.exists())
        Assertions.assertDoesNotThrow {
            ConfigLoaderBuilder
                .default()
                .addPropertySource(PropertySource.resource("/$input"))
                .build()
                .loadConfigOrThrow<TaxiPackageProject>()
        }
    }

    @ParameterizedTest
    @MethodSource("taxiConfigurationVariations")
    @DisplayName("Raw Typesafe config should load taxi.conf")
    fun loadDefaultGeneratedTaxiConfigViaTypeSafe(
        label: String,
        input: String,
    ) {
        log.error("running $label")
        val resource = FileResourceUtil.getFileFromResource(input)
        assumeTrue(resource.exists())
        Assertions.assertDoesNotThrow {
            TaxiProjectLoader(resource.toPath()).load().toConfig("taxi")
        }
    }

    @Test
    fun `test hocon as json`() {
        val tPackage = taxiPackage()
        val taxi = tPackage.toConfig("taxi")

        val expected = ConfigFactory.parseString(nakedConfig).root().render(optJson)
        val actual = taxi.root().toMap()["taxi"]?.render(optJson)
        // using optJson one-off comparison as HOCON libraries mangle option
        // particulars such as order or '=' vs ':' etc.
        JSONAssert.assertEquals(expected, actual, JSONCompareMode.STRICT)
    }

    @Test
    @DisplayName("Load taxi.conf using pure Typesafe.config")
    fun loadConfWithPlainConfig() {
        val conf = FileResourceUtil.getFileFromResource("taxi.conf")
        assumeTrue(conf.exists())
        val cPath = conf.toPath()
        val loader = TaxiProjectLoader(taxiConfPath = cPath)
        val tp = loader.load()
        log.error(tp.toConfig("taxi").root().render(optHocon))
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

    @Suppress("ktlint:standard:max-line-length", "detekt:MaxLineLength")
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

    val pluginJsonNoParent =
        """
        {
            "id" : "plugin-base",
            "url" : "https://github.com/plugin-base/plugin-base"
        }
        """.trimIndent()
    val pluginYaml = """
        id: "plugin-base"
url: "https://github.com/plugin-base/plugin-base"
  """

    companion object {
        @JvmStatic
        fun taxiConfigurationVariations(): Stream<Arguments> =
            Stream.of(
                Arguments.of("default taxi", "taxi.conf"),
                Arguments.of("generated, no plugin", "taxi-gen-no-plugins.conf"),
                Arguments.of("generated, ordered", "taxi-ordered.conf"),
                Arguments.of("default generated", "taxi-gen.conf"),
                Arguments.of("generated dot replace", "taxi-gen-dots.conf"),
            )
    }
}
