package de.randombyte.ktskript.script

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner
import org.jetbrains.kotlin.script.util.classpathFromClassloader
import org.jetbrains.kotlin.utils.PathUtil

fun getAsMuchClasspathAsPossible() = FastClasspathScanner().findBestClassLoader()
        .flatMap { classpathFromClassloader(it)!! } + PathUtil.getJdkClassesRootsFromCurrentJre()