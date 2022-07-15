package net.mbonnin.pga

import com.github.ajalt.clikt.core.CliktCommand

class UpdateCommand: CliktCommand(
  name = "update",
  help = "For each action, update to the latest tag. If no tag exist, leaves the action unchanged"
) {
  override fun run() {
    TODO("Not yet implemented")
  }
}