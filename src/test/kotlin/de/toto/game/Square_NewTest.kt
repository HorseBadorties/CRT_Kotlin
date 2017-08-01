package de.toto.game

import org.junit.Assert.*
import org.junit.Test

class Square_NewTest {

    @Test(expected = IllegalArgumentException::class)
    fun failConstructor() {
        Square_New(1,0)
        Square_New(1,9)
        Square_New(1,10)
        Square_New(0,1)
        Square_New(9,1)
        Square_New(10,1)
    }

    @Test
    fun passConstructor() {
        assertTrue(Square_New(1,1).name == "a1")
        assertNull(Square_New(1,1).piece)
    }

    @Test
    fun passFromName() {
        assertTrue(Square_New.fromName("a1").file == 1.toByte())
        assertTrue(Square_New.fromName("b1").file == 2.toByte())
        assertTrue(Square_New.fromName("c1").file == 3.toByte())
        assertTrue(Square_New.fromName("d1").file == 4.toByte())
        assertTrue(Square_New.fromName("e1").file == 5.toByte())
        assertTrue(Square_New.fromName("f1").file == 6.toByte())
        assertTrue(Square_New.fromName("g1").file == 7.toByte())
        assertTrue(Square_New.fromName("h1").file == 8.toByte())
    }

    @Test(expected = IllegalArgumentException::class)
    fun failFromName() {
        Square_New.fromName("a0")
        Square_New.fromName("a9")
        Square_New.fromName("a10")
        Square_New.fromName(" a1")
        Square_New.fromName("a1 ")
        Square_New.fromName("A1")
        Square_New.fromName("i1")
        Square_New.fromName("a")
        Square_New.fromName("1")
        Square_New.fromName("1a")
    }
}