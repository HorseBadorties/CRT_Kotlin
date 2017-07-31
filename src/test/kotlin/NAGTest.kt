
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
    fun getByNag() {
        assertTrue(NAG.getByNag("foo") == NAG.UNKNOWN)
        assertTrue(NAG.getByNag("$13") == NAG.UNCLEAR_POSITION)
        assertTrue(NAG.getByNag("$22") == NAG.ZUGZWANG_WHITE)
    }

}