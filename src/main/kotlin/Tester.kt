
data class Foo(val name: String)

fun main(args: Array<String>) {
    var iAmAVar: Foo? = null
//    mightBeNull = Foo("foo")

    println( iAmAVar?.let{ it.name+"XXX" } ?: "XXX" )
}
