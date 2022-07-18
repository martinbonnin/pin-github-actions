package pga

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class MainCommand: CliktCommand(
  name = "pin-github-actions",
  invokeWithoutSubcommand = true
) {
  private val version by option().flag()
  private val login by option().flag()
  private val logout by option().flag()
  private val update by option(help = "update to the latest known tag instead of just using the current").flag()

  private val paths by argument(
    help = "The yaml files/directories to process. You can also pass a directory in which case it will process all yaml files in that directory."
  ).multiple()

  override fun run() {
    when {
      version -> echo("pin-github-actions $VERSION")
      login -> login()
      logout -> logout()
      else -> {
        if (paths.isEmpty()) {
          echo(getFormattedHelp())
          exitProcess(1)
        }
        if (update) {
          update(paths)
        } else {
          pin(paths)
        }
      }
    }
  }
}


private fun update(paths: List<String>) {
  process(paths) {
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

private fun pin(paths: List<String>) {
  process(paths, ::pinCallback)
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

private fun login() {
  println("token: ")
  // Uncomment when https://github.com/ajalt/mordant/pull/63 is released
  // val token = terminal.readLineOrNull(true)
  val token = readPassword()
  check (token != null) {
    println("Cannot read password")
  }

  writeConfig(Config(token))

  terminal.println("Your calls will now be authenticated")
}

private fun logout() {
  deleteConfig()
}