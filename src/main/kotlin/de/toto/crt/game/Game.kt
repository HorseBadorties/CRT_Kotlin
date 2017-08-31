package de.toto.crt.game

import de.toto.crt.game.rules.FEN_STARTPOSITION

import de.toto.crt.game.rules.createNextFromSAN
import de.toto.crt.game.rules.fromFEN
import java.util.*

class Game {

    val listener = mutableListOf<GameListener>()

    val tags = mutableMapOf<String, String>()

    var currentPosition: Position = fromFEN(FEN_STARTPOSITION)
        private set

    fun startVariation(): Position {
        currentPosition = currentPosition.previous ?: currentPosition
        return currentPosition
    }

    fun endVariation(): Position {
        // find previous with variationLevel - 1 and then to his next in main line
        while (currentPosition.variationLevel == currentPosition.previous?.variationLevel) {
            currentPosition = currentPosition.previous ?: currentPosition
        }
        currentPosition = currentPosition.previous ?: currentPosition
        currentPosition = currentPosition.next.first()
        return currentPosition
    }

    fun startWithFen(fen: String): Position {
        return gotoPosition(fromFEN(fen))
    }

    fun addMove(san: String, asMainline: Boolean = !currentPosition.hasNext): Position {
        currentPosition = currentPosition.createNextFromSAN(san, asMainline)
        return currentPosition
    }

    fun back(): Position {
        return gotoPosition(currentPosition.previous ?: currentPosition)
    }

    fun next(): Position {
        return gotoPosition(if (currentPosition.hasNext) currentPosition.next.first() else currentPosition)
    }

    fun startPosition(): Position {
        var result = currentPosition
        while (result.previous != null) result = result.previous ?: result
        return result
    }

    fun gotoStartPosition(): Position {
        return gotoPosition(startPosition())
    }

    fun gotoPosition(pos: Position): Position {
        currentPosition = pos
        listener.forEach { it.positionChanged() }
        return currentPosition
    }


    /**
     * Return the first Position that matches the `predicate`,
     * or `null` if no match was found
     */
    fun get(predicate: (Position) -> Boolean): Position? {
        val queue = LinkedList<Position>()
        queue.add(startPosition())
        while (!queue.isEmpty()) {
            val p = queue.poll()
            if (predicate(p)) return p
            for (n in p.next) {
                queue.add(n)
            }
        }
        return null
    }

    fun contains(pos: Position):Boolean = get { it == pos } != null

    fun mergeIn(otherGame: Game) {
        fun find(pos: Position?) = get { it == pos }

        otherGame.startPosition().breadthFirst().forEach { otherPos ->
            if (!contains(otherPos)) {
                val ourVariationStart = find(otherPos.previous)
                ourVariationStart?.next?.add(otherPos)
                otherPos.previous = ourVariationStart
            }
        }
    }

}

interface GameListener {
    fun positionChanged()
}