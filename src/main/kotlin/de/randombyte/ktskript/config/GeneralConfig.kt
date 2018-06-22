package de.randombyte.ktskript.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.time.Duration

@ConfigSerializable class GeneralConfig(
        @Setting("script-update-interval", comment = "How often the script files should be checked for changes.")
            val updateInterval: Duration = Duration.ZERO
) {
    // default config
    constructor() : this(
            updateInterval = Duration.ofSeconds(3)
    )
}