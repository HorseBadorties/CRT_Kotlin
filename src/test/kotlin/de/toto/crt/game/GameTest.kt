package de.toto.crt.game

import de.toto.crt.game.rules.createNextFromSAN
import de.toto.crt.game.rules.createNextFromSANs
import org.junit.Test

import org.junit.Assert.*
import java.nio.file.Paths

class GameTest {

    @Test
    fun breadthFirst() {
        val games = fromPGN(Paths.get(javaClass.getResource("/pgn/Repertoire_Black.pgn").toURI()))
        val repertoire = games.first()
        games.forEach { if (it !== repertoire) repertoire.mergeIn(it) }
        println(repertoire.startPosition().breadthFirst().map { it.moveWithMovenumber() })
    }

    @Test
    fun contains() {
        val game = fromPGN("e4 e5 Nf3 Nc6 (Nf6 Nxe5) Bb5 Nf6 *")[0]
        assertTrue(game.contains(game.pos("Bb5")))
        assertTrue(game.contains(fromPGN("e4 e5 Nf3 Nc6 (Nf6 Nxe5) Bb5 Nf6 *")[0].pos("Nf6")))
        assertFalse(game.contains(fromPGN("e4 e5 Nf3 Nc6 (Nf6 Nxe5) Bb5 h6 *")[0].pos("h6")))
    }

    @Test
    fun mergeInVariation() {
        val game1 = fromPGN("e4 e5 Nf3 Nc6 Bb5 a6 *")[0]
        val game2 = fromPGN("e4 e5 Nf3 Nc6 (Nf6 Nxe5) Bb5 Nf6 *")[0]
        game1.mergeIn(game2)
        assertEquals(game1.pos("Nf3").next.size, 2)
    }

        @Test
    fun mergeInRepertoire() {
        val games = fromPGN(Paths.get(javaClass.getResource("/pgn/Repertoire_Black.pgn").toURI()))
        val repertoire = games.first()
        games.forEach { if (it !== repertoire) repertoire.mergeIn(it) }
        repertoire.gotoStartPosition()
        println(repertoire)
    }

}

fun fromMoves(pgnMoves: String): Game {
    val result = Game()
    result.currentPosition.createNextFromSANs(pgnMoves)
    return result
}

fun Game.gotoLast(): Position {
    while (currentPosition.hasNext) next()
    return currentPosition
}

fun Game.pos(move: String) = get { it.move == move }!!