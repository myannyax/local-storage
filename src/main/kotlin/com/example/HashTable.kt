package com.example

interface HashTable {
  fun get(id: Long): String?

  fun put(id: Long, value: String, log: Boolean = true)

  fun restore()
}