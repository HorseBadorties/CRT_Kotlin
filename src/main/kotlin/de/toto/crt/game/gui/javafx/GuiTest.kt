package de.toto.crt.game.gui.javafx

import com.kitfox.svg.*
import com.kitfox.svg.app.beans.SVGIcon
import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.scene.web.WebEngine
import javafx.scene.web.WebView
import java.awt.Dimension
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.net.URL


fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}


class App: Application() {

    private fun loadImage(url: String, size: Int): Image {
        val result = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val svgUniverse = SVGUniverse()
        val svgIcon = SVGIcon()
        svgIcon.setSvgURI(svgUniverse.loadSVG(javaClass.getResource(url)))
        svgIcon.isScaleToFit = true
        svgIcon.antiAlias = true
        svgIcon.preferredSize = Dimension(size, size)
        val ig2 = result.createGraphics()
        ig2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        ig2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        svgIcon.paintIcon(null, ig2, 0, 0)
        ig2.dispose()
        return SwingFXUtils.toFXImage(result, null)
    }

    override fun start(primaryStage: Stage?) {
        primaryStage?.title = "JavaFX-Tester"
        val canvas = Canvas(300.0, 300.0)
        val gc = canvas.getGraphicsContext2D();
        drawShapes(gc);
        val root = Group()
        root.children.add(canvas)
        primaryStage?.scene = Scene(root)
        primaryStage?.show()
    }

    private fun drawShapes(gc: GraphicsContext) {
        with (gc) {




            drawImage(loadImage("/images/pieces/merida/wK.svg", 300), 0.0, 0.0)
//            fill = Color.GREEN
//            stroke = Color.BLUE
//            setLineWidth(5.0)
//            strokeLine(40.0, 10.0, 10.0, 40.0)
//            fillOval(10.0, 60.0, 30.0, 30.0)
//            strokeOval(60.0, 60.0, 30.0, 30.0)
//            fillRoundRect(110.0, 60.0, 30.0, 30.0, 10.0, 10.0)
//            strokeRoundRect(160.0, 60.0, 30.0, 30.0, 10.0, 10.0)
//            fillArc(10.0, 110.0, 30.0, 30.0, 45.0, 240.0, ArcType.OPEN)
//            fillArc(60.0, 110.0, 30.0, 30.0, 45.0, 240.0, ArcType.CHORD)
//            fillArc(110.0, 110.0, 30.0, 30.0, 45.0, 240.0, ArcType.ROUND)
//            fill = Color.YELLOW
//            stroke = Color.RED
//            strokeArc(10.0, 160.0, 30.0, 30.0, 45.0, 240.0, ArcType.OPEN)
//            strokeArc(60.0, 160.0, 30.0, 30.0, 45.0, 240.0, ArcType.CHORD)
//            strokeArc(110.0, 160.0, 30.0, 30.0, 45.0, 240.0, ArcType.ROUND)
//            fillPolygon(doubleArrayOf(10.0, 40.0, 10.0, 40.0),
//                    doubleArrayOf(210.0, 210.0, 240.0, 240.0), 4)
//            strokePolygon(doubleArrayOf(60.0, 90.0, 60.0, 90.0),
//                    doubleArrayOf(210.0, 210.0, 240.0, 240.0), 4)
//            strokePolyline(doubleArrayOf(110.0, 140.0, 110.0, 140.0),
//                    doubleArrayOf(210.0, 210.0, 240.0, 240.0), 4)
        }
    }
}

