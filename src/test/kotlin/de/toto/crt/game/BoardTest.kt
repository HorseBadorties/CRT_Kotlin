package de.toto.crt.game

import org.junit.Test

import org.junit.Assert.*

class BoardTest {

    @Test
    fun squares() {
        assertTrue(Board().squares.size == 8)
        assertTrue(Board().squares[0][0].name == "a1")
        assertTrue(Board().squares[0][7].name == "h1")
        assertTrue(Board().squares[1][0].name == "a2")
        assertTrue(Board().squares[2][7].name == "h3")

    }

}