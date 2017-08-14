import de.toto.crt.game.FEN_STARTPOSITION
import de.toto.crt.game.Position
import de.toto.crt.game.fromFEN

fun main(args: Array<String>) {

    Position.fromFEN(FEN_STARTPOSITION).squares().forEach { println("${it.name}: ${it.toString()}") }

}



