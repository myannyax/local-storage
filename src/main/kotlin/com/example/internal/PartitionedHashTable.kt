package com.example.internal

import com.example.HashTable
import io.ktor.application.*

class PartitionedHashTable(name: String, private val partitionCount: Int = 2) : HashTable {

  private val hashTables = List(partitionCount) { PersistentHashTable("${name}_$it") }

  override suspend fun get(id: Long, call: ApplicationCall) {
    val tableId = (id.toInt() % partitionCount + partitionCount) % partitionCount
    hashTables[tableId].get(id, call)
  }

  override suspend fun put(id: Long, value: String, call: ApplicationCall) {
    val tableId = (id.toInt() % partitionCount + partitionCount) % partitionCount
    hashTables[tableId].put(id, value, call)
  }

  override fun restore() {
    hashTables.forEach { it.restore() }
  }

  override fun start() {
    hashTables.forEach { it.start() }
  }
}