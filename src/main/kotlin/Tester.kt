fun main(args: Array<String>) {
    val l = mutableListOf(2 ,2 ,3 ,4, 5, 6)
    val x = l.firstOrNull { it == 3 }
    println("$x $l")
}