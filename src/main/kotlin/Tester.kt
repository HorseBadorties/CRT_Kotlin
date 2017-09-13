import java.util.*

fun main(args: Array<String>) {
    println( List(3) { it } )
//    val q = queueOf(*emptyArray<Node>())
}

fun breadthFirst(root: Node): List<Node> {
    val result = mutableListOf<Node>()
    queueOf(root).pollEach { queue, node ->
        if (node !== root) result.add(node)
        node.children.filterTo(queue) { child ->  child !in result }
    }
    return result
}

class Node(var children: List<Node>)

inline fun queueOf(vararg nodes: Node) = LinkedList(nodes.asList())
//fun queueOf(vararg node: Node) = LinkedList<Node>().apply { addAll(node) }

inline fun Queue<Node>.pollEach(action: (Queue<Node>, Node) -> Unit) {
    while (!isEmpty()) action(this, poll())
}

