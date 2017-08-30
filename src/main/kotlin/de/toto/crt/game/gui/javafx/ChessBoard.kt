package de.toto.crt.game.gui.javafx

import com.kitfox.svg.SVGUniverse
import com.kitfox.svg.app.beans.SVGIcon
import de.toto.crt.game.Position
import de.toto.crt.game.rules.Square
import de.toto.crt.game.rules.legalMovesFrom
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import java.awt.Dimension
import java.awt.image.BufferedImage

private class SquareData(
    val square: Square,
    var topLeft: Point2D,
    var scaledImage: Image?)

class ChessBoard : Region() {

    var isOrientationWhite = true
        set(value) {
            field = value
            calcSquarePoints()
            draw()
        }
    fun flip() { isOrientationWhite = !isOrientationWhite }

    private val canvas = Canvas()
    private var squareSize: Int = 0
    private var position = Position()
    private val boardImageURL: String? = null // "/images/board/maple.jpg"
    private val pieceIcons: Map<Char, SVGIcon> = loadPieces()
    private val scaledPieces = mutableMapOf<Char, Image>()
    private val squares: List<SquareData> = squareData()
    private var dragSquare: SquareData? = null
    private var dragImage: Image? = null
    private var dropSquare: SquareData? = null
    private val dragAffectedSquares = mutableSetOf<Square>()

    private val listener = mutableListOf<ChessBoardListener>()
    fun addListener(l: ChessBoardListener) = listener.add(l)
    fun removeListener(l: ChessBoardListener) = listener.remove(l)

    init {
        children.add(canvas)

        // Drag&Drop handling
        canvas.setOnDragDetected { e ->
            val s = squareAt(e.x, e.y)
            if (s.rank in 1..8 && s.file in 1..8) {
                val piece = position.square(s.rank, s.file).piece
                if (piece != null) {
                    // we need to put something on the dragboard to effectivly start dragging
                    val dragboard = canvas.startDragAndDrop(*TransferMode.ANY)
                    val content = ClipboardContent()
                    content.putString("")
                    dragboard.setContent(content)
                    e.consume()
                    dragSquare = squareDataOf(s)
                    dragImage = scaledPieces[piece.fenChar]
                    draw() // get rid of possible arrows
                }
            }
            e.consume()
        }
        canvas.setOnDragOver { e ->
            val s = squareAt(e.x, e.y)
            if (s.rank in 1..8 && s.file in 1..8) {
                e.acceptTransferModes(*TransferMode.ANY)
                dropSquare = squareDataOf(s)
                drawDragPiece(Point2D(e.x, e.y))
            }
            e.consume()
        }
        canvas.setOnDragDropped { e ->
            e.isDropCompleted = true
            e.consume()
        }
        canvas.setOnDragDone { e ->
            e.consume()
            drawDragPiece(null)
        }
        canvas.setOnMouseClicked { e ->
            listener.forEach { it.squareClicked(squareAt(e.x, e.y)) }
        }

    }

    private fun squareData(): List<SquareData> {
        val result = mutableListOf<SquareData>()
        for (rank in 1..8) {
            for (file in 1..8) {
                result.add(SquareData(Square(rank, file), Point2D(0.0, 0.0), null))
            }
        }
        return result.toList()
    }

    fun setPosition(newPosition: Position) {
        if (newPosition != position) {
            position = newPosition
            draw()
        }
    }

    override fun layoutChildren() {
        scaleAll()
        draw()
    }

