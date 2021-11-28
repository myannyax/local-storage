package com.example.internal

import com.example.HashTable
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Path
import kotlin.concurrent.thread
import kotlin.io.path.createDirectories

class PersistentHashTable(name: String, initial_size: Int = 10000) : HashTable {
  private var n: Int
  private var data: Array<Node?>

  private data class Node(val id: Long, val value: String)

  private val logFile = logDirectory.resolve(name).toFile()

  sealed interface Request

  private data class GetRequest(val id: Long, val call: ApplicationCall) : Request

  private data class PutRequest(val id: Long, val value: String, val call: ApplicationCall) : Request

  private val channel = Channel<Request>()

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

  override fun start() {
    thread {
      runBlocking {
        for (request in channel) {
          when (request) {
            is GetRequest -> {
              val res = internalGet(request.id)
              request.call.respondText(
                res ?: "not found",
                status = if (res != null) HttpStatusCode.OK else HttpStatusCode.BadRequest
              )
            }
            is PutRequest -> {
              internalPut(request.id, request.value, true)
              request.call.respond(HttpStatusCode.OK)
            }
          }
        }
      }
    }
  }

  override suspend fun get(id: Long, call: ApplicationCall) {
    channel.send(GetRequest(id, call))
  }

  override suspend fun put(id: Long, value: String, call: ApplicationCall) {
    channel.send(PutRequest(id, value, call))
  }

  internal fun internalGet(id: Long): String? {
    val pos = findPos(id)
    return if (pos == -1 || data[pos]?.id != id) null
    else data[pos]?.value
  }

  internal fun internalPut(id: Long, value: String, log: Boolean = true) {
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
    if (!logFile.exists()) {
      logFile.parentFile.toPath().createDirectories()
      logFile.createNewFile()
      logFile.appendText("$n\n")
      return
    }
    logFile.readLines().forEach {
      val split = it.split(":")
      if (split.size == 2) {
        val (id, value) = split
        internalPut(id.toLong(), value, false)
      }
    }
  }

  private fun logPut(id: Long, value: String) {
    logFile.appendText("$id:$value\n")
  }

  private fun findPos(id: Long): Int {
    var hash = (id.hashCode() % n + n) % n
    var count = 0
    while (data[hash] != null && data[hash]?.id != id) {
      hash += 1
      hash %= n
      count++
      if (count > n / 2) {
        return -1
      }
    }
    return hash
  }

  private fun resize() {
    n *= 2
    val oldData = data
    data = arrayOfNulls(n)
    for (node in oldData) {
      if (node == null) continue
      internalPut(node.id, node.value, false)
    }
  }

  companion object {
    val logDirectory = Path.of("logs")
  }
}