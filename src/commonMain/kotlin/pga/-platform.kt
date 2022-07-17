package pga

import io.ktor.client.*
import okio.FileSystem

expect fun readPassword(): String?
expect fun exitProcess(statusCode: Int): Nothing
expect fun getenv(name: String): String?
expect val systemFileSystem: FileSystem
expect val httpClient: HttpClient