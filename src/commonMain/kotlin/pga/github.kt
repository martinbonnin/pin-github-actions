package pga

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.serialization.json.*


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
fun getLatestTag(owner: String, path: String): Tag? {
  val repoName = path.substringBefore('/')
  val json = getJson("https://api.github.com/repos/$owner/$repoName/tags").getOrThrow()
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
internal fun getShaFromRef(owner: String, path: String, ref: String): String? {
  //println("getShaFromRef($owner, $name, $ref)")
  /**
   * Some repositories contain several actions
   * See https://github.com/orgs/community/discussions/24990
   * See https://github.com/gradle/actions
   */
  val repoName = path.substringBefore('/')
  val result = getJson("https://api.github.com/repos/$owner/$repoName/git/ref/$ref")
  return when (result) {
    JsonNotFound -> {
      null
    }
    is JsonSuccess -> {
      val json = result.data
      if (json !is Map<*, *>) {
        error("Unexpected response from the GitHub API: $json")
      }
      json.get("object")?.asMap?.get("sha")?.asString
    }
  }
}

sealed interface JsonResult {
  fun getOrThrow(): Any? {
    if (this !is JsonSuccess) {
      error("Json not found")
    }
    return data
  }
}

class JsonSuccess(val data: Any?): JsonResult
object JsonNotFound: JsonResult

private fun getJson(url: String): JsonResult {
  return runBlocking {
    val response = httpClient.get(url) {
      val token = readConfig()?.token
      if (token != null) {
        header("Authorization", "token $token")
      }
    }

    if (response.status.value / 100 != 2) {
      when (response.status.value) {
          403 -> {
            if (response.headers.get("x-ratelimit-limit") == response.headers.get("x-ratelimit-used")) {
              val expires = response.headers.get("x-ratelimit-reset")?.toLongOrNull()?.let {
                Instant.fromEpochSeconds(it)
              }?.let {
                "until '$it'"
              } ?: ""
              println(
                """You have reached the GitHub unauthenticated rate limit, please either:
              |- wait $expires
              |- or authenticate with `pin-github-actions --login`
            """.trimMargin()
              )
              exitProcess(1)
            }
          }
          404 -> {
            return@runBlocking JsonNotFound
          }
      }
      error("Received HTTP ${response.status.value} from ${response.request.url}")
    }

    return@runBlocking JsonSuccess(Json.parseToJsonElement(response.body()).toAny())
  }
}
