package com.example.internal

import com.example.HashTable
import io.ktor.application.*

class PartitionedHashTable(name: String, private val partitionCount: Int = 4) : HashTable {

  private val hashTables = List(partitionCount) { PersistentHashTable("${name}_$it") }

  override suspend fun get(id: Long) : String? {
    val tableId = (id.toInt() % partitionCount + partitionCount) % partitionCount
    return hashTables[tableId].get(id)
  }

  override suspend fun put(id: Long, value: String) {
    val tableId = (id.toInt() % partitionCount + partitionCount) % partitionCount
    hashTables[tableId].put(id, value)
  }

  override fun restore() {
    hashTables.forEach { it.restore() }
  }
}