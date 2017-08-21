import de.toto.crt.game.Position

inline fun forEach(vararg elements: Any, action: (Any) -> Unit) {
    elements.forEach(action)
}

data class Foo(val name: String)

fun main(args: Array<String>) {
    val pos = Position()
    with (pos) {
//        sq
    }
    forEach("333", true, 1, Foo("foo")) { println(it) }
}





