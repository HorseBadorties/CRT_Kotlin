
import de.toto.game.NAG
import de.toto.game.getNag
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
        assertTrue(getNag("foo") == NAG.UNKNOWN)
        assertTrue(getNag("$13") == NAG.UNCLEAR_POSITION)
        assertTrue(getNag("$22") == NAG.ZUGZWANG_WHITE)
    }

}