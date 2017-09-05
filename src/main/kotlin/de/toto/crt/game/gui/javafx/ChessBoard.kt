package de.toto.crt.game.gui.javafx

import com.kitfox.svg.SVGUniverse
import com.kitfox.svg.app.beans.SVGIcon
import com.sun.javafx.tk.Toolkit
import de.toto.crt.game.ColoredArrow
import de.toto.crt.game.ColoredSquare
import de.toto.crt.game.Position
import de.toto.crt.game.forEachRankAndFile
import de.toto.crt.game.rules.Square
import de.toto.crt.game.rules.squaresOfMove
import javafx.embed.swing.SwingFXUtils
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.scene.input.ClipboardContent
import javafx.scene.input.TransferMode
import javafx.scene.layout.Pane
import java.awt.Dimension
import java.awt.image.BufferedImage
import de.toto.crt.game.gui.javafx.ChessBoard.Layer.*
import javafx.scene.paint.*
import javafx.scene.text.Font

private class SquareData(
    var square: Square,
    var topLeft: Point2D = Point2D(0.0, 0.0),
    var highlightColor: Color? = null,
    var isLastMoveSquare: Boolean = false)

class ChessBoard : Pane() {

    val listener = mutableListOf<ChessBoardListener>()

    var isOrientationWhite = true
        set(value) {
            field = value
            calcSquarePoints()
            drawSquareCoordinates()
            drawPosition()
        }
    fun flip() { isOrientationWhite = !isOrientationWhite }

    var isShowingSquareCoordinates = true
        set(value) {
            field = value
            canvasLayer[SQUARE_COORDINATES]?.isVisible = value
        }

    var isShowingBoard = true
        set(value) {
            field = value
            canvasLayer[BOARD]?.isVisible = value
            canvasLayer[SQUARE_COORDINATES]?.isVisible = value
            canvasLayer[PIECES]?.isVisible = value
            canvasLayer[SQUARE_HIGHLIGHTS]?.isVisible = value
        }

    var isShowingPieces = true
        set(value) {
            field = value
            canvasLayer[PIECES]?.isVisible = value
        }

    var isShowingGraphicsComments = true
        set(value) {
            field = value
            canvasLayer[ARROWS]?.isVisible = value
        }

    var position = Position()
        set(value) {
            if (value != position) {
                field = value
                updateSquareData(value)
                drawPosition()
            }
        }

    private enum class Layer { BOARD, SQUARE_COORDINATES, SQUARE_HIGHLIGHTS, PIECES, ARROWS, DRAG_DROP }
    private val canvasLayer: Map<Layer, Canvas> = Layer.values().associate { it to Canvas() }

    private var squareSize: Int = 0
    private val boardImageURL: String? = "/images/board/maple.jpg"
    private var boardImageScaled: Image? = null
    private val pieceIcons: Map<Char, SVGIcon> = loadPieces()
    private val scaledPieces = mutableMapOf<Char, Image>()
    private val squares: List<SquareData> = squareData()
    private var dragSquare: SquareData? = null
    private var dragImage: Image? = null
    private var dropSquare: SquareData? = null

    // TODO move into CSS
    private val squareSelectionColor = Color(0.3, 0.4, 0.5, 0.6)
    private val lightBrown = Color.rgb(208, 192, 160)
    private val darkBrown = Color.rgb(160, 128, 80)
    private val highlightColorGreen = Color(0.0, 1.0, 0.0, 0.4)
    private val highlightColorRed = Color(1.0, 0.0, 0.0, 0.4)
    private val highlightColorYellow = Color(1.0, 1.0, 0.0, 0.4)

