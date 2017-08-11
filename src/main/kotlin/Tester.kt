

fun foo(vararg rights: String) {
}

fun main(args: Array<String>) {
    foo(*listOf("1", "2").toTypedArray())

}



