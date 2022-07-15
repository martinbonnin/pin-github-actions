package pga

import kotlinx.cinterop.*
import okio.FileSystem
import platform.posix.*

actual val systemFileSystem = FileSystem.SYSTEM
actual fun exitProcess(statusCode: Int): Nothing = kotlin.system.exitProcess(statusCode)
actual fun readPassword(): String? {
  updateTermios {
    c_lflag = c_lflag.and(ECHO.inv().toULong())
  }
  val password = readLine()
  updateTermios {
    c_lflag = c_lflag.or(ECHO.toULong())
  }
  return password
}

private fun updateTermios(block:termios.() -> Unit) {
  memScoped {
    val termios = alloc<termios>()
    check(tcgetattr(0, termios.ptr) == 0) {
      error("tcgetattr() error: $errno")
    }

    block(termios)

    check(tcsetattr(0, TCSADRAIN, termios.ptr) == 0) {
      error("tcsetattr() error: $errno")
    }
  }
}

actual fun getenv(name: String): String? {
  return platform.posix.getenv(name)?.toKString()
}