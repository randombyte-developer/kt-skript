package de.randombyte.ktskript.extensions

import de.randombyte.ktskript.KtSkript
import org.spongepowered.api.CatalogType
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.util.Identifiable

internal inline val KtSkript: KtSkript get() = PluginManager.getPlugin(KtSkript.ID).get().instance.get() as KtSkript

val Any.bestName: String get() = when {
    this is User -> name
    this is DataHolder && supports(Keys.DISPLAY_NAME) -> get(Keys.DISPLAY_NAME).get().toPlain()
    this is CatalogType -> id
    this is Identifiable -> uniqueId.toString()
    else -> this::class.java.name
}