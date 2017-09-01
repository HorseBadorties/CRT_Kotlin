package de.toto.crt.game.gui.javafx

import com.kitfox.svg.SVGUniverse
import com.kitfox.svg.app.beans.SVGIcon
import de.toto.crt.game.ColoredArrow
import de.toto.crt.game.ColoredSquare
import de.toto.crt.game.Position
import de.toto.crt.game.rules.Square
import de.toto.crt.game.rules.squaresOfMove
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import javafx.scene.image.WritableImage
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.paint.Color
import java.awt.Dimension
import java.awt.image.BufferedImage

private class SquareData(
    var square: Square,
    var topLeft: Point2D = Point2D(0.0, 0.0),
    var scaledImage: Image? = null,
    var color: Color? = null,
    var lastMoveSquare: Boolean = false)

class ChessBoard : Pane() {

    val listener = mutableListOf<ChessBoardListener>()

    private var isOrientationWhite = true
        set(value) {
            field = value
            calcSquarePoints()
            draw()
        }
    fun flip() { isOrientationWhite = !isOrientationWhite }

    var position = Position()
        set(value) {
            if (value != position) {
                field = value
                positionSquaresToSquareData()
                draw()
            }
        }

    private val canvas = Canvas()
    private val arrowCanvas = Canvas()
    private var squareSize: Int = 0
    private val boardImageURL: String? = null // "/images/board/maple.jpg"
    private val pieceIcons: Map<Char, SVGIcon> = loadPieces()
    private val scaledPieces = mutableMapOf<Char, Image>()
    private val squares: List<SquareData> = squareData()
    private var dragSquare: SquareData? = null
    private var dragImage: Image? = null
    private val dragAffectedSquares = mutableSetOf<Square>()
    private var dropSquare: SquareData? = null

    // TODO move into CSS
    private val squareSelectionColor = Color(0.3, 0.4, 0.5, 0.6);
    private val lightBrown = Color.rgb(208, 192, 160);
    private val darkBrown = Color.rgb(160, 128, 80);
    private val highlightColorGreen = Color(0.0, 1.0, 0.0, 0.4);
    private val highlightColorRed = Color(1.0, 0.0, 0.0, 0.4);
    private val highlightColorYellow = Color(1.0, 1.0, 0.0, 0.4);

    init {
        children.add(canvas)
        children.add(arrowCanvas)

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
                    drawDragPiece(Point2D(e.x, e.y))
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
            listener.forEach { it.moveIssued(dragSquare!!.square, dropSquare!!.square) }
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
                result.add(SquareData(position.square(rank, file)))
            }
        }
        return result.toList()
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
        arrowCanvas.width = size
        arrowCanvas.height = width
        // calc new squareSize
        squareSize = (canvas.width / 8).toInt()
        // calc square topLeft points
        calcSquarePoints()
        // scale square images
        if (boardImageURL != null) {
            try {
                val boardImageScaled = Image(boardImageURL, canvas.width, canvas.width, true, true)
                for (rank in 1..8) {
                    for (file in 1..8) {
                        with (squareDataOf(rank, file)) {
                            scaledImage = WritableImage(boardImageScaled.pixelReader,
                                    topLeft.x.toInt(), topLeft.y.toInt(), squareSize, squareSize)
                        }
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
     * 1. clear canvas
     * 2. draw squares
     * 3. draw arrows
     */
    private fun draw() {
        with (arrowCanvas.graphicsContext2D) {
            clearRect(0.0, 0.0, width, height)
        }
        with (canvas.graphicsContext2D) {
            clearRect(0.0, 0.0, width, height)
        }
        for (rank in 1..8) {
            for (file in 1..8) {
                drawSquare(rank, file)
            }
        }
        if (dragSquare == null) {
            position.graphicsComments.filterIsInstance<ColoredArrow>().forEach {
                drawColoredArrow(it)
            }
        }
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
                colorSquare(x, y, if (it.square.isWhite) lightBrown else darkBrown)
            }
            // TODO 2. square coordinates

            // 3. colored squares of last move
            if (it.lastMoveSquare && dragSquare == null) {
                colorSquare(x, y, squareSelectionColor)
            }

            // 4. colored squares of highlights
            if (it.color != null && dragSquare == null) {
                colorSquare(x, y, translateColor(it.color)!!)
            }

            // 5. draw pieces
            if (it !== dragSquare) {
                position.square(rank, file).piece?.let {
                    canvas.graphicsContext2D.drawImage(scaledPieces[it.fenChar], x, y)
                }
            }
            // 6. D&D squares highlight
            if (it === dragSquare || it === dropSquare) {
                colorSquare(x, y, squareSelectionColor)
            }
        }
    }

    private fun translateColor(color: Color?) = when (color) {
        Color.RED -> highlightColorRed
        Color.YELLOW -> highlightColorYellow
        Color.GREEN -> highlightColorGreen
        else -> color
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
            dragImage = null
            dragSquare = null
            dropSquare = null
            draw()
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

    private fun drawColoredArrow(arrow: ColoredArrow) {
        fun Point2D.squareCenter() = Point2D(x + squareSize / 2, y + squareSize / 2)

        val pointFrom = squareDataOf(arrow.from).topLeft.squareCenter()
        val pointTo = squareDataOf(arrow.to).topLeft.squareCenter()
        val distance = pointFrom.distance(pointTo)
        val arrowHeight = (squareSize / 10).toDouble()
        val arrowheadSide = (squareSize / 3).toDouble()
        val arrowheadLength = arrowheadSide

        with (arrowCanvas.graphicsContext2D) {
            save()
            val midPoint = Point2D((pointFrom.x + pointTo.x) / 2,  (pointFrom.y + pointTo.y) / 2)
            translate(pointFrom.x, pointFrom.y)
            val degrees = Math.atan2(pointTo.y - pointFrom.y, pointTo.x - pointFrom.x)
//            rotate(-20.0)
            beginPath()
            moveTo(-distance / 2, arrowHeight / 2)
            lineTo(distance / 2 - arrowheadLength, arrowHeight / 2)
            lineTo(distance / 2 - arrowheadLength, arrowheadSide / 2)
            lineTo(distance / 2, 0.0)
            lineTo(distance / 2 - arrowheadLength, -(arrowheadSide / 2))
            lineTo(distance / 2 - arrowheadLength, -(arrowHeight / 2))
            lineTo(-distance / 2, -(arrowHeight / 2))
            closePath()

            fill  = translateColor(arrow.color)
            fill()
            translate(-midPoint.x, -midPoint.y)
            restore()
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

    private fun positionSquaresToSquareData() {
        fun Square.sameRankAndFileAs(s: Square) = rank == s.rank && file == s.file
        val coloredSquares = position.graphicsComments.filterIsInstance<ColoredSquare>()
        val squaresOfMove = position.squaresOfMove
        for (rank in 1..8) {
            for (file in 1..8) {
                with (squareDataOf(rank, file)) {
                    square = position.square(rank, file)
                    color = coloredSquares.firstOrNull { it.square.sameRankAndFileAs(square) }?.color
                    lastMoveSquare = squaresOfMove.any { it.sameRankAndFileAs(square) }
                }
            }
        }
    }

}

interface ChessBoardListener {
    fun squareClicked(square: Square)
    fun moveIssued(from: Square, to: Square)
}