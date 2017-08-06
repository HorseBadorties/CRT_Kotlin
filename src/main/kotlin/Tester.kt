data class Foo(val name: String, val age: Int, val optional: String? = null) {
    override fun equals(other: Any?): Boolean {
        println("Foo.equals called")
        return super.equals(other)
    }
}

fun main(args: Array<String>) {

    println(Foo::class.members)
    println(Foo("foo1", 10) == Foo("foo1", 10, "extra"))

    val foo = Foo("foo1", 10).copy(optional = "new")
    val x: String? = null

    x?.let {
        println("in let")
    }

    val x1 = "z".."a"
    println(x1)

    if ("d" in x1) println("in")
//    for (x in x1) println(x)

    val x2 = 3 downTo 1
    println(x2)
    for (x in x2) println(x)

    emptyList<Int>().minKt()

    IntProgression
    println(listOf(1, 19, 1, 12, 9, 3, 5)
            .filter { it != 1 }
            .map { it.toInt() }
            .sorted())

}

fun Iterable<Int>.min2(): Int? {
    val iterator = iterator()
    if (!iterator.hasNext()) return null
    var min = iterator.next()
    while (iterator.hasNext()) {
        val e = iterator.next()
        if (min > e) min = e
    }
    return min
}

fun Iterable<Int>.minKt(): Int? {
    with(iterator()) {
        if (!hasNext()) return null
        var min = next()
        forEach { if (min > it) min = it }
        return min
    }
}
