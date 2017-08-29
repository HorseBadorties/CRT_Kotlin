package de.toto.crt.game.gui.javafx

import de.toto.crt.game.rules.Square
import javafx.geometry.Point2D
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.stage.Stage
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxToolkit
import org.testfx.framework.junit.ApplicationTest
import org.testfx.util.WaitForAsyncUtils

class GuiTest: ApplicationTest() {

    private lateinit var ourStage: Stage

    @Before
    fun setupClass() {
        ApplicationTest.launch(App::class.java)
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
        val board: ChessBoard = lookup<ChessBoard> { it is ChessBoard }.query()

        press(KeyCode.RIGHT)
        WaitForAsyncUtils.waitForFxEvents()
        press(KeyCode.LEFT)
        sleep(2000)
        drag(translate(board.squareCenter(Square(2, 4))))
        moveTo(translate(board.squareCenter(Square(4, 4))))
        sleep(2000)
        drop()
        sleep(2000)
    }

    fun translate(p: Point2D) = Point2D(ourStage.x*2 + p.x*2, ourStage.y*2 + p.y*2)

}