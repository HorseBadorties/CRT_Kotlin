package de.toto.crt.game

import java.nio.file.*

fun fromPGN(path: Path) = PGNParser().parse(path)

fun fromPGN(pgn: String) = PGNParser().parse(pgn)

private class PGNParser {

    var lineNumber = 0
    var startOfFile = true
    var inMovetext = false
    var inComment = false
    var comment = StringBuilder()
    var currentGame = Game()

    val games = mutableListOf<Game>()

    fun parse(path: Path): List<Game> {
        for (line in Files.lines(path, Charsets.UTF_8)) {
            parseLine(line)
        }
        return games
    }

    fun parse(string: String): List<Game> {
        for (line in string.lines()) {
            parseLine(line)
        }
        return games
    }

    private fun parseLine(line: String) {
        lineNumber++
        try {
            var s = line.trim()
            if (s.isEmpty()) return
            if (startOfFile) {
                if (s.contains('[')) {
                    // Drop first 3 ChessBase "special characters"
                    s = s.dropWhile { it != '[' }
                }
                startOfFile = false
            }
            if (inMovetext) {
                parseMovetext(s)
            } else {
                if (s.startsWith('[') && s.endsWith(']')) {
                    with (s.dropLast(1).drop(1)) {
                        currentGame.tags.put(substringBefore(' '),
                                substringAfter(' ').replace("\"", ""))
                    }
                } else {
                    inMovetext = true
                    parseMovetext(s)
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
                    token.isBlank() -> { /* do nothing */ }
                    token.isMoveNumber -> { /* do nothing */ }
                    token.isNAG -> currentGame.currentPosition.nags.add(NAG.getNag(token))
                    token.isGameResult -> {
                        inMovetext = false
                        games.add(currentGame)
                        currentGame = Game()
                    }
                    token.isCommentStart -> inComment = true
                    token.isVariantStart -> currentGame.startVariation()
                    token.isVariantEnd -> currentGame.endVariation()
                    else -> currentGame.addMove(token)
                }
            } else {
                if (token.isCommentEnd) {
                    inComment = false
                    currentGame.currentPosition.comment = comment.toString()
                    comment = StringBuilder()
                } else {
                    comment.append(token)
                }
            }
        }
    }

}

// split at "(", ")", ".", " ", "{", "}",
private val delimiters = "(). {}"

// split string including delimiters
private val regex = Regex("(?<=[$delimiters])|(?=[$delimiters])")

private val RESULTS = setOf("*", "1-0", "0-1", "1/2-1/2")

private val String.isGameResult: Boolean get() { return this in RESULTS }
private val String.isCommentStart: Boolean get() { return startsWith('{') }
private val String.isCommentEnd: Boolean get() { return endsWith('}') }
private val String.isVariantStart: Boolean get() { return startsWith('(') }
private val String.isVariantEnd: Boolean get() { return endsWith(')') }
private val String.isMoveNumber: Boolean get() { return contains('.') || isNumber }
private val String.isNAG: Boolean get() { return startsWith('$') }
private val String.isNumber: Boolean get() { return all { it.isDigit() } }
