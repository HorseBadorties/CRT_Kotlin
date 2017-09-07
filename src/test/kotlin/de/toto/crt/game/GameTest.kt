package de.toto.crt.game

import de.toto.crt.game.rules.NAG
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Paths

class GameTest {

    @Test
    fun preOrderDepthFirst() {
        var game = fromPGN("1.e4 e5 2.Nf3 Nc6 (2...Nf6 3.Nxe5) 3.Bb5 Nf6 *")[0]
        assertEquals(game.startPosition().preOrderDepthFirst().toString(),
                "[e4, e5, Nf3, Nc6, Bb5, Nf6, Nf6, Nxe5]")
        game = fromPGN("1.e4 e5 2.Nf3 Nc6 (2...Nf6 3.Nxe5 (3.Nc3) 3...Qe7 4.Nf3) 3.Bb5 Nf6 *")[0]
        assertEquals(game.startPosition().preOrderDepthFirst().toString(),
                "[e4, e5, Nf3, Nc6, Bb5, Nf6, Nf6, Nxe5, Qe7, Nf3, Nc3]")
    }

    @Test
    fun preOrderDepthFirstFiltered() {
        val game = fromPGN("1.e4 e5 2.Nf3 Nc6 (2...Nf6 ${NAG.DUBIOUS_MOVE.nag} 3.Nxe5) 3.Bb5 Nf6 *")[0]
        assertEquals(game.startPosition().preOrderDepthFirst { !it.nags.contains(NAG.DUBIOUS_MOVE) }.
                filter { it.whiteToMove }.toString(),
                "[e5, Nc6, Nf6]")
    }

    @Test
    fun drillMoves() {
        val games = fromPGN(Paths.get(javaClass.getResource("/pgn/Repertoire_Black.pgn").toURI()))
        val repertoire = games.first()
        games.forEach { if (it !== repertoire) repertoire.mergeIn(it) }
        val drillMoves = repertoire.startPosition().preOrderDepthFirst {
//            true
            it.previous?.next?.indexOf(it) == 0 || !it.whiteToMove
//            !it.nags.contains(NAG.DUBIOUS_MOVE) || it.whiteToMove
//            (!it.nags.contains(NAG.DUBIOUS_MOVE) || it.whiteToMove) && (it.previous?.next?.indexOf(it) == 0 || !it.whiteToMove)
        }.filter { !it.whiteToMove }
        println("${drillMoves.count()}: $drillMoves")
    }

    @Test
    fun breadthFirst() {
        val game = fromPGN("1.e4 e5 2.Nf3 Nc6 (2...Nf6 3.Nxe5) 3.Bb5 Nf6 *")[0]
        assertEquals(game.startPosition().breadthFirst().toString(),
                "[e4, e5, Nf3, Nc6, Nf6, Bb5, Nxe5, Nf6]")
    }

    @Test
    fun mergeIn() {
        val games = fromPGN(Paths.get(javaClass.getResource("/pgn/Repertoire_Black.pgn").toURI()))
        val repertoire = games.first()
        games.forEach { if (it !== repertoire) repertoire.mergeIn(it) }
        println(repertoire.startPosition().breadthFirst().size)
    }


    @Test
    fun contains() {
        val game = fromPGN("e4 e5 Nf3 Nc6 (Nf6 Nxe5) Bb5 Nf6 *")[0]
        assertTrue(game.contains(game.pos("Bb5")!!))
        assertTrue(game.contains(fromPGN("e4 e5 Nf3 Nc6 (Nf6 Nxe5) Bb5 Nf6 *")[0].pos("Nf6")!!))
        assertFalse(game.contains(fromPGN("e4 e5 Nf3 Nc6 (Nf6 Nxe5) Bb5 h6 *")[0].pos("h6")!!))
    }

    @Test
    fun mergeInVariation() {
        val game1 = fromPGN("e4 e5 Nf3 Nc6 Bb5 a6 *")[0]
        val game2 = fromPGN("1.e4 e5 2.Nf3 Nc6 (2..Nf6 3.Nxe5) 3.Bb5 Nf6 *")[0]
        game1.mergeIn(game2)
        assertEquals(game1.pos("Nf3")?.next?.size, 2)
        assertEquals(game2.pos("3... Nf6")?.next?.size, 0)
    }



}

fun Game.pos(move: String) = getOrNull {
    if (move.first().isDigit()) it.movenumberMove == move else it.move == move
}