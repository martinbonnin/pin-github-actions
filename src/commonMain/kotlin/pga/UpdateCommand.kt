package pga

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple

class UpdateCommand : CliktCommand(
  name = "update",
  help = "For each action, update to the latest tag. If no tag exist, leaves the action unchanged"
) {
  private val files by argument(
    help = "The yaml files to process. You can also pass a directory in which case it will process all yaml files in that directory."
  ).multiple(required = true)

  override fun run() {
    process(files) {
      val tag = getLatestTag(it.owner, it.name)
      if (tag == null) {
        return@process it
      }

      return@process it.copy(
        version = tag.sha,
        comment = tag.name
      )
    }
  }
}