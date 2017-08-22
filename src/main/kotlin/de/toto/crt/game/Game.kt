package de.toto.crt.game

import de.toto.crt.game.rules.FEN_STARTPOSITION

import de.toto.crt.game.rules.createNextFromSAN
import de.toto.crt.game.rules.fromFEN

class Game {

    val tags = mutableMapOf<String, String>()

    var currentPosition: Position = fromFEN(FEN_STARTPOSITION)
        private set

    fun startVariation() = back()

    fun endVariation(): Position {
        // find previous with variationLevel - 1 and then to his next in main line
        while (currentPosition.variationLevel == currentPosition.previous?.variationLevel) {
            back()
        }
        back()
        return next()
    }

    fun startWithFen(fen: String): Position {
        currentPosition = fromFEN(fen)
        return currentPosition
    }

    fun addMove(san: String, asMainline: Boolean = !currentPosition.hasNext): Position {
        currentPosition = currentPosition.createNextFromSAN(san, asMainline)
        return currentPosition
    }

    fun back(): Position {
        currentPosition = currentPosition.previous ?: currentPosition
        return currentPosition
    }

    fun next(): Position {
        currentPosition = if (currentPosition.hasNext) currentPosition.next.first() else currentPosition
        return currentPosition
    }

    fun startPosition(): Position {
        var result = currentPosition
        while (result.previous != null) result = result.previous ?: result
        return result
    }

    fun gotoStartPosition(): Position {
        currentPosition = startPosition()
        return currentPosition
    }

    fun mergeIn(otherGame: Game) {
        var ourPos = startPosition()
        var otherPos = otherGame.startPosition()

        while (otherPos.hasNext) {
            otherPos = otherPos.next.first()
            val idx = ourPos.next.indexOf(otherPos)
            if (idx > -1) {
                ourPos = ourPos.next[idx]
            } else {
                for (otherVariation in otherPos.previous!!.next) {
                    if (otherVariation !in ourPos.next) {
                        ourPos.next.add(otherVariation)
                        otherVariation.comment = otherGame.toString()
                    }
                }
                break
            }
        }
    }

}