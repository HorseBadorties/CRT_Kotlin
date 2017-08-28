package de.toto.crt.game.gui.javafx

import de.toto.crt.game.fromPGN
import de.toto.crt.game.rules.FEN_STARTPOSITION
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
    val game = fromPGN(Paths.get(javaClass.getResource("/pgn/Repertoire_Black.pgn").toURI()))[0]

    override fun start(stage: Stage?) {
        stage?.title = "JavaFX-Tester with FX ChessBoard"


        board.setPosition(game.gotoStartPosition())
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

