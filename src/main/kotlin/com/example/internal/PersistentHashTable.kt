package com.example.internal

import com.example.HashTable
import java.nio.file.Files
import java.nio.file.Path

class PersistentHashTable(name: String) : HashTable {
  private var data = arrayOfNulls<Node>(N)

  private data class Node(val id: Long, val value: String)

  private val logFile = logDirectory.resolve(name).toFile()

  init {
    Files.createDirectories(logDirectory)
    logFile.createNewFile()
  }

  override fun get(id: Long): String? {
    val pos = findPos(id)
    return if (pos == -1 || data[pos]?.id != id) null
    else data[pos]?.value
  }

  override fun put(id: Long, value: String, log: Boolean) {
    var pos = findPos(id)
    if (pos == -1) {
      resize()
      pos = findPos(id)
      assert(pos != -1)
    }
    data[pos] = Node(id, value)
    if (log) logPut(id, value)
  }

  override fun restore() {
    logFile.readLines().forEach {
      val (id, value) = it.split(":")
      put(id.toLong(), value, false)
    }
  }

  private fun logPut(id: Long, value: String) {
    logFile.appendText("$id:$value\n")
  }

  private fun findPos(id: Long): Int {
    var hash = id.hashCode() % N
    var count = 0
    while (data[hash] != null && data[hash]?.id != id) {
      hash += 1
      hash %= N
      count++
      if (count > N / 2) {
        return -1
      }
    }
    return hash
  }

  private fun resize() {
    N *= 2
    val oldData = data
    data = arrayOfNulls(N)
    for (node in oldData) {
      if (node == null) continue
      put(node.id, node.value, false)
    }
  }

  companion object {
    private var N = 10000
    private val logDirectory = Path.of("logs")
  }
}