package de.toto.crt.game

import org.junit.Test
import org.junit.Assert.*
import java.nio.file.Paths

class PGNParserTest {

    @Test
    fun testTags() {
        assertTrue(
            fromPGN(Paths.get("C:\\Users\\Torsten\\Google Drive\\Schach\\Repertoire\\Repertoire_Black.pgn")).size == 47
        )
    }
}