    private fun scaleAll() {
        // canvas size has to be always divisible by 8
        var s = Math.min(width, height).toInt()
        while (s % 8 != 0) s--
        val size = s.toDouble()
        canvas.width = size
        canvas.height = width
        // calc new squareSize
        squareSize = (canvas.width / 8).toInt()
        // scale square images
        if (boardImageURL != null) {
            try {
                val boardImageScaled = Image(boardImageURL, canvas.width, canvas.width, true, true)
                for (rank in 1..8) {
                    for (file in 1..8) {
                        squareDataOf(rank, file).scaledImage =
                                WritableImage(boardImageScaled.pixelReader, 0, 0, squareSize, squareSize)
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        // scale piece images
        scaledPieces.clear()
        for ((name, icon) in pieceIcons) {
            val bufferedAWTImage = BufferedImage(squareSize, squareSize, BufferedImage.TYPE_INT_ARGB)
            val awtGraphics = bufferedAWTImage.createGraphics()
            icon.preferredSize = Dimension(squareSize, squareSize)
            icon.paintIcon(null, awtGraphics, 0, 0)
            awtGraphics.dispose()
            scaledPieces.put(name, SwingFXUtils.toFXImage(bufferedAWTImage, null))
        }
        // calc square topLeft points
        calcSquarePoints()
    }

    private fun calcSquarePoints() {
        for (rank in 1..8) {
            for (file in 1..8) {
                val x = if (isOrientationWhite) ((file - 1) * squareSize).toDouble()
                else canvas.width - file  * squareSize
                val y = if (isOrientationWhite) canvas.width - rank * squareSize
                else ((rank - 1) * squareSize).toDouble()
                squareDataOf(rank, file).let {
                    it.topLeft = Point2D(x, y)
                }
            }
        }
    }

    /**
     * 1. fill canvas with white
     * 2. draw squares
     * 3. draw arrows
     */
    private fun draw() {
        canvas.graphicsContext2D.fill = Color.WHITE
        canvas.graphicsContext2D.fillRect(0.0, 0.0, width, height)
        for (rank in 1..8) {
            for (file in 1..8) {
                drawSquare(rank, file)
            }
        }
        // TODO draw arrows
    }

    /**
     * 1. square background
     * 2. square coordinates
     * 3. colored squares of last move
     * 4. colored squares of highlights
     * 5. pieces
     * 6. D&D squares highlight
     */
    private fun drawSquare(rank: Int, file: Int) {
        squareDataOf(rank, file).let {
            val x = it.topLeft.x
            val y = it.topLeft.y
            // 1. square background
            if (it.scaledImage != null) {
                canvas.graphicsContext2D.drawImage(it.scaledImage, x, y)
            } else {
                colorSquare(x, y, if (it.square.isWhite) Color.LIGHTGREY else Color.GRAY)
            }
            // 5. draw pieces
            if (it !== dragSquare) {
                position.square(rank, file).piece?.let {
                    canvas.graphicsContext2D.drawImage(scaledPieces[it.fenChar], x, y)
                }
            }
            // 6. D&D squares highlight
            if (it === dragSquare || it === dropSquare) {
                colorSquare(x, y, Color.YELLOW, 0.4)
            }
        }
    }

    private fun drawDragPiece(point: Point2D?) {
        // redraw the former drag-affected squares
        dragAffectedSquares.forEach { drawSquare(it.rank, it.file) }
        dragAffectedSquares.clear()
        if (point != null) {
            // draw drag scaledImage
            val x = point.x - squareSize / 2
            val y = point.y - squareSize / 2
            canvas.graphicsContext2D.drawImage(dragImage, x, y)
            // save the new drag-affected squares
            fun _add(x: Double, y: Double) {
                val s = squareAt(x, y)
                if (s.rank in 1..8 && s.file in 1..8) dragAffectedSquares.add(s)
            }
            _add(x, y)
            _add(x, y + squareSize)
            _add(x + squareSize, y)
            _add(x + squareSize, y + squareSize)
        } else {
            // d&d finished - clear all d&d related stuff
            userMove(dragSquare!!.square, dropSquare!!.square)
            dragImage = null
            dragSquare = null
            dropSquare = null
            draw()
        }
    }

    private fun userMove(from: Square, to: Square) {
        with (position) {
            if (legalMovesFrom(square(from.rank, from.file)).contains(square(to.rank, to.file))) {
                listener.forEach { it.moveIssued(from, to) }
            }
        }
    }

    private fun colorSquare(x: Double, y: Double, color: Color, alpha: Double = 1.0) {
        with (canvas.graphicsContext2D) {
            globalAlpha = alpha
            fill = color
            fillRect(x, y, squareSize.toDouble(), squareSize.toDouble())
            globalAlpha = 1.0
        }
    }

    private fun squareDataOf(square: Square) = squareDataOf(square.rank, square.file)

    private fun squareDataOf(rank: Int, file: Int) =
            squares.first { it.square.rank == rank && it.square.file == file }

    private fun squareAt(x: Double, y: Double): Square {
        val rank =  if (isOrientationWhite) 8 - y.toInt() / squareSize
                    else y.toInt() / squareSize + 1
        val file =  if (isOrientationWhite) x.toInt() / squareSize + 1
                    else 8 - x.toInt() / squareSize
        return Square(rank, file)
    }

    // for TestFX
    fun squareCenter(square: Square): Point2D {

        with (squareDataOf(square)) {
            return Point2D(topLeft.x + squareSize / 2, topLeft.y + squareSize / 2)
        }
    }

    private fun loadPieces(): Map<Char, SVGIcon> {

        // "wK" -> 'K'; "bK" -> 'k'
        fun toFen(str: String): Char =
                if (str.first() == 'w') str.drop(1)[0] else str.drop(1).toLowerCase()[0]

        val svgUniverse = SVGUniverse()
        return listOf("wK", "wQ", "wR", "wB", "wN", "wP", "bK", "bQ", "bR", "bB", "bN", "bP").associate {
            val svgIcon = SVGIcon()
            svgIcon.svgURI = svgUniverse.loadSVG(javaClass.getResource("/images/pieces/merida/$it.svg"))
            svgIcon.isScaleToFit = true
            svgIcon.antiAlias = true
            Pair(toFen(it), svgIcon)
        }
    }



}

interface ChessBoardListener {
    fun squareClicked(square: Square)
    fun moveIssued(from: Square, to: Square)
}