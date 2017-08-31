package de.toto.crt.game

import de.toto.crt.game.rules.NAG
import java.nio.file.*

fun fromPGN(path: Path, listener: (Game) -> Boolean) = PGNParser().parse(path, listener)

fun fromPGN(path: Path): List<Game> {
    val result = mutableListOf<Game>()
    PGNParser().parse(path, { result.add(it) })
    return result
}

fun fromPGN(pgn: String): List<Game> {
    val result = mutableListOf<Game>()
    PGNParser().parse(pgn, { result.add(it) })
    return result
}

private class PGNParser {

    var lineNumber = 0
    var startOfFile = true
    var inMovetext = false
    var inComment = false
    var comment = StringBuilder()
    var game = Game()

    private lateinit var listener: (Game) -> Boolean

    fun parse(path: Path, listener: (Game) -> Boolean) {
        this.listener = listener
        for (line in Files.lines(path, Charsets.UTF_8)) {
            if (!parseLine(line)) return
        }
    }

    fun parse(string: String, listener: (Game) -> Boolean) {
        this.listener = listener
        string.lines().forEach {
            if (!parseLine(it)) return
        }
    }

    private fun parseLine(line: String): Boolean {
        lineNumber++
        try {
            var s = line.trim()
            if (s.isEmpty()) return true
            if (startOfFile) {
                if (s.contains('[')) {
                    // Drop first 3 ChessBase "special characters"
                    s = s.dropWhile { it != '[' }
                }
                startOfFile = false
            }
            if (inMovetext) {
                if (!parseMovetext(s)) return false
            } else {
                if (s.startsWith('[') && s.endsWith(']')) {
                    with (s.dropLast(1).drop(1)) {
                        game.tags.put(substringBefore(' '),
                                substringAfter(' ').replace("\"", ""))
                    }
                } else {
                    inMovetext = true
                    if ("FEN" in game.tags) {
                        game.startWithFen(game.tags["FEN"] ?: "")
                    }
                    parseMovetext(s)
                }
            }
            return true
        } catch (e: Exception) {
            throw Exception("error on line $lineNumber", e)
        }
    }

    private fun parseMovetext(line: String): Boolean {
        for (token in line.split(regex)) {
            if (!inComment) {
                when {
                    token.isBlank() -> { /* do nothing */ }
                    token.isMoveNumber -> { /* do nothing */ }
                    token.isNAG -> game.currentPosition.nags.add(NAG.getNag(token))
                    token.isGameResult -> {
                        inMovetext = false
                        if (!listener(game)) return false
                        game = Game()
                    }
                    token.isCommentStart -> inComment = true
                    token.isVariantStart -> game.startVariation()
                    token.isVariantEnd -> game.endVariation()
                    else -> game.addMove(token)
                }
            } else {
                if (token.isCommentEnd) {
                    inComment = false
                    game.currentPosition.comment = comment.toString().trim()
                    comment = StringBuilder()
                } else {
                    comment.append(token)
                }
            }
        }
        if (inComment) comment.append(" ")
        return true
    }

}

// split at "(", ")", ".", " ", "{" and "}"
private val delimiters = "(). {}"

// split string including delimiters
private val regex = Regex("(?<=[$delimiters])|(?=[$delimiters])")

// legal PGN result Strings
private val RESULTS = setOf("*", "1-0", "0-1", "1/2-1/2")

private val String.isGameResult: Boolean get() { return this in RESULTS }
private val String.isCommentStart: Boolean get() { return startsWith('{') }
private val String.isCommentEnd: Boolean get() { return endsWith('}') }
private val String.isVariantStart: Boolean get() { return startsWith('(') }
private val String.isVariantEnd: Boolean get() { return endsWith(')') }
private val String.isMoveNumber: Boolean get() { return contains('.') || isNumber }
private val String.isNAG: Boolean get() { return startsWith('$') }
private val String.isNumber: Boolean get() { return all { it.isDigit() } }
