package net.mbonnin.pga

import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

data class ActionUsage(
  val owner: String,
  val name: String,
  val version: String,
  val comment: String?
)

internal fun process(files: List<String>, block: (ActionUsage) -> ActionUsage?) {
  files.forEach {
    val newText =  FileSystem.SYSTEM.openReadOnly(it.toPath()).use { handle ->
      handle.source().buffer().readUtf8().lines().mapIndexed { index, line ->
        val matchResult = Regex("( *- *uses: *)([A-Za-z0-9_.-]*)/([A-Za-z0-9_.-]*)@([^\\s#]+)(.*)").matchEntire(line)
        if (matchResult == null) {
          line
        } else {
          val trailing = matchResult.groupValues[5].trim()
          val comment = if (trailing.isNotEmpty()) {
            check(trailing[0] == '#') {
              "Unrecognized trailing chars: '$trailing' in line$index: '$line'"
            }
            trailing.substring(1)
          } else {
            null
          }
          val actionUsage = ActionUsage(
            owner = matchResult.groupValues[2],
            name = matchResult.groupValues[3],
            version = matchResult.groupValues[4],
            comment = comment
          )

          val newUsage = block(actionUsage)
          if (newUsage == null) {
            return@mapIndexed null
          }

          val newComment = newUsage.comment?.let { "#$it" } ?: ""

          "${matchResult.groupValues[1]}${newUsage.owner}/${newUsage.name}@${newUsage.version} $newComment"
        }
      }.joinToString("\n")
    }

    FileSystem.SYSTEM.openReadWrite(it.toPath()).use { handle ->
      handle.sink().buffer().writeUtf8(newText)
    }
  }
}