fun main(args: Array<String>) {
    val l = listOf("1", "2", "3")
    println(l.associate { Pair("it=$it", it.toInt()) })
}