package pga

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class MainCommand: CliktCommand() {
  init {
    subcommands(PinCommand(), UpdateCommand(), LoginCommand(), LogoutCommand())
  }

  override fun run() {

  }
}