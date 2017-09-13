package de.toto.crt.game.gui.javafx

import de.toto.crt.game.Game
import de.toto.crt.game.GameListener
import de.toto.crt.game.Position
import de.toto.crt.game.fromPGN
import de.toto.crt.game.rules.Square
import de.toto.crt.game.rules.squaresOfMove
import de.toto.crt.game.rules.toFEN
import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCode.*
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCodeCombination.CONTROL_DOWN
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.controlsfx.control.StatusBar
import org.controlsfx.tools.Borders
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}

class App: Application() {

    var ourStage: Stage? = null
    val board = ChessBoard()
    var game = Game()
    val statusBar = StatusBar()
    val lstVariations = ListView<Position>()
    val lstMoves = ListView<Position>()
    val drillPositions = LinkedList<Position>()

    fun fromResource(name: String) = Paths.get(javaClass.getResource(name).toURI())

    override fun start(stage: Stage?) {
        ourStage = stage
        stage?.title = "JavaFX-Tester with FX ChessBoard"

        initBoard()
        loadPGN(fromResource("/pgn/Repertoire_Black.pgn"))

        val pane = BorderPane()
        val variationsAndMoves = SplitPane().apply {
            orientation = Orientation.HORIZONTAL
            items.add(BorderPane().apply {
                top = Label("Variations")
                center = lstVariations
                minWidth = 50.0
            })
            items.add(BorderPane().apply {
                top = Label("Movelist")
                center = lstMoves
                minWidth = 50.0
            })
        }


        SplitPane().apply {
            orientation = Orientation.HORIZONTAL
            items.add(board)
            items.add(Borders.wrap(variationsAndMoves).emptyBorder().padding(5.0).build().build())
            pane.center = this
        }

        lstVariations.onMouseClicked = EventHandler { gotoSelectedVariation() }
        lstVariations.onKeyPressed = EventHandler { e -> if (e.code == KeyCode.ENTER) gotoSelectedVariation() }

        // ToolBar
        val toolBar = ToolBar()
        with (Button("Load")) {
            graphic = ImageView(Image(this@App.javaClass.getResourceAsStream("/images/icon/Open in Popup-32.png")))
            contentDisplay = ContentDisplay.TOP
            onAction = EventHandler { loadPGN(pickPGN()) }
            toolBar.items.add(this)
        }
        with (Button("Drill")) {
            graphic = ImageView(Image(this@App.javaClass.getResourceAsStream("/images/icon/Make Decision red2-32.png")))
            contentDisplay = ContentDisplay.TOP
            onAction = EventHandler { drill() }
            toolBar.items.add(this)
        }
        with (Button("Settings")) {
            graphic = ImageView(Image(this@App.javaClass.getResourceAsStream("/images/icon/Settings-32.png")))
            contentDisplay = ContentDisplay.TOP
            onAction = EventHandler { settings() }
            toolBar.items.add(this)
        }
        pane.top = toolBar
        pane.bottom = statusBar

        val scene = Scene(pane, Prefs.getDouble(Prefs.FRAME_WIDTH, 800.0), Prefs.getDouble(Prefs.FRAME_HEIGHT, 800.0))
        with (scene.getAccelerators()) {
            put(KeyCodeCombination(HOME), Runnable { game.gotoStartPosition() })
            put(KeyCodeCombination(LEFT), Runnable { back() })
            put(KeyCodeCombination(RIGHT), Runnable { next() })
            put(KeyCodeCombination(F, CONTROL_DOWN), Runnable { board.flip() })
            put(KeyCodeCombination(S, CONTROL_DOWN), Runnable { settings() })
        }
        stage?.scene = scene
        stage?.icons?.add(Image("/images/icon/White Knight-96.png"))
        stage?.show()
    }

    private fun gotoSelectedVariation() {
        game.gotoPosition(lstVariations.selectionModel.selectedItem)
    }

    private fun back() {
        game.back()
    }

    private fun next() {
        if (lstVariations.selectionModel.selectedItem != null) {
            gotoSelectedVariation()
        } else {
            game.next()
        }
    }


    fun pickPGN(): Path {
        with (FileChooser()) {
            title = "Select a pgn file that contains your repertoire"
            extensionFilters.add(FileChooser.ExtensionFilter("PGN Files", "*.pgn"))
            return showOpenDialog(ourStage)?.toPath() ?: fromResource("/pgn/Repertoire_Black.pgn")
        }
    }

