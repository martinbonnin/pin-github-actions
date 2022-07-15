package pga

import com.github.ajalt.clikt.core.CliktCommand

class LoginCommand: CliktCommand(
  help = "Authenticate your calls to the GitHub APIs to get a higher rate limit"
) {
  override fun run() {
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
}