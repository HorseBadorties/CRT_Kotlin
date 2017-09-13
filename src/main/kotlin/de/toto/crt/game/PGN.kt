package de.toto.crt.game

import kotlinx.coroutines.experimental.*
import java.nio.file.*

import de.toto.crt.game.rules.NAG

fun fromPGN(pgn: String): Game {
    val game = StringBuilder()
    pgn.lines().forEach() {
        if (!it.isBlank()) {
            game.append(it.trim()).append(System.lineSeparator())
        }
    }
    return PGNParser(lineNumber = 1).parse(game.toString(), { true }) ?: throw IllegalArgumentException("failed to parse $pgn")
}

fun fromPGN(path: Path, predicate: (Game) -> Boolean = { true }): List<Game> {
    val result = mutableListOf<Game>()
    var startOfFile = true
    var lineNumber = 0
    var lineNumberGameStart = 0
    val game = StringBuilder()
    var inGametext = false
    val deferredGames = mutableListOf<Deferred<Game?>>()
    for (line in Files.lines(path, Charsets.UTF_8)) {
        lineNumber++
        if (line.isBlank()) continue
        if (startOfFile) {
            if (line.contains('[')) {
                // Drop first 3 ChessBase "special characters"
                game.append(line.dropWhile { it != '[' }.trim()).append(System.lineSeparator())
            }
            startOfFile = false
            lineNumberGameStart = lineNumber
        } else {
            if (line.trim().isTag) {
                if (inGametext) {
                    val newGametext = game.toString()
                    val newLineNumber = lineNumberGameStart
                    deferredGames.add(async(CommonPool) {
                        try {
                            PGNParser(newLineNumber).parse(newGametext, predicate)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            null
                        }
                    })
                    game.setLength(0)
                    inGametext = false
                    lineNumberGameStart = lineNumber
                }
            } else {
                inGametext = true
            }
            game.append(line.trim()).append(System.lineSeparator())
        }
    }
    runBlocking {
        deferredGames.forEach { it.await()?.let { result.add(it) } }
    }
    return result
}


private class PGNParser(lineNumber: Int) {

    val game = Game()
    var lineNumber = lineNumber
    var inMovetext = false
    var inComment = false
    var gameResultFound = false
    var comment = StringBuilder()

    lateinit var predicate: (Game) -> Boolean

    fun parse(string: String, predicate: (Game) -> Boolean = { true }): Game? {
        this.predicate = predicate
        string.lines().forEach { if (!gameResultFound) parseLine(it) }
        return if (predicate(game)) game else null
    }

    private fun parseLine(line: String) {
        lineNumber++
        try {
            if (inMovetext) {
                parseMovetext(line)
            } else {
                if (line.isTag) {
                    with (line.dropLast(1).drop(1)) {
                        game.tags.put(substringBefore(' '),
                                substringAfter(' ').replace("\"", ""))
                    }
                } else {
                    inMovetext = true
                    if ("FEN" in game.tags) {
                        game.startWithFen(game.tags["FEN"] ?: "")
                    }
                    parseMovetext(line)
                }
            }
        } catch (e: Exception) {
            throw Exception("error on line $lineNumber", e)
        }
    }

    private fun parseMovetext(line: String) {
        for (token in line.split(regex)) {
            if (!inComment) {
                when {
                    token.isBlank() -> {}
                    token.isMoveNumber -> {}
                    token.isGameResult -> gameResultFound = true
                    token.isNAG -> game.currentPosition.nags.add(NAG.getNag(token))
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
private val String.isTag: Boolean get() = startsWith("[") && endsWith("]")