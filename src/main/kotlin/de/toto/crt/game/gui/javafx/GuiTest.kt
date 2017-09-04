package de.toto.crt.game.gui.javafx

import de.toto.crt.game.Game
import de.toto.crt.game.GameListener
import de.toto.crt.game.fromPGN
import de.toto.crt.game.rules.Square
import de.toto.crt.game.rules.squaresOfMove
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ToolBar
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import org.controlsfx.control.StatusBar
import java.nio.file.Paths
import com.kitfox.svg.batik.MultipleGradientPaint.NO_CYCLE
import de.toto.crt.game.Position
import javafx.application.Platform
import javafx.scene.Group
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.shape.Rectangle
import java.util.*


fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}

class App: Application() {

    val pane = BorderPane()
    val board = ChessBoard()
    var game: Game
    val statusBar = StatusBar()
    val drillPositions = LinkedList<Position>()

    init {
        val games = fromPGN(Paths.get(javaClass.getResource("/pgn/Repertoire_Black.pgn").toURI()))
//        val games = fromPGN(Paths.get(javaClass.getResource("/pgn/TestRepertoire.pgn").toURI()))
        game = games.first()
        games.forEach { if (it !== game) game.mergeIn(it) }
    }

//    override fun start(primaryStage: Stage) {
//
//        val mainGroup = Group()
//        val r = Rectangle(0.0, 0.0, 200.0, 200.0)
//
//        val stop1 = Stop(0.0, Color(0.0, 0.0, 1.0, 0.2))
//        val stop2 = Stop(1.0, Color(0.0, 0.0, 1.0, 0.8))
//        val lg = LinearGradient(0.0, 0.0, 200.0, 200.0, false,
//                CycleMethod.NO_CYCLE,
//                stop1, stop2)
//        r.setFill(lg)
//
//        mainGroup.getChildren().addAll(r)
//        val scene = Scene(mainGroup, 200.0, 200.0)
//        primaryStage.scene = scene
//        primaryStage.show()
//    }

     override fun start(stage: Stage?) {
        stage?.title = "JavaFX-Tester with FX ChessBoard"

        game.listener.add(gameListener)

        board.position = game.gotoStartPosition()
        board.listener.add(chessBoardListener)
        pane.center = board

        // ToolBar
        val btnDrill = Button("Drill")
        btnDrill.onAction = EventHandler { drill() }
         val btnCoordinates = Button("Square Coordinates")
        btnCoordinates.onAction = EventHandler { board.isShowingSquareCoordinates = !board.isShowingSquareCoordinates }
        val toolBar = ToolBar(btnDrill, btnCoordinates)
        pane.top = toolBar

        pane.bottom = statusBar

        val scene = Scene(pane, 800.0, 800.0)
        with (scene.getAccelerators()) {
            put(KeyCodeCombination(KeyCode.LEFT), Runnable {
                    board.position = game.back()
            })
            put(KeyCodeCombination(KeyCode.RIGHT), Runnable {
                board.position =  game.next()
            })
            put(KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN), Runnable {
                board.flip()
            })
        }
        stage?.scene = scene
        stage?.icons?.add(Image("/images/icon/White Knight-96.png"))
        stage?.show()
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
        statusBar.text = game.currentPosition.comment
    }

    private fun drill() {
        if (drillPositions.isEmpty()) {
            drillPositions.clear()
            drillPositions.addAll(game.currentPosition.preOrderDepthFirst(shuffle = true) {
                // only mainline
                if (it.whiteToMove != board.isOrientationWhite) {
//                    it.previous?.next?.get(0) == it
                    true
                } else it.hasNext
//                true
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

