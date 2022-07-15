package pga

import com.github.ajalt.clikt.core.CliktCommand

class LogoutCommand: CliktCommand() {
  override fun run() {
    deleteConfig()
  }
}