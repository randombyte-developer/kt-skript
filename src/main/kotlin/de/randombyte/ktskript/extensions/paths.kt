package de.randombyte.ktskript.extensions

import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import java.nio.file.Path

fun Path.toConfigurationLoader() = HoconConfigurationLoader.builder()
        .setPath(this)
        .build()

fun Path.loadConfig(): ConfigurationNode = toConfigurationLoader().load()

fun Path.saveConfig(configurationNode: ConfigurationNode) {
    toConfigurationLoader().save(configurationNode)
}