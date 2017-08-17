package de.toto.crt.game

import org.junit.Test
import org.junit.Assert.*
import java.nio.file.Paths

class PGNParserTest {

    @Test
    fun testTags() {
        assertTrue(
            fromPGN(Paths.get("C:\\Users\\080064\\Downloads\\Repertoire_Black.pgn")).size == 47
        )
    }
}