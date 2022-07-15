package pga

import okio.*

fun Path.writeUtf8(text: String) {
  // Truncate the file to remove any trailing byte
  systemFileSystem.openReadWrite(this).resize(0)
  this.toSink().buffer().use {
    it.writeUtf8(text)
  }
}

fun Path.toSource(): Source {
  return systemFileSystem.openReadOnly(this).source()
}

fun Path.toSink(): Sink {
  return systemFileSystem.openReadWrite(this).sink()
}

fun Path.mkdirs() {
  return systemFileSystem.createDirectories(this)
}

fun Path.delete() {
  return systemFileSystem.delete(this)
}