    init {
        canvasLayer.values.forEach { children.add(it) }

        // Drag&Drop handling
        with (canvasLayer[DRAG_DROP]!!) {
            setOnDragDetected { e ->
                val s = squareAt(e.x, e.y)
                val piece = position.square(s.rank, s.file).piece
                if (piece != null) {
                    // we need to put something on the dragboard to effectivly start dragging
                    val dragboard = startDragAndDrop(*TransferMode.ANY)
                    val content = ClipboardContent()
                    content.putString("")
                    dragboard.setContent(content)
                    e.consume()
                    dragSquare = squareDataOf(s)
                    dragImage = scaledPieces[piece.fenChar]
                    drawPieces()
                    drawDragPiece(Point2D(e.x, e.y))
                }
                e.consume()
            }
            setOnDragOver { e ->
                e.acceptTransferModes(*TransferMode.ANY)
                dropSquare = squareDataOf(squareAt(e.x, e.y))
                drawDragPiece(Point2D(e.x, e.y))
                e.consume()
            }
            setOnDragDropped { e ->
                e.isDropCompleted = true
                e.consume()
            }
            setOnDragDone { e ->
                e.consume()
                // d&d finished - clear all d&d related stuff
                clearCanvas(DRAG_DROP)
                listener.forEach { it.moveIssued(dragSquare!!.square, dropSquare!!.square) }
                dragImage = null
                dragSquare = null
                dropSquare = null
                drawPieces()
            }
            setOnMouseClicked { e ->
                listener.forEach { it.squareClicked(squareAt(e.x, e.y)) }
            }
        }

    }

    private fun squareData(): List<SquareData> {
        val result = mutableListOf<SquareData>()
        forEachRankAndFile { rank, file -> result.add(SquareData(position.square(rank, file))) }
        return result
    }

    override fun layoutChildren() {
        scaleAll()
        drawBoard()
        drawSquareCoordinates()
        drawPosition()
    }

    private fun canvasSize(): Double = canvasLayer[BOARD]?.height ?: 0.0

