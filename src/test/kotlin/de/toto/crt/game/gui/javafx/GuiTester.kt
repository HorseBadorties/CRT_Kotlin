package de.toto.crt.game.gui.javafx

import de.toto.crt.game.rules.Square
import javafx.geometry.Point2D
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.stage.Screen
import javafx.stage.Stage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit
import org.testfx.framework.junit.ApplicationTest

class GuiTester: ApplicationTest() {

    private lateinit var ourStage: Stage
    private lateinit var board: ChessBoard

    @Before
    fun setupClass() {
        ApplicationTest.launch(App::class.java)
        board = lookup<ChessBoard> { it is ChessBoard }.query()
    }

    override fun start(stage: Stage?) {
        stage?.show()
        ourStage = stage!!

    }

    @After
    fun afterEachTest() {
        FxToolkit.hideStage()
        release(*arrayOf<KeyCode>())
        release(*arrayOf<MouseButton>())
    }

    @Test
    fun testStartup() {
        press(KeyCode.RIGHT)
        press(KeyCode.LEFT)
        move("c2", "c4")
        move("g8", "f6")
        move("b1", "c3")
//        drag(findSquare("c2"))
//        dropTo(findSquare("c4"))
        clickOn(findSquare("a1"))
        clickOn(findSquare("a8"))
        clickOn(findSquare("h1"))
        clickOn(findSquare("h8"))
    }

    fun translate(p: Point2D) = Point2D(
            ourStage.x * scaleFactor() + p.x * scaleFactor(),
            ourStage.y * scaleFactor() + p.y * scaleFactor())


    fun findSquare(square: Square) = translate(board.squareCenter(square))

    fun findSquare(name: String) = findSquare(square(name))

    fun move(from: String, to: String) {
        drag(findSquare(from))
        dropTo(findSquare(to))
    }

}

fun square(name: String) = Square.fromName(name)

fun scaleFactor() = when (Screen.getPrimary().dpi.toInt()) {
    in 0..96 -> 1.0
    in 97..120 -> 1.25
    in 121..144 -> 1.5
    else -> 2.0
}

