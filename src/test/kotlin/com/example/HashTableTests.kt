package com.example

import com.example.internal.PersistentHashTable
import org.junit.AfterClass
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertEquals

class HashTableTest {
    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup() {
            PersistentHashTable.logDirectory.resolve("__test").toFile().deleteRecursively()
        }
    }

    @Test
    fun simpleTest() {
        val kek = PersistentHashTable("__test/simpleTest")
        kek.internalPut(100, "kek")
        assertEquals("kek", kek.internalGet(100))
        assertEquals(null, kek.internalGet(101))
    }

    @Test
    fun stressTest() {
        val kek = PersistentHashTable("__test/stressTest")
        println(getMem())
        var random = Random(42)
        println(getMem())
        for (i in 0..10000000) {
            if (i % 100000 == 0) print("lol kek")
            val id = random.nextLong()
            kek.internalPut(id, id.toString())
        }
        println(getMem())
        random = Random(42)
        for (i in 0..10000000) {
            val id = random.nextLong()
            assertEquals(id.toString(), kek.internalGet(id))
        }
    }

    private fun getMem(): Long {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024
    }

    /*@Test
    fun testRestore() {
        val kek = PersistentHashTable("__test/stressTest")
        val list = mutableListOf<Long>()
        val random = Random(42)
        for (i in 0L..100000L) {
            val id = random.nextLong()
            list.add(id)
            kek.internalPut(id, id.toString())
        }

        for (i in list) {
            assertEquals(i.toString(), kek.internalGet(i))
        }
    }*/
}
