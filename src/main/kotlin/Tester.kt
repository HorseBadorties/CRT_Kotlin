import java.util.*

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    (1..scanner.nextInt()).forEach {
        println(scanner.next().run {
            if (this.all { it == this[0] }) "YES" else "NO"
        })
    }
}