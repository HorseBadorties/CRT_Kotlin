import de.toto.crt.game.Piece

fun main(args: Array<String>) {
    Piece.values().forEach { println("${it.name}: ${it.figurine}") }


}



