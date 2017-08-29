package de.toto.crt.game.gui.swing

import de.toto.crt.game.Position
import java.util.*
import javax.swing.tree.TreeNode

class PositionTreeNode(val position: Position): TreeNode {

    override fun getIndex(node: TreeNode?): Int {
        node as PositionTreeNode
        return position.next.indexOf(node.position)
    }

    override fun isLeaf() = !position.hasNext

    override fun getChildCount() = position.next.size

    override fun getParent() = PositionTreeNode(position.previous!!)

    override fun getChildAt(childIndex: Int) = PositionTreeNode(position.next[childIndex]
    )

    override fun getAllowsChildren() = false

    override fun children(): Enumeration<*> {
        return Vector(position.next.map { PositionTreeNode(it) }).elements()

    }

    override fun toString() = position.toString()


}

fun Position.asTreeNode() = PositionTreeNode(this)