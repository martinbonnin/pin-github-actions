package pga

import com.github.ajalt.mordant.terminal.Terminal
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

data class ActionUsage(
  val owner: String,
  val name: String,
  val version: String,
  val comment: String?
)

val cache = mutableMapOf<ActionUsage, ActionUsage?>()

internal val REGEX = Regex("([ -]*uses: *)([A-Za-z0-9_.-]*)/([A-Za-z0-9_.-/]*)@([^\\s#]+)(.*)")
internal fun processLine(line: String, index: Int, block: (ActionUsage) -> ActionUsage?): String {
  val matchResult = REGEX.matchEntire(line)
  if (matchResult == null) {
    return line
  }
  val trailing = matchResult.groupValues[5].trim()
  val comment = if (trailing.isNotEmpty()) {
    check(trailing[0] == '#') {
      "Unrecognized trailing chars: '$trailing' in line$index: '$line'"
    }
    trailing.substring(1).trim()
  } else {
    null
  }

  if (comment?.startsWith("pga-ignore") == true) {
    return line
  }

  val actionUsage = ActionUsage(
    owner = matchResult.groupValues[2],
    name = matchResult.groupValues[3],
    version = matchResult.groupValues[4],
    comment = comment
  )

  val newUsage = cache.getOrPut(actionUsage) { block(actionUsage) }
  if (newUsage == null) {
    return line
  }

  val newComment = newUsage.comment?.let { "#$it" } ?: ""

  return "${matchResult.groupValues[1]}${newUsage.owner}/${newUsage.name}@${newUsage.version} $newComment"
}

internal val terminal = Terminal()

internal fun process(files: List<String>, block: (ActionUsage) -> ActionUsage?) {
  terminal.print("processing: $files...")

  files.flatMap {
    val path = it.toPath()

    val metadata = systemFileSystem.metadata(path)
    if (metadata.isDirectory) {
      systemFileSystem.listRecursively(path, followSymlinks = true).filter {
        it.name.endsWith(".yaml") || it.name.endsWith(".yml")
      }.toList()
    } else {
      listOf(path)
    }
  }.let { allPaths ->
    allPaths.forEachIndexed { index, path ->
      terminal.cursor.move {
        startOfLine()
        clearScreenAfterCursor()
      }

      terminal.print("processing [${index + 1}/${allPaths.size}]... ${path.name}")

      val newText = systemFileSystem.openReadOnly(path).use { handle ->
        handle.source().buffer().readUtf8().lines().mapIndexed { index, line ->
          processLine(line, index, block)
        }.joinToString("\n")
      }

      systemFileSystem.delete(path)
      systemFileSystem.openReadWrite(path).sink().buffer().use { buffer ->
        buffer.writeUtf8(newText)
        buffer.flush()
      }
    }
  }
  terminal.println()
  terminal.println("done.")
}