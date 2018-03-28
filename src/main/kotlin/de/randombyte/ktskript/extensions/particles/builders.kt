package de.randombyte.ktskript.extensions.particles

import com.flowpowered.math.vector.Vector3d
import org.spongepowered.api.block.BlockState
import org.spongepowered.api.data.type.NotePitch
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.particle.ParticleOptions
import org.spongepowered.api.effect.particle.ParticleTypes
import org.spongepowered.api.item.FireworkEffect
import org.spongepowered.api.util.Color

fun particleEffect(configurator: ParticleEffect.Builder.() -> Unit) = ParticleEffect.builder().apply {
    // setting some default values, todo remove?
    type(ParticleTypes.REDSTONE_DUST)
    velocity(Vector3d(0.5, 0.5, 0.5))
    quantity(10)
    offset(Vector3d.ZERO)

    configurator.invoke(this)
}.build()

fun ParticleEffect.Builder.blockState(blockState: BlockState) = option(ParticleOptions.BLOCK_STATE, blockState)
fun ParticleEffect.Builder.color(color: Color) = option(ParticleOptions.COLOR, color)
fun ParticleEffect.Builder.note(notePitch: NotePitch) = option(ParticleOptions.NOTE, notePitch)
fun ParticleEffect.Builder.fireworkEffects(fireworksEffects: List<FireworkEffect>) = option(ParticleOptions.FIREWORK_EFFECTS, fireworksEffects)

fun fireworkEffect(configurator: FireworkEffect.Builder.() -> Unit) = FireworkEffect.builder().apply {
    configurator.invoke(this)
}.build()