    private fun scaleAll() {
        // set the size of the canvases - it has to be always divisible by 8
        var s = Math.min(width, height).toInt()
        while (s % 8 != 0) s--
        val canvasSize = s.toDouble()
        canvasLayer.values.forEach {
            it.width = canvasSize
            it.height = canvasSize
        }
        // calc new squareSize
        squareSize = (canvasSize / 8).toInt()
        // calc square topLeft points
        calcSquarePoints()
        // scale square images
        if (boardImageURL != null) {
            try {
                boardImageScaled = Image(boardImageURL, canvasSize, canvasSize, false, true)
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
        forEachSquareWithRankAndFile { rank, file ->
            val x = if (isOrientationWhite) ((file - 1) * squareSize).toDouble() else canvasSize() - file  * squareSize
            val y = if (isOrientationWhite) canvasSize() - rank * squareSize else ((rank - 1) * squareSize).toDouble()
            topLeft = Point2D(x, y)
        }
    }

    private fun drawBoard() {
        with (canvasLayer[BOARD]!!.graphicsContext2D) {
            if (boardImageScaled != null) {
                drawImage(boardImageScaled, 0.0, 0.0)
            } else forEachSquare {
               colorSquare(topLeft.x, topLeft.y, if (square.isWhite) lightBrown else darkBrown)
            }
        }
    }

    private fun drawSquareCoordinates() {
        clearCanvas(SQUARE_COORDINATES)
        with (canvasLayer[SQUARE_COORDINATES]!!.graphicsContext2D) {
            font = Font("Dialog", squareSize / 7.0)
            val fontHeight = Toolkit.getToolkit().fontLoader.getFontMetrics(font).ascent
            val span = squareSize / 25
            forEachSquareWithRankAndFile { rank, file ->
                val coordinateRankAndFile =  if (isOrientationWhite) 1 else 8
                if (rank == coordinateRankAndFile || file == coordinateRankAndFile) {
                    fill = if (square.isWhite) darkBrown else lightBrown
                    fillText(square.name, topLeft.x + span, topLeft.y + fontHeight + span)
                }
            }
        }
    }

    private fun drawSquareHighlights() {
        clearCanvas(SQUARE_HIGHLIGHTS)
        with (canvasLayer[SQUARE_HIGHLIGHTS]!!.graphicsContext2D) {
            forEachSquare {
                if (isLastMoveSquare) colorSquare(topLeft.x, topLeft.y, squareSelectionColor)
                if (highlightColor != null && isShowingGraphicsComments) {
                    colorSquare(topLeft.x, topLeft.y, translateColor(highlightColor)!!)
                }
            }
        }
    }

    private fun drawPieces() {
        clearCanvas(PIECES)
        with (canvasLayer[PIECES]!!.graphicsContext2D) {
            forEachSquareWithRankAndFile { rank, file ->
                if (this !== dragSquare) {
                    position.square(rank, file).piece?.let {
                        drawImage(scaledPieces[it.fenChar], topLeft.x, topLeft.y)
                    }
                }
            }
        }
    }

    private fun drawDragPiece(point: Point2D) {
        clearCanvas(DRAG_DROP)
        with (canvasLayer[DRAG_DROP]!!.graphicsContext2D) {
            forEachSquare {
                if (this === dragSquare || this === dropSquare) {
                    colorSquare(topLeft.x, topLeft.y, squareSelectionColor)
                }
            }
            if (isShowingPieces) {
                val x = point.x - squareSize / 2
                val y = point.y - squareSize / 2
                drawImage(dragImage, x, y)
            }
        }
    }

    private fun clearCanvas(layer: Layer) = canvasLayer[layer]!!.graphicsContext2D.clearRect(0.0, 0.0, width, height)

    private fun drawPosition() {
        drawSquareHighlights()
        drawPieces()
        drawColoredArrows()
    }

    private fun translateColor(color: Color?) = when (color) {
        Color.RED -> highlightColorRed
        Color.YELLOW -> highlightColorYellow
        Color.GREEN -> highlightColorGreen
        else -> color
    }


    private fun GraphicsContext.colorSquare(x: Double, y: Double, color: Color, alpha: Double = 1.0) {
        save()
        globalAlpha = alpha
        fill = color
        fillRect(x, y, squareSize.toDouble(), squareSize.toDouble())
        restore()
    }

    private fun drawColoredArrows() {
        canvasLayer[ARROWS]!!.graphicsContext2D.clearRect(0.0, 0.0, width, height)
        position.graphicsComments.filterIsInstance<ColoredArrow>().forEach { drawColoredArrow(it) }
    }

    private fun drawColoredArrow(arrow: ColoredArrow) {
        fun Point2D.squareCenter() = Point2D(x + squareSize / 2, y + squareSize / 2)

        val pointFrom = squareDataOf(arrow.from).topLeft.squareCenter()
        val pointTo = squareDataOf(arrow.to).topLeft.squareCenter()
        val distance = pointFrom.distance(pointTo)
        val arrowHeight = (squareSize / 10).toDouble()
        val arrowheadSide = (squareSize / 3).toDouble()
        val arrowheadLength = arrowheadSide

        with (canvasLayer[ARROWS]!!.graphicsContext2D) {
            save()
            val midPoint = Point2D((pointFrom.x + pointTo.x) / 2, (pointFrom.y + pointTo.y) / 2)
            translate(midPoint.x, midPoint.y)
            val theta = Math.atan2(pointFrom.y - pointTo.y, pointFrom.x - pointTo.x)
            rotate(180 + Math.toDegrees(theta))
            beginPath()
            moveTo(-distance / 2, arrowHeight / 2)
            lineTo(distance / 2 - arrowheadLength, arrowHeight / 2)
            lineTo(distance / 2 - arrowheadLength, arrowheadSide / 2)
            lineTo(distance / 2, 0.0)
            lineTo(distance / 2 - arrowheadLength, -arrowheadSide / 2)
            lineTo(distance / 2 - arrowheadLength, -arrowHeight / 2)
            lineTo(-distance / 2, -arrowHeight / 2)
            closePath()
            val from = Color(arrow.color.red, arrow.color.green, arrow.color.blue, 0.3)
            val to = Color(arrow.color.red, arrow.color.green, arrow.color.blue, 0.6)
            fill = LinearGradient(0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE,
                    Stop(0.0, from), Stop(1.0, to))
            fill()
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

    private fun updateSquareData(pos: Position) {
        val coloredSquares = pos.graphicsComments.filterIsInstance<ColoredSquare>()
        val squaresOfMove = pos.squaresOfMove
        forEachSquareWithRankAndFile { rank, file ->
            square = pos.square(rank, file)
            highlightColor = coloredSquares.firstOrNull { it.square.sameRankAndFileAs(square) }?.color
            isLastMoveSquare = squaresOfMove.any { it.sameRankAndFileAs(square) }
        }
    }

    private fun forEachSquare(action: SquareData.() -> Unit) {
        forEachRankAndFile { rank, file -> action(squareDataOf(rank, file)) }
    }

    private fun forEachSquareWithRankAndFile(action: SquareData.(rank: Int, file: Int) -> Unit) {
        forEachRankAndFile { rank, file -> action(squareDataOf(rank, file), rank, file) }
    }

}

interface ChessBoardListener {
    fun squareClicked(square: Square)
    fun moveIssued(from: Square, to: Square)
}