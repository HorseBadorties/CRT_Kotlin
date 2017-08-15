package de.toto.crt.game

import java.nio.file.*

class Tag(name: String, value: String)

fun fromPGN(path: Path) {
    var linenumber = 0
    var startOfFile = true
    val moves = StringBuilder()
    var expectedResult: String
    for (line in Files.lines(path, Charsets.UTF_8)) {
        linenumber++
        var s = line.trim()
        if (s.isEmpty()) continue
        if (startOfFile) {
            // Drop first 3 ChessBase "special characters"
            s = s.dropWhile { it != '[' }
            startOfFile = false
        }
        if (s.startsWith('[') && moves.isEmpty()) {
            with (s.dropLast(1).drop(1)) {
                val tag = Tag(substringBefore(' '), substringAfter(' ').replace('"', ' '))
            }

        } else {

        }
        println(s)
    }
}

fun main(args: Array<String>) {
    fromPGN(Paths.get("C:\\Users\\080064\\Downloads\\Repertoire_Black.pgn"))
}
