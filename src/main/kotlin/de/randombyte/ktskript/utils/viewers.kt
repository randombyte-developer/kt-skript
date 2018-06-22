package de.randombyte.ktskript.utils

import com.flowpowered.math.vector.Vector3i
import org.spongepowered.api.effect.Viewer
import org.spongepowered.api.effect.particle.ParticleEffect

// todo remove?
fun Viewer.spawnParticles(particleEffect: ParticleEffect, position: Vector3i) = spawnParticles(particleEffect, position.toDouble())