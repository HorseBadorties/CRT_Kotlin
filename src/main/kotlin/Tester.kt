
fun main(args: Array<String>) {
    val foo = "foo"
//    println("${foo[1]}, ${foo.elementAt(1)}, ${foo.codePointAt(1)}")
    println(foo.onEach { println(it) }.onEach { println(it) })
    val l = listOf("f", "o", "o")
    println(l.onEach { println(it) }.onEach { println(it) })
    println("c".repeat(5))
}
