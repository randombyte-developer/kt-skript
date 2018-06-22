package de.randombyte.ktskript.config.kosp

/**
 * Caches a config file, backed by a [ConfigManager].
 */
open class ConfigHolder<T : Any>(val configManager: ConfigManager<T>) {
    private lateinit var config: T

    /**
     * Called when the config was reloaded.
     */
    open fun reloaded() { }

    fun reload() {
        save(configManager.load()) // generates the config automatically
        reloaded()
    }

    fun get() = config

    fun save(config: T) {
        this.config = config
        configManager.save(config)
    }
}

fun <T : Any> ConfigManager<T>.toConfigHolder(): ConfigHolder<T> = ConfigHolder(this)