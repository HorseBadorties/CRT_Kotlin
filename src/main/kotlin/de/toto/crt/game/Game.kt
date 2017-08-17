package de.toto.crt.game

class Game {

    val tags = mutableMapOf<String, String>()
    var currentPosition: Position = Position.fromFEN(FEN_STARTPOSITION)
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


}