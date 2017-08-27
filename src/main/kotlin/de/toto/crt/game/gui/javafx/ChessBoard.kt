package de.toto.crt.game.gui.javafx

import com.kitfox.svg.SVGUniverse
import com.kitfox.svg.app.beans.SVGIcon
import de.toto.crt.game.Position
import javafx.embed.swing.SwingFXUtils
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import java.awt.Dimension
import java.awt.image.BufferedImage



class SquareData(var topLeft: Pair<Double, Double>, val isWhite: Boolean)

class ChessBoard : Pane() {

    private var position: Position = Position()
    private val canvas = Canvas()
    private var boardImageScaled: Image? = null
    private val pieceIcons = loadPieces()
    private val scaledPieces = mutableMapOf<String, Image>()
    private val squareData = mutableMapOf<Pair<Int, Int>, SquareData>()

    init {
        children.add(canvas)
        for (rank in 0..7) {
            for (file in 0..7) {
                val square = position.squares[rank][file]
                squareData.put(Pair(rank, file), SquareData(Pair(0.0, 0.0), square.isWhite))
            }
        }
    }

    fun setPosition(newPosition: Position) {
        if (newPosition != position) {
            position = newPosition
            draw()
        }
    }

    override fun layoutChildren() {
        val size = Math.min(width, height)
        if (size > 0 && size != canvas.width) {
            canvas.width = size
            canvas.height = width
            scalePieces()
        }
        val squareSize = squareSize()
        for (rank in 0..7) {
            for (file in 0..7) {
                val x = file * squareSize
                val y = canvas.width - (rank+1) * squareSize
                squareData[Pair(rank, file)]?.topLeft = Pair(x, y)
            }
        }
        draw()
    }

    private fun draw() {
        val squareSize = squareSize()
        with (canvas.graphicsContext2D) {

            if (boardImageScaled != null) {
                drawImage(boardImageScaled, 0.0, 0.0)
            }
            for (rank in 0..7) {
                for (file in 0..7) {
                    val squareData = squareData[Pair(rank, file)]
                    val (x, y) = squareData!!.topLeft
                    if (boardImageScaled == null) {
                        // draw squares
                        fill = if (squareData.isWhite) Color.LIGHTGREY else Color.GRAY
                        fillRect(x, y, squareSize, squareSize)
                    }
                    // draw pieces
                    position.squares[rank][file].piece?.let {
//                        val image = scaledPieces[it.fenChar.toString()]
//                        if (image != null) {
//                            render(this, image,
//                                    0, 0, squareSize.toInt(), squareSize.toInt(), x.toInt(), y.toInt())
//                        }
                        drawImage(scaledPieces[it.fenChar.toString()], x, y)
                    }
                }
            }
        }
    }

    private fun squareSize() = canvas.width / 8

    private fun loadPieces(): Map<String, SVGIcon> {

        fun toFen(str: String) = if (str.first() == 'w') str.drop(1) else str.drop(1).toLowerCase()

        return listOf("wK", "wQ", "wR", "wB", "wN", "wP", "bK", "bQ", "bR", "bB", "bN", "bP").associate {
            val svgUniverse = SVGUniverse()
            val svgIcon = SVGIcon()
            svgIcon.svgURI = svgUniverse.loadSVG(javaClass.getResource("/images/pieces/merida/$it.svg"))
            Pair(toFen(it), svgIcon)
        }
    }

    private fun scalePieces() {
//        boardImageScaled = Image("/images/board/maple.jpg", canvas.width, canvas.width, true, true)
        scaledPieces.clear()
        val size = squareSize().toInt()
        for ((name, icon) in pieceIcons) {
            val bufferedAWTImage = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB_PRE)
            val awtGraphics = bufferedAWTImage.createGraphics()
            icon.isScaleToFit = true
            icon.antiAlias = true
            icon.preferredSize = Dimension(size, size)
            icon.paintIcon(null, awtGraphics, 0, 0)
            awtGraphics.dispose()
            scaledPieces.put(name, SwingFXUtils.toFXImage(bufferedAWTImage, null))
        }
    }

    fun render(context: GraphicsContext, image: Image, sx: Int, sy: Int, sw: Int, sh: Int, tx: Int, ty: Int) {
        val reader = image.pixelReader
        val writer = context.pixelWriter
        for (x in 0..sw - 1) {
            for (y in 0..sh - 1) {
                val color = reader.getColor(sx + x, sy + y)
                if (color.isOpaque) {
                    writer.setColor(tx + x, ty + y, color)
                }
            }
        }
    }

}