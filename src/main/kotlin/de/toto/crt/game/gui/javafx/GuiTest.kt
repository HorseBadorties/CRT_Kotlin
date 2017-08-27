package de.toto.crt.game.gui.javafx

import de.toto.crt.game.gui.swing.ChessBoard
import de.toto.crt.game.rules.FEN_STARTPOSITION
import de.toto.crt.game.rules.fromFEN
import javafx.application.Application
import javafx.embed.swing.SwingNode
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import java.awt.Dimension
import javax.swing.SwingUtilities

fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}


class App: Application() {

    override fun start(stage: Stage?) {
        stage?.title = "JavaFX-Tester"
        val border = BorderPane()

//        val board = ChessBoard()
//        board.setPosition(fromFEN(FEN_STARTPOSITION))
//        border.center = board

        val swingNode = SwingNode()
        val board = ChessBoard()
        board.setPosition(fromFEN(FEN_STARTPOSITION))
        board.preferredSize = Dimension(1600,1600)
        board.rescale()
        swingNode.content = board
        border.center = swingNode

        stage?.scene = Scene(border, 800.0, 800.0)
        println("${border.boundsInLocal},${border.boundsInParent}")
        stage?.show()
    }

}

