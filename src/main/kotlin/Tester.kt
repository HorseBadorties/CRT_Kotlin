
open class Foo
class SpecialFoo : Foo() {
    fun bar() {}
}

fun main(args: Array<String>) {
    val l = listOf(Foo(), SpecialFoo(), Foo(), SpecialFoo())
    l.filterIsInstance<SpecialFoo>().forEach {
        it.bar()
    }
}