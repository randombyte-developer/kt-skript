package de.randombyte.ktskript.extensions

import com.flowpowered.math.vector.Vector3d
import com.flowpowered.math.vector.Vector3i

fun v3(x: Double, y: Double, z: Double) = Vector3d(x, y, z)
fun v3(x: Int, y: Int, z: Int) = Vector3i(x, y, z)