package com.example

import com.example.internal.PersistentHashTable
import org.junit.Test
import kotlin.test.assertEquals

class HashTableTest {
    @Test
    fun kek() {
        val kek = PersistentHashTable("lol_kek")
        kek.put(100, "kek")
        assertEquals("kek", kek.get(100))
    }

    @Test
    fun kek1() {
        val kek = PersistentHashTable("lol_kek")
        kek.restore()
        assertEquals("kek", kek.get(100))
    }
}
