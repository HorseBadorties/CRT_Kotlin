package de.toto.crt.game

import org.junit.Assert.*
import org.junit.Test

class SquareTest {

    @Test(expected = IllegalArgumentException::class)
    fun failConstructor() {
        Square(1,0)
        Square(1,9)
        Square(1,10)
        Square(0,1)
        Square(9,1)
        Square(10,1)
    }

    @Test
    fun passConstructor() {
        assertTrue(Square(1,1).name == "a1")
        assertTrue(Square(1,8).name == "h1")
        assertTrue(Square(8,1).name == "a8")
        assertTrue(Square(8,8).name == "h8")
    }

    @Test
    fun passFromName() {
        assertTrue(Square.fromName("a1").file == 1.toByte())
        assertTrue(Square.fromName("b1").file == 2.toByte())
        assertTrue(Square.fromName("c1").file == 3.toByte())
        assertTrue(Square.fromName("d1").file == 4.toByte())
        assertTrue(Square.fromName("e1").file == 5.toByte())
        assertTrue(Square.fromName("f1").file == 6.toByte())
        assertTrue(Square.fromName("g1").file == 7.toByte())
        assertTrue(Square.fromName("h1").file == 8.toByte())
    }

    @Test(expected = IllegalArgumentException::class)
    fun failFromName() {
        Square.fromName("a0")
        Square.fromName("a9")
        Square.fromName("a10")
        Square.fromName(" a1")
        Square.fromName("a1 ")
        Square.fromName("A1")
        Square.fromName("i1")
        Square.fromName("a")
        Square.fromName("1")
        Square.fromName("1a")
    }

    @Test
    fun isWhite() {
        assertFalse(Square.fromName("a1").isWhite)
        assertTrue(Square.fromName("a8").isWhite)
        assertTrue(Square.fromName("h1").isWhite)
        assertFalse(Square.fromName("h8").isWhite)
    }

    @Test
    fun fileName() {
        assertTrue(Square(1,1).fileName == "a")
        assertTrue(Square(1,2).fileName == "b")
        assertTrue(Square(1,3).fileName == "c")
        assertTrue(Square(1,4).fileName == "d")
        assertTrue(Square(1,5).fileName == "e")
        assertTrue(Square(1,6).fileName == "f")
        assertTrue(Square(1,7).fileName == "g")
        assertTrue(Square(1,8).fileName == "h")
    }

    @Test
    fun name() {
        assertTrue(Square(1,1).name == "a1")
        assertTrue(Square(1,8).name == "h1")
        assertTrue(Square(3,3).name == "c3")
        assertTrue(Square(8,1).name == "a8")
        assertTrue(Square(8,8).name == "h8")

        assertFalse(Square(8,8).name == "H8")
        assertFalse(Square(8,8).name == " h8")
        assertFalse(Square(8,8).name == "h 8")
    }

    @Test
    fun equals() {
        assertTrue(Square(1, 1) == Square.fromName("a1"))
        assertTrue(Square.fromName("a1") == Square.fromName("a1"))
        assertTrue(Square(1, 2) != Square.fromName("a1"))
        assertTrue(Square.fromName("a1") != Square.fromName("b1"))
    }

}