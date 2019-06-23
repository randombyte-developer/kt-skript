package de.randombyte.ktskript.config.kosp

import de.randombyte.ktskript.utils.toConfigurationLoader
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection
import java.nio.file.Files
import java.nio.file.Path

/**
 * To be overwritten to have fields for various [ConfigHolder]s.
 */
abstract class AbstractConfigAccessors(val configPath: Path) {

    init {
        if (Files.notExists(configPath)) {
            Files.createDirectories(configPath)
        }
    }

    abstract val holders: List<ConfigHolder<*>>

    fun reloadAll() {
        holders.forEach(ConfigHolder<*>::reload)
        reloadedAll()
    }

    /**
     * Called when all configs were reloaded.
     */
    open fun reloadedAll() { }

    protected inline fun <reified T : Any> getConfigHolder(
            configName: String,
            noinline additionalSerializers: TypeSerializerCollection.() -> Any = {}
    ) = ConfigManager(
            configPath.resolve(configName).toConfigurationLoader(),
            T::class.java,
            additionalSerializers = additionalSerializers
    ).toConfigHolder()
}