import de.toto.crt.game.fromPGN
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    var count = 0
    val duration = measureTimeMillis {
        val games = fromPGN(Paths.get("C:\\Users\\080064\\Downloads\\twic_BULK.pgn")) {
            count++; it.tags["White"]?.startsWith("Duda,") ?: false
        }
        println("${games.size} Duda games found")
    }
    println("$count games parsed in ${duration/1000} seconds")

}
