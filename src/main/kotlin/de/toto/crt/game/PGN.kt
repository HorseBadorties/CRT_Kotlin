package de.toto.crt.game

import java.nio.file.*

fun fromPGN(path: Path) {
    var lineNumber = 0
    var startOfFile = true
    var tags = mutableListOf<Pair<String, String>>()
    val moves = StringBuilder()
    var expectedResult: String = ""
    for (line in Files.lines(path, Charsets.UTF_8)) {
        lineNumber++
        var s = line.trim()
        if (s.isEmpty()) continue
        if (startOfFile) {
            // Drop first 3 ChessBase "special characters"
            s = s.dropWhile { it != '[' }
            startOfFile = false
        }
        if (s.startsWith('[') && moves.isEmpty()) {
            with (s.dropLast(1).drop(1)) {
                val t = Pair(substringBefore(' '), substringAfter(' ').replace("\"", ""))
                if ("Result" == t.first) expectedResult = t.second
                tags.add(t)
            }
        } else {
            if (moves.isEmpty()) {
                require(!expectedResult.isEmpty()) {
                    "Result tag missing"
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    fromPGN(Paths.get("C:\\Users\\080064\\Downloads\\Repertoire_Black.pgn"))
}
