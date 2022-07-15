package pga

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath
import okio.buffer

@Serializable
class Config(
  val token: String
)

val configPath = "${getenv("HOME")}/.pin-github-actions/config.json".toPath()

fun readConfig(): Config? {
  configPath.parent!!.mkdirs()
  val configStr = try {
    configPath.toSource().buffer().readUtf8()
  } catch (_: Exception) {
    return null
  }

  return Json.decodeFromString(Config.serializer(), configStr)
}

fun writeConfig(config: Config) {
  configPath.writeUtf8(Json.encodeToString(Config.serializer(), config))
}

fun deleteConfig() {
  configPath.delete()
}