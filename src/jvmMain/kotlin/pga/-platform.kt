package pga

import okio.FileSystem

actual fun exitProcess(statusCode: Int): Nothing = kotlin.system.exitProcess(statusCode)

actual val systemFileSystem: FileSystem = FileSystem.SYSTEM

actual fun readPassword(): String? = System.console()?.readPassword()?.concatToString()

actual fun getenv(name: String): String? = System.getenv(name)