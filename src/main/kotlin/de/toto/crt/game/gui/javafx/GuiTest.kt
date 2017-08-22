package de.toto.crt.game.gui.javafx

import javafx.application.Application
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.stage.Stage
import javafx.scene.paint.Color
import java.nio.file.Paths
import java.lang.System.gc
import javafx.scene.shape.ArcType
import com.sun.prism.impl.shape.BasicRoundRectRep.fillRoundRect



fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}


class App: Application() {

    override fun start(primaryStage: Stage?) {
        primaryStage?.title = "JavaFX-Tester"
        val canvas = Canvas(300.0, 250.0)
        val gc = canvas.getGraphicsContext2D();
        drawShapes(gc);
        val root = Group()
        root.children.add(canvas)
        primaryStage?.scene = Scene(root)
        primaryStage?.show()
    }

    private fun drawShapes(gc: GraphicsContext) {
        println("drawing")
        with (gc) {
            fill = Color.GREEN
            stroke = Color.BLUE
            setLineWidth(5.0)
            strokeLine(40.0, 10.0, 10.0, 40.0)
            fillOval(10.0, 60.0, 30.0, 30.0)
            strokeOval(60.0, 60.0, 30.0, 30.0)
            fillRoundRect(110.0, 60.0, 30.0, 30.0, 10.0, 10.0)
            strokeRoundRect(160.0, 60.0, 30.0, 30.0, 10.0, 10.0)
            fillArc(10.0, 110.0, 30.0, 30.0, 45.0, 240.0, ArcType.OPEN)
            fillArc(60.0, 110.0, 30.0, 30.0, 45.0, 240.0, ArcType.CHORD)
            fillArc(110.0, 110.0, 30.0, 30.0, 45.0, 240.0, ArcType.ROUND)
            fill = Color.YELLOW
            stroke = Color.RED
            strokeArc(10.0, 160.0, 30.0, 30.0, 45.0, 240.0, ArcType.OPEN)
            strokeArc(60.0, 160.0, 30.0, 30.0, 45.0, 240.0, ArcType.CHORD)
            strokeArc(110.0, 160.0, 30.0, 30.0, 45.0, 240.0, ArcType.ROUND)
            fillPolygon(doubleArrayOf(10.0, 40.0, 10.0, 40.0),
                    doubleArrayOf(210.0, 210.0, 240.0, 240.0), 4)
            strokePolygon(doubleArrayOf(60.0, 90.0, 60.0, 90.0),
                    doubleArrayOf(210.0, 210.0, 240.0, 240.0), 4)
            strokePolyline(doubleArrayOf(110.0, 140.0, 110.0, 140.0),
                    doubleArrayOf(210.0, 210.0, 240.0, 240.0), 4)
        }
    }
}

