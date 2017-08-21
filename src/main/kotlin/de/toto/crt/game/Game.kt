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

    fun getStartPosition(): Position {
        var result = currentPosition
        while (result.previous != null) result = result.previous ?: result
        return result
    }

    fun getPosition(sanWithMoveNumber: String): Position {
        var result = getStartPosition()
        while (result.moveWithMovenumber != sanWithMoveNumber) {
            if (!result.hasNext) {
                throw IllegalArgumentException(
                        "move $sanWithMoveNumber does not exist")
            } else {
                result = result.next.first()
            }
        }
        return result
    }

    fun gotoPosition(sanWithMoveNumber: String): Position {
        currentPosition = getPosition(sanWithMoveNumber)
        return currentPosition
    }

    fun hasTag(tagName: String) = tags.containsKey(tagName)

    fun getTag(tagName: String) = tags[tagName]

}