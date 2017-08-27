package de.toto.crt.game.gui.swing

import com.kitfox.svg.SVGUniverse
import com.kitfox.svg.app.beans.SVGIcon
import de.toto.crt.game.Position
import java.awt.*
import javax.swing.JComponent

class SquareData(var topLeft: Pair<Int, Int>, val isWhite: Boolean)

class ChessBoard: JComponent() {

    private var position: Position = Position()
    private var boardImageScaled: Image? = null
    private val pieceIcons = loadPieces()
    private val squareData = mutableMapOf<Pair<Int, Int>, SquareData>()

    init {
        for (rank in 0..7) {
            for (file in 0..7) {
                val square = position.squares[rank][file]
                squareData.put(Pair(rank, file), SquareData(Pair(0, 0), square.isWhite))
            }
        }
    }

    fun setPosition(newPosition: Position) {
        if (newPosition != position) {
            position = newPosition
            repaint()
        }
    }

    private fun squareSize() = preferredSize.width / 8

    override fun paintComponent(g: Graphics?) {
        val squareSize = squareSize()
        val comp = this
        with (g as Graphics2D) {

            if (boardImageScaled != null) {
                drawImage(boardImageScaled, 0, 0, null)
            }
            for (rank in 0..7) {
                for (file in 0..7) {
                    val squareData = squareData[Pair(rank, file)]
                    val (x, y) = squareData!!.topLeft
                    if (boardImageScaled == null) {
                        // draw squares
                        color = if (squareData.isWhite) Color.LIGHT_GRAY else Color.GRAY
                        fillRect(x, y, squareSize, squareSize)
                    }
                    // draw pieces
                    position.squares[rank][file].piece?.let {
                        val icon = pieceIcons[it.fenChar.toString()]
                        if (icon != null) {
                            icon.preferredSize = Dimension(squareSize, squareSize)
                            icon.paintIcon(comp, this, x, y)
                        }
                    }
                }
            }
        }
    }

    fun rescale() {
        val squareSize = squareSize()
        for (rank in 0..7) {
            for (file in 0..7) {
                val x = file * squareSize
                val y = preferredSize.width - (rank+1) * squareSize
                squareData[Pair(rank, file)]?.topLeft = Pair(x, y)
            }
        }

    }

    private fun loadPieces(): Map<String, SVGIcon> {

        fun toFen(str: String) = if (str.first() == 'w') str.drop(1) else str.drop(1).toLowerCase()

        return listOf("wK", "wQ", "wR", "wB", "wN", "wP", "bK", "bQ", "bR", "bB", "bN", "bP").associate {
            val svgUniverse = SVGUniverse()
            val svgIcon = SVGIcon()
            svgIcon.svgURI = svgUniverse.loadSVG(javaClass.getResource("/images/pieces/merida/$it.svg"))
            svgIcon.isScaleToFit = true
            svgIcon.antiAlias = true
            Pair(toFen(it), svgIcon)
        }
    }
}