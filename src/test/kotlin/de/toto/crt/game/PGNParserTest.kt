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
            assertTrue(this.getPosition("4... Qa5+").hasVariation("Nd2"))
            assertTrue(this.getPosition("4... Qa5+").hasVariation("Nc3"))
            assertTrue(this.getPosition("4... Qa5+").hasVariation("Bd2"))
            false
        }

    }

    @Test
    fun test() {
        val moves = """
            1. d4 Nf6 2. c4 e6 3. Nc3 Bb4 4. Qc2 d5 5. a3 Bxc3+ 6. Qxc3 Ne4 7. Qc2 c5 8.
dxc5 Nc6 9. cxd5 exd5 10. e3 Bf5 11. Bd3 Qa5+ 12. b4 Nxb4 13. axb4 Qxa1 14. Ne2
Bd7 15. Nf4 Ba4 16. Qb2 Qxb2 17. Bxb2 Nf6 18. Bxf6 gxf6 19. Kd2 Kf8 20. Ra1 Bb3
21. Kc3 Bc4 22. Bxc4 dxc4 23. Kxc4 Kg7 24. b5 Rhc8 25. Nd5 f5 26. Ne7 Rc7 27.
Nxf5+ Kf6 28. Nd4 Ke5 29. Ra3 Rac8 30. Nb3 b6 31. c6 Rd8 32. Nd4 Rd5 33. f4+
Ke4 34. Ne2 Rc5+ 35. Kb4 Kf5 36. Nc3 Ke6 37. e4 f5 38. e5 a5+ 39. bxa6 R7xc6
40. Ne2 Rc4+ 41. Kb3 Re4 42. a7 Re3+ 43. Kb4 Rc4+ 44. Kxc4 Rxa3 45. Nd4+ Kf7
46. Nc6 Ke6 47. Kb5 1-0
"""
        fromPGN(moves)
    }

    @Test
    fun test2() {
        val moves = """
1. d4 Nf6 2. c4 g6 3. Nc3 Bg7 4. e4 d6 5. Nf3 O-O 6. h3 e5 7. d5 Na6 8. Be3 Nh5
9. Nh2 Qe8 10. Be2 Nf4 11. Bf3 f5 12. a3 Nc5 13. Bxc5 dxc5 14. O-O Qe7 15. Re1
a6 16. Ne2 Qd6 17. Nf1 Bd7 18. Rb1 b6 19. Nd2 Bh6 20. Nxf4 Bxf4 21. b4 Rae8 22.
Qc2 Rf6 23. Qc3 Qf8 24. Nb3 cxb4 25. axb4 Bg5 26. Rb2 Rf7 27. Nc1 Qh6 28. Nd3
fxe4 29. Bxe4 Bxh3 30. gxh3 Qxh3 31. Bg2 Qh4 32. Re4 Qh5 33. Rbe2 Ref8 34. c5
Bf4 35. Nxe5 Qh2+ 36. Kf1 Rf5 37. Nf3 Qh5 38. Re7 Bh6 39. R2e5 bxc5 40. bxc5
Rxf3 41. Bxf3 -- 42. Ke1 Qh1+ 1-0"""
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
        fromPGN(Paths.get("C:\\Users\\Torsten\\Downloads\\2015-2017.pgn"), { count++; true })
        println(count)
    }
}
