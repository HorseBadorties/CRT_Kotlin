
fun varargsTest(vararg s: String) = null

fun main(args: Array<String>) {
    val list = listOf("1", "2")

    varargsTest(*list.toTypedArray())

}



