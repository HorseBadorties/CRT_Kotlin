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
[Event "Fernando 06.10"]
[Site "?"]
[Date "????.??.??"]
[Round "?"]
[White "Anti-Blumenfeld"]
[Black "2.Nf3 3.Nc3"]
[Result "*"]
[ECO "A43"]
[Annotator "Torsten"]
[PlyCount "15"]

1. d4 Nf6 2. Nf3 c5 3. d5 e6 4. Nc3 {#REP} exd5 5. Nxd5 Nxd5 6. Qxd5 Nc6 7. e4
d6 8. Ng5 (8. Bc4 Be6 9. Qd3) (8. Bg5) *
"""
        assertTrue(fromPGN(moves).size  == 1)
    }
}