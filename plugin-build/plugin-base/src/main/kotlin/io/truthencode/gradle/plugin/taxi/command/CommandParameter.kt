package io.truthencode.gradle.plugin.taxi.command

data class CommandParameter(
    var flag: Pair<String, String?>,
    var args: List<String>,
)
