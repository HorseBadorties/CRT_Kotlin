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
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ToolBar
import javafx.scene.image.Image
import javafx.scene.input.KeyCode.*
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCodeCombination.*
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.controlsfx.control.StatusBar
import java.nio.file.Paths
import java.util.*


fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}

class App: Application() {

    var ourStage: Stage? = null
    val board = ChessBoard()
    var game: Game
    val statusBar = StatusBar()
    val drillPositions = LinkedList<Position>()

    init {
        val games = fromPGN(fromResource("/pgn/Repertoire_Black.pgn"))
//        val games = fromPGN(Paths.getOrNull(javaClass.getResource("/pgn/GraphicsCommentsAndNAGs.pgn").toURI()))
//        val games = fromPGN(Paths.getOrNull(javaClass.getResource("/pgn/TestRepertoire.pgn").toURI()))
        game = games.first()
        games.forEach { if (it !== game) game.mergeIn(it) }
    }

    fun fromResource(name: String) = Paths.get(javaClass.getResource(name).toURI())

     override fun start(stage: Stage?) {
        ourStage = stage
        stage?.title = "JavaFX-Tester with FX ChessBoard"
        val pane = BorderPane()

        game.listener.add(gameListener)

        initBoard()
        pane.center = board

        // ToolBar
        val btnDrill = Button("Drill")
        btnDrill.onAction = EventHandler { drill() }
        val btnSettings = Button("Settings")
        btnSettings.onAction = EventHandler { settings() }
        val toolBar = ToolBar(btnDrill, btnSettings)
        pane.top = toolBar

        pane.bottom = statusBar

        val scene = Scene(pane, Prefs.getDouble(Prefs.FRAME_WIDTH, 800.0), Prefs.getDouble(Prefs.FRAME_HEIGHT, 800.0))
        with (scene.getAccelerators()) {
            put(KeyCodeCombination(HOME), Runnable { board.position = game.gotoStartPosition() })
            put(KeyCodeCombination(LEFT), Runnable { board.position = game.back() })
            put(KeyCodeCombination(RIGHT), Runnable { board.position =  game.next() })
            put(KeyCodeCombination(F, CONTROL_DOWN), Runnable { board.flip() })
            put(KeyCodeCombination(S, CONTROL_DOWN), Runnable { settings() })
        }
        stage?.scene = scene
        stage?.icons?.add(Image("/images/icon/White Knight-96.png"))
        stage?.show()
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
                board.position = game.gotoPosition(pos)
                userMoved()
            }
        }

        override fun moveIssued(from: Square, to: Square) {
            val pos = game.currentPosition.next.firstOrNull { it.squaresOfMove.containsAll(listOf(from, to)) }
            if (pos != null) {
                board.position = game.gotoPosition(pos)
                userMoved()
            }
        }
    }

    private val gameListener = object: GameListener {
        override fun positionChanged() {
            updateStatusBar()
        }
    }

    private fun updateStatusBar() {
        statusBar.text = if (game.currentPosition.move.isEmpty()) "" else game.currentPosition.movenumberMoveNAGs
        statusBar.rightItems.clear()
        statusBar.rightItems.add(Label(game.currentPosition.toFEN()))
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
            board.position = game.gotoStartPosition()
        }
    }

    private fun doDrillMove() {
        if (!drillPositions.isEmpty()) {
            val nextDrillPosition = drillPositions.poll()
            if (nextDrillPosition.next.size > 1) {
                // we have an alternative variation at hand


            }
            board.position = game.gotoPosition(nextDrillPosition)
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

