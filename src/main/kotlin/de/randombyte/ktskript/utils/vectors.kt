package de.randombyte.ktskript.utils

import com.flowpowered.math.vector.Vector3d
import com.flowpowered.math.vector.Vector3i

fun v3(x: Double, y: Double, z: Double) = Vector3d(x, y, z)
fun v3(x: Int, y: Int, z: Int) = Vector3i(x, y, z)

fun Vector3i.copy(newX: Int = x, newY: Int = y, newZ: Int = z) = Vector3i(newX, newY, newZ)
fun Vector3d.copy(newX: Double = x, newY: Double = y, newZ: Double = z) = Vector3d(newX, newY, newZ)

operator fun Vector3i.rangeTo(that: Vector3i) = Vector3iRange(this, that)

/**
 * Iterates over all positions between [start] and [endInclusive].
 */
class Vector3iRange(override val start: Vector3i, override val endInclusive: Vector3i) : ClosedRange<Vector3i>, Iterable<Vector3i> {

    override fun contains(value: Vector3i) =
            (start.x..endInclusive.x).contains(value.x) &&
            (start.y..endInclusive.y).contains(value.y) &&
            (start.z..endInclusive.z).contains(value.z)

    override fun iterator(): Iterator<Vector3i> = Vector3iProgressionIterator(start, endInclusive)

    internal class Vector3iProgressionIterator(private val start: Vector3i, private val end: Vector3i): Iterator<Vector3i> {
        private var next = start

        override fun hasNext() = next.x <= end.x && next.y <= end.y && next.z <= end.z

        override fun next(): Vector3i {
            val value = next
            if (hasNext()) {
                next = next.add(1, 0, 0)
                if (next.x > end.x) next = Vector3i(start.x, next.y + 1, next.z)
                if (next.y > end.y) next = Vector3i(next.x, start.y, next.z + 1)
            }
            return value
        }
    }
}