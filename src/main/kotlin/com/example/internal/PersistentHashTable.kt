package com.example.internal

import com.example.HashTable
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories

class PersistentHashTable(val name: String, initial_size: Int = 10000) : HashTable {
  private var n: Int
  private var data: Array<Node?>

  private data class Node(val id: Long, val value: String)

  private var currentLogId = 0

  private var logFile = logDirectory.resolve("${name}_$currentLogId").toFile()

  private val lock = Mutex()

  private var size = 0

  init {
    Files.createDirectories(logDirectory)
    n = initial_size
    if (logFile.exists()) {
      val line = logFile.useLines { it.firstOrNull() }
      n = line?.toInt() ?: initial_size
    }
    data = arrayOfNulls(n)
    restore()
  }

  override suspend fun get(id: Long): String? {
    val res = lock.withLock {
      internalGet(id)
    }
    return res
  }

  override suspend fun put(id: Long, value: String) {
    lock.withLock {
      internalPut(id, value, true)
    }
  }

  internal fun internalGet(id: Long): String? {
    val pos = findPos(id)
    return if (pos == -1 || data[pos]?.id != id) null
    else data[pos]?.value
  }

  internal fun internalPut(id: Long, value: String, log: Boolean = true) {
    size++
    if (n * 0.75 <= size) {
      resize()
    }
    val pos = findPos(id)
    data[pos] = Node(id, value)
    if (log) logPut(id, value)
  }

  override fun restore() {
    if (!logFile.exists()) {
      logFile.parentFile.toPath().createDirectories()
      logFile.createNewFile()
      logFile.appendText("$n\n")
      return
    }
    while (true) {
      var shouldStop = true
      logFile.readLines().forEach {
        val split = it.split(":")
        if (split.size == 2) {
          val (id, value) = split
          internalPut(id.toLong(), value, false)
        } else if (it == "*") {
          shouldStop = false
          nextLogFile()
        }
      }
      if (shouldStop) break
    }
  }

  private fun logPut(id: Long, value: String) {
    if (logFile.length() / 1024 / 1024 > 20) {
      logFile.appendText("*\n")
      nextLogFile()
    }
    logFile.appendText("$id:$value\n")
  }

  private fun nextLogFile() {
    currentLogId++
    logFile = logDirectory.resolve("${name}_$currentLogId").toFile()
  }

  private fun findPos(id: Long): Int {
    var hash = (id.hashCode() % n + n) % n
    var count = 0
    while (data[hash] != null && data[hash]?.id != id) {
      hash += 1
      hash %= n
      count++
    }
    return hash
  }

  private fun resize() {
    n = (n.toFloat() * 1.66).toInt()
    val oldData = data
    data = arrayOfNulls(n)
    for (node in oldData) {
      if (node == null) continue
      internalPut(node.id, node.value, false)
    }
  }

  companion object {
    val logDirectory: Path = Path.of("logs")
  }
}