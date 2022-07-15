package net.mbonnin.pga

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple

class PinCommand: CliktCommand(
  name = "pin",
  help = "replaces tags and branches references by their actual sha1"
) {
  val files by argument().multiple(required = true)

  override fun run() {

    TODO("Not yet implemented")
  }
}