package io.truthencode.gradle.plugin.taxi

import com.typesafe.config.ConfigRenderOptions
import io.truthencode.gradle.plugin.taxi.util.LazyLogging
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject

fun <T : LazyLogging> getClassForLogging(javaClass: Class<T>): Class<*> =
    javaClass.enclosingClass?.takeIf {
        it.kotlin.companionObject?.java == javaClass
    } ?: javaClass

inline fun <reified T : LazyLogging> T.logger(): Logger = LoggerFactory.getLogger(getClassForLogging(T::class.java))

/**
 * Deserializes a config to an HOCON string
 */
val optHocon: ConfigRenderOptions? =
    ConfigRenderOptions
        .concise()
        .setOriginComments(false)
        .setJson(false)
        .setFormatted(true)

const val DEFAULT_TAXI_SOURCE_ROOT = "src/"
const val DEFAULT_TAXI_CONFIG_NAME = "taxi.conf"

// Task IDs
const val TAXI_VERSION_BUMP_TASK_NAME = "taxiVersionBump"
const val TAXI_PUBLISH_PLUGIN_TASK_NAME = "taxiPublishPlugin"
const val TAXI_INSTALL_TASK_NAME = "taxiInstall"
const val TAXI_SET_VERSION_TASK_NAME = "taxiSetVersion"
const val TAXI_PUBLISH_TASK_NAME = "taxiPublish"
const val TAXI_BUILD_TASK_NAME = "taxiBuild"
const val TAXI_INIT_TASK_NAME = "taxiInit"
const val TAXI_ORBITAL_TASK_NAME = "taxiOrbital"

const val TAXI_WRITE_CONFIG_TASK_NAME = "taxiWriteConfig"
