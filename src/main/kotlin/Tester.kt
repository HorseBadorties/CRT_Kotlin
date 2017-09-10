
fun main(args: Array<String>) {
    val (x, y) = Foo1(23, 45)
    val (x2, y2) = Foo2(23, 45)
}

data class Foo1(val x: Int, val y: Int)
class Foo2(val x: Int, val y: Int) {
    operator fun component1() = x
    operator fun component2() = y
}