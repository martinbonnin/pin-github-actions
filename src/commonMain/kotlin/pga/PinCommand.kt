package pga

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple

class PinCommand : CliktCommand(
  name = "pin",
  help = "replaces tags and branches references by their actual sha"
) {
  private val files by argument(
    help = "The yaml files to process. You can also pass a directory in which case it will process all yaml files in that directory."
  ).multiple()

  override fun run() {
    val paths = if (files.isEmpty()) {
      listOf(".github/workflows/")
    } else {
      files
    }
    process(paths, ::pinCallback)
  }
}


internal fun pinCallback(actionUsage: ActionUsage): ActionUsage? {
  val sha = getSha(actionUsage.owner, actionUsage.name, actionUsage.version)

  return if (sha == null) {
    null
  } else {
    actionUsage.copy(
      version = sha,
      comment = actionUsage.version
    )
  }
}