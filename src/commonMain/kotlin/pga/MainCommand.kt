package pga

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class MainCommand: CliktCommand(
  name = "pin-github-actions",
  invokeWithoutSubcommand = true
) {
  init {
    subcommands(PinCommand(), UpdateCommand(), LoginCommand(), LogoutCommand())
  }

  private val version by option().flag()

  override fun run() {
    if (version) {
      echo("pin-github-actions $VERSION")
    } else {
      echo(getFormattedHelp())
    }
  }
}