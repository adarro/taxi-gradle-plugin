package io.truthencode.gradle.plugin.taxi

enum class TaxiClientType {
    /**
     * Use the built-in client regardless of what client is installed.
     */
    InternalOnly,

    /**
     * Use the external client if it is installed, otherwise use the built-in client.
     */
    ExternalThenInternal,

    /**
     * Use the external client regardless of what client is installed.
     * Fails if the external client is not installed.
     */
    ExternalOnly,
}
