package de.toto.crt.game.gui.javafx

import de.toto.crt.game.Game
import de.toto.crt.game.fromPGN
import de.toto.crt.game.rules.FEN_STARTPOSITION
import de.toto.crt.game.rules.Square
import de.toto.crt.game.rules.fromFEN
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import java.nio.file.Paths
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination


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
        board.addListener(object: ChessBoardListener {
            override fun squareClicked(square: Square) {}

            override fun moveIssued(from: Square, to: Square) {
                val pos = game.currentPosition.next.firstOrNull { it.move.contains(to.name) }
                if (pos != null) board.setPosition(game.gotoPosition(pos))
            }

        })
        pane.center = board

        val scene = Scene(pane, 800.0, 800.0)
        with (scene.getAccelerators()) {
            put(KeyCodeCombination(KeyCode.LEFT, KeyCombination.SHORTCUT_DOWN), Runnable {
                    board.setPosition(game.back())
            })
            put(KeyCodeCombination(KeyCode.RIGHT, KeyCombination.SHORTCUT_DOWN ), Runnable {
                board.setPosition(game.next())
            })
        }
        stage?.scene = scene
        stage?.show()
    }


}

