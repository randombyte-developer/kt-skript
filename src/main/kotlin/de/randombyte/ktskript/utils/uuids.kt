package de.randombyte.ktskript.utils

import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.world.World
import java.util.*

fun String.toUUID(): UUID = UUID.fromString(this)

fun UUID.getPlayer(): Player? = Sponge.getServer().getPlayer(this).orNull()
fun UUID.getUser(): User? = UserStorageService::class.service.get(this).orNull()
fun UUID.getWorld(): World? = Sponge.getServer().getWorld(this).orNull()