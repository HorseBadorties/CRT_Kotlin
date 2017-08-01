package de.toto.game

import org.junit.Assert.*
import org.junit.Test

class Square_NewTest {

    @Test(expected = IllegalArgumentException::class)
    fun failConstructor() {
        Square_New("a9")
        Square_New("i8")
        Square_New(" a1")
        Square_New("a1 ")
        Square_New("_a1")
        Square_New(" a1 ")

        Square_New(1,0)
        Square_New(1,9)
        Square_New(1,10)
        Square_New(0,1)
        Square_New(9,0)
        Square_New(10,0)
    }

    @Test
    fun passConstructor() {
        assertTrue(Square_New("a1").file == 1.toByte())
        assertTrue(Square_New("b1").file == 2.toByte())
        assertTrue(Square_New("c1").file == 3.toByte())
        assertTrue(Square_New("d1").file == 4.toByte())
        assertTrue(Square_New("e1").file == 5.toByte())
        assertTrue(Square_New("f1").file == 6.toByte())
        assertTrue(Square_New("g1").file == 7.toByte())
        assertTrue(Square_New("h1").file == 8.toByte())

        assertTrue(Square_New(1,1).name == "a1")
        assertNull(Square_New(1,1).piece)
    }
}