package de.randombyte.ktskript.config

import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.time.Duration

@ConfigSerializable class GeneralConfig(
        @Setting("script-update-interval", comment = "How often the script files should be checked for changes.")
            val updateInterval: Duration = Duration.ZERO,
        @Setting("output-scripts-to-console", comment = "Useful for debugging the imports.")
            val outputScripts: Boolean = false,
        @Setting("warn-about-multiple-compile-requests", comment = "Read the docs to learn more.")
            val warnAboutDuplicates: Boolean = true
) {
    // default config
    constructor() : this(
            updateInterval = Duration.ofSeconds(3),
            outputScripts = false,
            warnAboutDuplicates = true
    )
}