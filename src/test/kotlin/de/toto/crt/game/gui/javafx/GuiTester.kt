package de.toto.crt.game.gui.javafx

import com.sun.javafx.tk.Toolkit
import de.toto.crt.game.rules.Square
import javafx.geometry.Point2D
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.stage.Screen
import javafx.stage.Stage
import org.junit.Assert.*
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
        assertEquals(board.position.move, "d4")
        press(KeyCode.LEFT)
        assertEquals(board.position.move, "")
        move("d2", "d4")
        assertEquals(board.position.move, "d4")
        move("g8", "f6")
        assertEquals(board.position.move, "Nf6")
        drag(findSquare("c2"))
        dropTo(findSquare("c4"))
        assertEquals(board.position.move, "c4")
        clickOn(findSquare("a1"))
        clickOn(findSquare("a8"))
        clickOn(findSquare("h1"))
        clickOn(findSquare("h8"))
    }

    private fun translate(p: Point2D): Point2D {
        val scaleFactor = Toolkit.getToolkit().screenConfigurationAccessor.getRenderScale(Toolkit.getToolkit().primaryScreen)
        return Point2D(
                ourStage.x * scaleFactor + p.x * scaleFactor,
                ourStage.y * scaleFactor + p.y * scaleFactor)
    }


    private fun findSquare(square: Square) = translate(board.squareCenter(square))

    private fun findSquare(name: String) = findSquare(square(name))

    private fun move(from: String, to: String) {
        drag(findSquare(from))
        dropTo(findSquare(to))
    }



}

fun square(name: String) = Square.fromName(name)



