package de.toto.crt.game.gui.javafx

import com.kitfox.svg.SVGUniverse
import com.kitfox.svg.app.beans.SVGIcon
import de.toto.crt.game.Position
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventType
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import java.awt.Dimension
import java.awt.image.BufferedImage



class SquareData(
    var rank: Int,
    var file: Int,
    var top: Double,
    var left: Double,
    val isWhite: Boolean,
    var image: Image?)

class ChessBoard : Pane() {

    private var position: Position = Position()
    private val canvas = Canvas()
    private val pieceIcons = loadPieces()
    private val scaledPieces = mutableMapOf<String, Image>()
    private val squareData = mutableMapOf<Pair<Int, Int>, SquareData>()
    private var dragging = false
    private var dragSquare: SquareData? = null
    private var dropSquare: SquareData? = null

    init {
        canvas.setOnDragDetected { e ->
            println("DragDetected on ${rankAndFileAt(e.sceneX, e.sceneY)}")
            val dragboard = canvas.startDragAndDrop(*TransferMode.ANY)
            val content = ClipboardContent()
            content.putString("foo")
            dragboard.setContent(content)
            dragging = true
            val rankAndFile = rankAndFileAt(e.sceneX, e.sceneY)
            dragSquare = squareData[rankAndFile]
            drawSquare(dragSquare!!.rank, dragSquare!!.file)
            e.consume()
        }
        canvas.setOnDragOver { e ->
            e.acceptTransferModes(*TransferMode.ANY);
            e.consume();
            val rankAndFile = rankAndFileAt(e.sceneX, e.sceneY)
            val formerDropSquare = dropSquare
            dropSquare = squareData[rankAndFile]
            if (formerDropSquare != dropSquare) {
                if (formerDropSquare != null) {
                    drawSquare(formerDropSquare.rank, formerDropSquare.file)
                }
                drawSquare(dropSquare!!.rank, dropSquare!!.file)
            }
        }
        canvas.setOnDragDropped { e ->
            println("Dropped on ${rankAndFileAt(e.sceneX, e.sceneY)}")
            e.isDropCompleted = true
            e.consume()
        }
        canvas.setOnDragDone { e ->
            dragging = false
            dragSquare = null
            dropSquare = null
            draw()
            e.consume()
        }

        children.add(canvas)
        for (rank in 0..7) {
            for (file in 0..7) {
                val square = position.squares[rank][file]
                squareData.put(Pair(rank, file),
                        SquareData(rank, file, 0.0, 0.0, square.isWhite, null))
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
        // our size has to be always divisible by 8
        var s = Math.min(width, height).toInt()
        while (s % 8 != 0) s--
        val size = s.toDouble()
        if (size > 0 && size != canvas.width) {
            canvas.width = size
            canvas.height = width
            scaleAll()
            for (rank in 0..7) {
                for (file in 0..7) {
                    val x = file * squareSize
                    val y = canvas.width - (rank+1) * squareSize
                    with (squareData[Pair(rank, file)]!!) {
                        top = x
                        left = y
                    }
                }
            }
        }
        draw()
    }

    /**
     * 1. square background
     * 2. square coordinates
     * 3. colored squares of last move
     * 4. colored squares of highlights
     * 5. pieces
     * 6. D&D squares highlight
     * 7. D&D piece
     * 8. arrows
     */
    private fun draw() {
        for (rank in 0..7) {
            for (file in 0..7) {
                drawSquare(rank, file)
            }
        }
    }

    private fun drawSquare(rank: Int, file: Int) {
        with (canvas.graphicsContext2D) {
            val squareData = squareData[Pair(rank, file)]
            val x = squareData!!.top
            val y = squareData.left
            // draw squares
            if (squareData.image != null) {
                drawImage(squareData.image, x, y)
            } else {
                colorSquare(x, y, if (squareData.isWhite) Color.LIGHTGREY else Color.GRAY)
            }
            // drag square
            if (squareData === dragSquare || squareData === dropSquare) {
                colorSquare(x, y, Color.YELLOW, 0.4)
            }
            // draw pieces
            if (squareData !== dragSquare) {
                position.squares[rank][file].piece?.let {
                    drawImage(scaledPieces[it.fenChar.toString()], x, y)
                }
            }
        }
    }

    private fun colorSquare(x: Double, y: Double, color: Color, alpha: Double = 1.0) {
        with (canvas.graphicsContext2D) {
            globalAlpha = alpha
            fill = color
            fillRect(x, y, squareSize, squareSize)
            globalAlpha = 1.0
        }
    }

    private fun rankAndFileAt(x: Double, y: Double): Pair<Int, Int> {
        val rank = 8 - y / squareSize
        val file = x / squareSize
        return Pair(rank.toInt(), file.toInt())
    }

    private var squareSize = canvas.width / 8

    private fun loadPieces(): Map<String, SVGIcon> {

        // "wK" -> "K"; "bK" -> "k"
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

    private fun scaleAll() {
        squareSize = canvas.width / 8
        val squareSizeInt = squareSize.toInt()
        try {
            val imgFile = "maple.jpg"
            val boardImageScaled = Image("/images/board/$imgFile", canvas.width, canvas.width, true, true)
            for (rank in 0..7) {
                for (file in 0..7) {
                    val x = file * squareSizeInt
                    val y = canvas.width.toInt() - (rank + 1) * squareSizeInt
                    squareData[Pair(rank, file)]?.image =
                            WritableImage(boardImageScaled.pixelReader, x, y, squareSizeInt, squareSizeInt)
                }
            }
        } catch (ex:  Exception) {
            ex.printStackTrace()
        }

        scaledPieces.clear()
        for ((name, icon) in pieceIcons) {
            val bufferedAWTImage = BufferedImage(squareSizeInt, squareSizeInt, BufferedImage.TYPE_INT_ARGB)
            val awtGraphics = bufferedAWTImage.createGraphics()
            icon.preferredSize = Dimension(squareSizeInt, squareSizeInt)
            icon.paintIcon(null, awtGraphics, 0, 0)
            awtGraphics.dispose()
            scaledPieces.put(name, SwingFXUtils.toFXImage(bufferedAWTImage, null))
        }
    }

}