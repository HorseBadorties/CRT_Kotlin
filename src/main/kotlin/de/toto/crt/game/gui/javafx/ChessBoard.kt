package de.toto.crt.game.gui.javafx

import com.kitfox.svg.SVGUniverse
import com.kitfox.svg.app.beans.SVGIcon
import de.toto.crt.game.Position
import javafx.embed.swing.SwingFXUtils
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import java.awt.Dimension
import java.awt.RenderingHints
import java.awt.image.BufferedImage

class ChessBoard : Pane() {

    private var position: Position = Position()
    private val canvas = Canvas()
    private var pieceIcons = loadPieces()
    private val scaledPieces = mutableMapOf<String, Image>()

    init {
        children.add(canvas)
    }

    fun setPosition(newPosition: Position) {
        if (newPosition != position) {
            position = newPosition
            draw()
        }
    }

    override fun layoutChildren() {
        with (Math.min(width, height)) {
            if (this > 0 && this != canvas.width) {
                canvas.width = this
                canvas.height = width
                scalePieces()
                draw()
            }
        }
    }

    private fun draw() {
        val squareSize = squareSize()
        val canvasSize = canvas.width
        with (canvas.graphicsContext2D) {
            fill = Color.LIGHTGOLDENRODYELLOW
            fillRect(0.0, 0.0, width, height)
            for (rank in 0..7) {
                for (file in 0..7) {
                    val x = file * squareSize
                    val y = canvasSize - (rank+1) * squareSize
                    position.squares[rank][file].piece?.let {
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
        scaledPieces.clear()
        val size = squareSize().toInt()
        for ((name, icon) in pieceIcons) {
            val bufferedAWTImage = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
            val awtGraphics = bufferedAWTImage.createGraphics()
            awtGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            awtGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            icon.isScaleToFit = true
            icon.antiAlias = true
            icon.preferredSize = Dimension(size, size)
            icon.paintIcon(null, awtGraphics, 0, 0)
            awtGraphics.dispose()
            scaledPieces.put(name, SwingFXUtils.toFXImage(bufferedAWTImage, null))
        }
    }

}