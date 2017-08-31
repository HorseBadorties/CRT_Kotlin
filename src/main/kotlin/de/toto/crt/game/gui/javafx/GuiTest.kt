package de.toto.crt.game.gui.javafx

import de.toto.crt.game.Game
import de.toto.crt.game.fromPGN
import de.toto.crt.game.rules.Square
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
import java.nio.file.Paths


fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}

class App: Application() {

    val pane = BorderPane()
    val board = ChessBoard()
    var game: Game

    init {
        val games = fromPGN(Paths.get(javaClass.getResource("/pgn/Repertoire_Black.pgn").toURI()))
        game = games.first()
        games.forEach { if (it !== game) game.mergeIn(it) }
    }

    override fun start(stage: Stage?) {
        stage?.title = "JavaFX-Tester with FX ChessBoard"

        board.setPosition(game.gotoStartPosition())
        board.listener.add(object: ChessBoardListener {
            override fun squareClicked(square: Square) = println("User clicked square $square")

            override fun moveIssued(from: Square, to: Square) {
                val pos = game.currentPosition.next.firstOrNull { it.move.contains(to.name) }
                if (pos != null) board.setPosition(game.gotoPosition(pos))
            }

        })
        pane.center = board

        // ToolBar
        val btnDrill = Button("Drill")
        val btnFlip = Button("Flip")
        btnFlip.onAction = EventHandler { board.flip() }
        val toolBar = ToolBar(btnDrill, btnFlip)
        pane.top = toolBar

        val scene = Scene(pane, 800.0, 800.0)
        with (scene.getAccelerators()) {
            put(KeyCodeCombination(KeyCode.LEFT), Runnable {
                    board.setPosition(game.back())
            })
            put(KeyCodeCombination(KeyCode.RIGHT), Runnable {
                board.setPosition(game.next())
            })
            put(KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN), Runnable {
                board.flip()
            })
        }
        stage?.scene = scene
        stage?.show()
    }


}

