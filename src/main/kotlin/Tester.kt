val s: String by lazy { println("delazified!"); "hi" }

fun main(args: Array<String>) {
    s
//    println(s)
}
