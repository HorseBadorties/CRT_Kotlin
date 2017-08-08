
fun main(args: Array<String>) {
    val x : String? = null
    when (x) {
        "foo" -> println("foo")
        null -> println("x is null")
        else -> println("whatever")
    }
}