    fun loadPGN(path: Path) {
        game?.listener.remove(gameListener)
        val games = fromPGN(path)
        game = games.first()
        games.forEach { if (it !== game) game.mergeIn(it) }
        game.listener.add(gameListener)
        game.gotoStartPosition()
        updateStatusBar("$path loaded")
    }

    override fun stop() {
        with (Prefs) {
            set(Prefs.WHITE_PERSPECTIVE, board.isOrientationWhite)
            set(Prefs.SHOW_BOARD, board.isShowingBoard)
            set(Prefs.SHOW_PIECES, board.isShowingPieces)
            set(Prefs.SHOW_COORDINATES, board.isShowingSquareCoordinates)
            set(Prefs.SHOW_GRAPHICS_COMMENTS, board.isShowingGraphicsComments)
            set(Prefs.FRAME_WIDTH, ourStage?.scene?.width ?: 800.0)
            set(Prefs.FRAME_HEIGHT, ourStage?.scene?.height ?: 800.0)
        }
        super.stop()
    }

    private fun settings() = SettingsDialog(ourStage?.scene?.window, BoardProperties(board), FooProperties()).show()

    private fun initBoard() {
        board.apply {
            position = game.gotoStartPosition()
            isOrientationWhite = Prefs.getBoolean(Prefs.WHITE_PERSPECTIVE)
            isShowingBoard = Prefs.getBoolean(Prefs.SHOW_BOARD)
            isShowingPieces = Prefs.getBoolean(Prefs.SHOW_PIECES)
            isShowingSquareCoordinates = Prefs.getBoolean(Prefs.SHOW_COORDINATES)
            isShowingGraphicsComments = Prefs.getBoolean(Prefs.SHOW_GRAPHICS_COMMENTS)
            listener.add(chessBoardListener)
        }
    }

    private val chessBoardListener = object: ChessBoardListener {
        override fun squareClicked(square: Square) {
            val pos = game.currentPosition.next.firstOrNull { it.squaresOfMove[1].sameRankAndFileAs(square) }
            if (pos != null) {
                game.gotoPosition(pos)
                userMoved()
            }
        }

        override fun moveIssued(from: Square, to: Square) {
            val pos = game.currentPosition.next.firstOrNull { it.squaresOfMove.containsAll(listOf(from, to)) }
            if (pos != null) {
                game.gotoPosition(pos)
                userMoved()
            }
        }
    }

    private val gameListener = object: GameListener {
        override fun positionChanged() {
            board.position = game.currentPosition
            lstVariations.items.clear()
            lstVariations.items.addAll(game.currentPosition.next)
            lstMoves.items.clear()

            updateStatusBar()
        }
    }

    private fun updateStatusBar(_text: String? = null) {
        with (statusBar) {
            with (game.currentPosition) {
                if (_text != null) {
                    text = _text
                } else {
                    text = if (move.isEmpty()) "" else movenumberMoveNAGs
                }
                rightItems.clear()
                rightItems.add(Label(toFEN()))
            }
        }
    }

    private fun drill() {
        if (drillPositions.isEmpty()) {
            drillPositions.clear()
            drillPositions.addAll(game.currentPosition.preOrderDepthFirst(shuffle = true) {
                // only mainline
                if (it.whiteToMove != board.isOrientationWhite) {
                    it.previous?.next?.getOrNull(0) == it
                } else it.hasNext
            })
            if (!drillPositions.isEmpty() && drillPositions[0].whiteToMove == board.isOrientationWhite) {
                doDrillMove()
            }

        } else {
            drillPositions.clear()
            game.gotoStartPosition()
        }
    }

    private fun doDrillMove() {
        if (!drillPositions.isEmpty()) {
            val nextDrillPosition = drillPositions.poll()
            if (nextDrillPosition.next.size > 1) {
                // we have an alternative variation at hand


            }
            game.gotoPosition(nextDrillPosition)
        }
    }

    private fun userMoved() {
        if (!drillPositions.isEmpty() && drillPositions[0] == game.currentPosition) {
            drillPositions.poll()
            Thread({
                Thread.sleep(500)
                Platform.runLater { doDrillMove() }
            }).start()
        }

    }

}
