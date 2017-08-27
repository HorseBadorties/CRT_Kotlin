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
        with (fromPGN(moves).first()) {
            assertTrue(this.getPosition("4...Qa5+").hasVariation("Nd2"))
            assertTrue(this.getPosition("4...Qa5+").hasVariation("Nc3"))
            assertTrue(this.getPosition("4...Qa5+").hasVariation("Bd2"))
            false
        }

    }

    @Test
    fun test() {
        val moves = """
            1. d4 Nf6 2. c4 c5 3. d5 b5 4. cxb5 a6 5. Nc3 axb5 6. e4 (6. Nxb5 Qa5+ 7. Nc3
Bb7 8. Bd2 Qb6 9. e4 e6 10. Bc4 Nxe4) 6... Qa5 {recommended by GM Ramirez} (
6... b4 7. Nb5 d6 (7... Nxe4 ${'$'}4 8. Qe2 {[%cal Gb5d6]} f5 9. f3) 8. Bd3 (8. Bf4
g5 ${'$'}1 9. Bxg5 Nxe4 10. Bf4 Nf6 ${'$'}1 {[%cal Gb8d7,Gf8g7,Ge8g8,Gh8f8]} 11. Qe2 Ra6)
(8. Bc4 Nbd7 (8... Nxe4 ${'$'}6 9. Qe2 Nf6 10. Bf4 ${'$'}16 Ra6 11. Nxd6+ Rxd6 12. Bb5+)
9. Nf3 Nb6) (8. Nf3 Nxe4 9. Bc4 g6 10. Qe2 f5) 8... e6 9. dxe6 Bxe6 10. a4 bxa3
11. Rxa3 Rxa3 12. Nxa3 Be7 ${'$'}11 {[%cal Ge8g8,Gh8f8,Gb8c6]}) 7. Bd2 b4 8. e5 (8.
Nb5 Nxe4 9. Qe2 Ba6 10. Qxe4 Bxb5 11. Bxb5 Qxb5 12. d6 Nc6) 8... bxc3 9. Bxc3
Qa4 (9... Qb6 10. exf6 gxf6 11. Nf3 d6 12. Be2 Nd7 13. O-O Bh6 14. a4) 10. Qxa4
Rxa4 11. exf6 gxf6 {[%csl Rd5][%cal Rh8g8,Rg8g2,Ra4a2]} *
"""
        val game = fromPGN(moves).first()
    }

    @Test
    fun skipInvalidGame() {
        val moves = """
[Event "20th OIBM 2016"]
1. d4 Nf6 2. c4 g6 3. Nc3 Bg7 4. e4 d6 5. Nf3 O-O 6. h3 e5 7. d5 Na6 8. Be3 Nh5
9. Qe8 10. Be2 Nf4 1-0

[Event "20th OIBM 2016"]
1. d4 Nf6 1-0"""
        fromPGN(moves)
    }

    @Test
    fun fen() {
        val pgn = """
            [Event "20th OIBM 2016"]
[Site "Bad Wiessee GER"]
[Date "2016.11.04"]
[Round "7.6"]
[White "Kozul, Z."]
[Black "Kveinys, A."]
[Result "1/2-1/2"]
[ECO "D10"]
[WhiteElo "2619"]
[BlackElo "2509"]
[SetUp "1"]
[FEN "r3kb1r/1p1n1pp1/pPp1pn1p/2Pp1b2/1P1P4/2N1PN2/5PPP/R1B1KB1R b KQkq - 0 12"]
[PlyCount "59"]
[EventDate "2016.10.29"]

12... O-O-O 13. Bb2 Ne4 14. Nxe4 Bxe4 15. Nd2 Bc2 16. Be2 g5 17. e4 Bg7 18. f3
Nb8 19. O-O dxe4 20. fxe4 Bxd4+ 21. Bxd4 Rxd4 22. Nc4 f5 23. exf5 exf5 24. Rac1
Be4 25. Rcd1 Rhd8 26. Rxd4 Rxd4 27. Nd6+ Kd7 28. Nxe4 Rxe4 29. Bd3 Rxb4 30.
Bxf5+ Ke7 31. Re1+ Kf7 32. Be6+ Kf8 33. Bc8 Rc4 34. Bxb7 Rxc5 35. Bxa6 Nxa6 36.
Ra1 Rb5 37. Rxa6 Ke7 38. Ra7+ Kd6 39. Rh7 Rxb6 40. Rxh6+ Ke5 41. Kf2 Kf5
1/2-1/2"""
        fromPGN(pgn)
    }

    @Test
    fun testBig() {
        var count = 0
//        fromPGN(Paths.get("C:\\Users\\080064\\Downloads\\twic_BULK.pgn"), { count++; true })
        fromPGN(Paths.get("C:\\Users\\Torsten\\Downloads\\2015-2017.pgn"), { count++; true })
        println(count)
    }
}

fun Position.hasVariation(san: String) = next.any { it.move == san }

fun Game.getPosition(sanWithMoveNumber: String): Position {
    var result = startPosition()
    while (result.moveWithMovenumber() != sanWithMoveNumber) {
        if (!result.hasNext) {
            throw IllegalArgumentException("move $sanWithMoveNumber does not exist")
        } else {
            result = result.next.first()
        }
    }
    return result
}
