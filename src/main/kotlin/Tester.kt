
data class Foo(val name: String)

fun main(args: Array<String>) {
    println(listOf("1" , "19", "1" ,"12" ,"9" ,"3", "5")
            .filter { it != "1" }
            .map { it.toInt() }
            .sorted()
            .map { it.toString() + "s"})

}
