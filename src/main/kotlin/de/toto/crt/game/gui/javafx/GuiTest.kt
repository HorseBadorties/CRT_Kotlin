package de.toto.crt.game.gui.javafx

import de.toto.crt.game.rules.FEN_STARTPOSITION
import de.toto.crt.game.rules.fromFEN
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}

class App: Application() {

    override fun start(stage: Stage?) {
        stage?.title = "JavaFX-Tester"
        val border = BorderPane()
        val board = ChessBoard()
        board.setPosition(fromFEN(FEN_STARTPOSITION))
        border.center = board
        stage?.scene = Scene(border, 800.0, 800.0)
        stage?.show()
    }

}

