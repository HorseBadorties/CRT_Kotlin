import java.lang.IllegalArgumentException

fun main(args: Array<String>) {
    val l = listOf(1, 2, 3, 4, 5, 6, 7)
            .toMap()
    println(l)

    val m = mapOf(1 to 2, 3 to 4, 5 to 6)
    println(m)
}

public inline fun <T> Collection<T>.toMap(): Map<T, T> {
    if (size % 2 != 0) throw IllegalArgumentException("Collection has an uneven number of elements.")
    val map = mutableMapOf<T, T>()
    indices.filter { it % 2 == 0 }
           .forEach { map.put(elementAt(it), elementAt(it +1)) }
    return map
}

