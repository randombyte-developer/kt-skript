package de.randombyte.ktskript.utils

import de.randombyte.ktskript.KtSkriptPlugin
import org.spongepowered.api.CatalogType
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.util.Identifiable

inline val KtSkript: KtSkriptPlugin get() = PluginManager.getPlugin(KtSkriptPlugin.ID).get().instance.get() as KtSkriptPlugin

val Any.bestName: String get() = when {
    this is User -> name
    this is DataHolder && supports(Keys.DISPLAY_NAME) -> get(Keys.DISPLAY_NAME).get().toPlain()
    this is CatalogType -> id
    this is Identifiable -> uniqueId.toString()
    else -> this::class.java.name
}