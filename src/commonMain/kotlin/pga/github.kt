package pga

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.serialization.json.*

val client = HttpClient(CIO)

/**
 * @return the sha of a given tag or branch or null if it isn't a tag or branch
 *
 * @throws Exception if there is a network error or if the response cannot be handled
 */
fun getSha(owner: String, name: String, tagOrBranch: String): String? {
  getShaFromRef(owner, name, "heads/$tagOrBranch")?.let { return it }
  getShaFromRef(owner, name, "tags/$tagOrBranch")?.let { return it }

  return null
}

/**
 * @return the latest tag for a repo or null if there are no tags
 *
 * @throws Exception if there is a network error or if the response cannot be handled
 */
fun getLatestTag(owner: String, name: String): Tag? {
  val json = getJson("https://api.github.com/repos/$owner/$name/tags")
  if (json !is List<*>) {
    error("Unexpected response from the GitHub API: $json")
  }
  if (json.isEmpty()) {
    // No tags
    return null
  }
  val tag = json.asList[0].asMap
  return Tag(
    tag["name"].asString,
    tag["commit"].asMap["sha"].asString
  )
}

data class Tag(val name: String, val sha: String)

/**
 * Gets the sha of a given ref or null if it doesn't exist
 *
 * @throws Exception if there is a network error or if the response cannot be handled
 */
private fun getShaFromRef(owner: String, name: String, ref: String): String? {
  val json = getJson("https://api.github.com/repos/$owner/$name/git/matching-refs/$ref")
  if (json !is List<*>) {
    error("Unexpected response from the GitHub API: $json")
  }
  if (json.isEmpty()) {
    // This ref does not exist
    return null
  }
  return json.asList[0].asMap["object"].asMap["sha"].asString
}

private fun getJson(url: String): Any? {
  return runBlocking {
    val response = client.get(url) {
      val token = readConfig()?.token
      if (token != null) {
        header("Authorization", "token $token")
      }
    }

    if (response.status.value / 100 != 2) {
      if (response.status.value == 403) {
        if (response.headers.get("x-ratelimit-limit") == response.headers.get("x-ratelimit-used")) {
          val expires = response.headers.get("x-ratelimit-reset")?.toLongOrNull()?.let {
            Instant.fromEpochSeconds(it)
          }?.let {
            "until '$it'"
          } ?: ""
          println(
            """You have reached the GitHub unauthenticated rate limit, please either:
            |- wait $expires
            |- or authenticate with `pin-github-actions login`
          """.trimMargin()
          )
          exitProcess(1)
        }
      }
      error("Received HTTP ${response.status.value} from ${response.request.url}")
    }

    Json.parseToJsonElement(response.body()).toAny()
  }
}
