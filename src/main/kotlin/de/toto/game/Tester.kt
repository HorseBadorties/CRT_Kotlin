package de.toto.game

class Foo(val first: Int, val second: Int) {

    init {
        if (first !in 1..10) {
            throw IllegalArgumentException("first must be between 1 and 10")
        }
        if (second !in 22..33) {
            throw IllegalArgumentException("second must be between 22 and 33")
        }
    }

    // stringValue contains e.g. "1 22"
    constructor(stringValue: String) : this(
            parseFirst(stringValue), parseSecond(stringValue)
    )



}

private fun parseFirst(stringValue: String): Int {
    //would do some parsing and validation here
    // ..
    return 1
}

private fun parseSecond(stringValue: String): Int {
    //would do some parsing and validation here
    // ..
    return 22
}

class Foo2 {

    var value: Int = 0
        private set(newValue) {
            if (this.value < 1 || this.value > 10) {
                throw IllegalArgumentException("first must be between 1 and 10")
            }
            field = newValue
        }

    constructor(_value: Int) {
        value = _value
    }

    constructor(stringValue: String) {
        val _value = Integer.parseInt(stringValue)
        if (_value == 42) {
            throw IllegalArgumentException("first 42 not allowed when initialized with a String first")
        }
        value = _value
    }

}


fun main(args: Array<String>) {
    println(Foo(1, 24))
}