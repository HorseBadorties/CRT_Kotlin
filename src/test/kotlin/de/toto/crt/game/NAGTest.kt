package de.toto.crt.game
import org.junit.Test

import org.junit.Assert.*

class NAGTest {
    @Test
    fun testToString() {
        NAG.values().forEach { assertEquals(it.toString(), it.pgn) }
    }

    @Test
    fun isPositionEval() {
        assertFalse(NAG.DUBIOUS_MOVE.isPositionEval)
        assertFalse(NAG.UNKNOWN.isPositionEval)
        assertTrue(NAG.ADVANTAGE_BLACK.isPositionEval)
    }

    @Test
    fun getNag() {
        assertTrue(NAG.getNag("foo") == NAG.UNKNOWN)
        assertTrue(NAG.getNag("$13") == NAG.UNCLEAR_POSITION)
        assertTrue(NAG.getNag("$22") == NAG.ZUGZWANG_WHITE)
    }

}