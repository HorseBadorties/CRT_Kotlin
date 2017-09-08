fun main(args: Array<String>) {
    val boolean = false
    var result = boolean t 1.0 ?: "0"
    println(result::class.java)
}

infix fun <T : Any> Boolean.t(value: T): T? = if(this) value else null