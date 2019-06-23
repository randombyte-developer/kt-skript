package de.randombyte.ktskript.config

import de.randombyte.ktskript.config.kosp.AbstractConfigAccessors
import de.randombyte.ktskript.config.kosp.ConfigHolder
import java.nio.file.Path

class ConfigAccessors(configPath: Path) : AbstractConfigAccessors(configPath) {

    val general: ConfigHolder<GeneralConfig> = getConfigHolder("general.conf")

    override val holders = listOf(general)
}