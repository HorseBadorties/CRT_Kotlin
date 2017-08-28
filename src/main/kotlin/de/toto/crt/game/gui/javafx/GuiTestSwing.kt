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
    Application.launch(AppSwingBoard::class.java, *args)
}

class AppSwingBoard: Application() {

    val pane = BorderPane()
    val board = ChessBoard()

    override fun start(stage: Stage?) {
        stage?.title = "JavaFX-Tester with Swing ChessBoard"

        pane.widthProperty().addListener( { _, _, _ -> handleResize() } )
        pane.heightProperty().addListener( { _, _, _ -> handleResize() } )

        val swingNode = SwingNode()

        board.setPosition(fromFEN(FEN_STARTPOSITION))
        board.preferredSize = Dimension(800, 800)
        board.rescale()
        swingNode.content = board
        pane.center = swingNode

        stage?.scene = Scene(pane, 800.0, 800.0)
        stage?.show()
    }

    private fun handleResize() {
        val min = Math.min(pane.width, pane.height).toInt()
        SwingUtilities.invokeLater {
            board.preferredSize = Dimension(min, min)
            board.rescale()
            board.invalidate()
            board.repaint()
        }

    }

}