
fun main(args: Array<String>) {

    emptyList<Int>().minKt()


    println(listOf(1, 19, 1, 12, 9, 3, 5)
            .filter { it != 1 }
//            .map { it.toInt() }
//            .sorted()
            .min())


IntRange.EMPTY.forEach { println("$it") }

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
