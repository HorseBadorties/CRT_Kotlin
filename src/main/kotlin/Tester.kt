class Foo {
    val withBackingField = "Foo withBackingField"
    val noBackingField: String  get() = "Foo noBackingField"
    fun noBackingField() = noBackingField
    fun randomName() = noBackingField
}

fun main(args: Array<String>) {
    val f = Foo()
    println("breakpoint")
}


