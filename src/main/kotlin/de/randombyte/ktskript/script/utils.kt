package de.randombyte.ktskript.script

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner
import org.jetbrains.kotlin.script.util.classpathFromClassloader
import org.jetbrains.kotlin.utils.PathUtil
import org.spongepowered.api.Sponge
import java.io.File

fun getAsMuchClasspathAsPossible(): List<File> {
    val normalClassLoaders = FastClasspathScanner().findBestClassLoader() + Sponge::class.java.classLoader
    return normalClassLoaders.flatMap { classpathFromClassloader(it)!! } + PathUtil.getJdkClassesRootsFromCurrentJre()
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D) : ComponentTU