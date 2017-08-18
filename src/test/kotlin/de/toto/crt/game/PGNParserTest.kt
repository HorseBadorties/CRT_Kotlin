package de.toto.crt.game

import org.junit.Test
import org.junit.Assert.*
import java.nio.file.Paths

class PGNParserTest {

    @Test
    fun testTags() {
        fromPGN(Paths.get(javaClass.getResource("/pgn/Repertoire_White.pgn").toURI()))
        fromPGN(Paths.get(javaClass.getResource("/pgn/Repertoire_Black.pgn").toURI()))
    }

    @Test
    fun testX() {
        val moves = """
1. d4 Nf6 2. c4 c5 3. d5 b5 4. e3 (4. e4 Nxe4 5. Qf3 Nd6 (5... Nf6 ${'$'}4 6. d6))
4... Qa5+ 5. Nc3 (5. Bd2 b4) (5. Nd2 bxc4 6. Bxc4 Ba6) 5... b4 6. Nce2 *
"""
        val g = fromPGN(moves)[0]
        assertTrue(g.getPosition("4... Qa5+").hasVariation("Nd2"))
        assertTrue(g.getPosition("4... Qa5+").hasVariation("Nc3"))
        assertTrue(g.getPosition("4... Qa5+").hasVariation("Bd2"))
    }
}
