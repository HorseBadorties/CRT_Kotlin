package de.toto.crt.game.gui.javafx

import com.kitfox.svg.SVGUniverse
import com.kitfox.svg.app.beans.SVGIcon
import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Group
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.stage.Stage
import java.awt.Dimension
import java.awt.RenderingHints
import java.awt.image.BufferedImage

class SVGSalamanderTest: Application() {

    val size = 150
    val image = "/images/pieces/merida/wQ.svg"

    override fun start(stage: Stage?) {
        stage?.title = "SVGSalamanderTest-JavaFX"
        val canvas = Canvas(size.toDouble(), size.toDouble())
        val root = Group()
        val svgUniverse = SVGUniverse()
        val svgIcon = SVGIcon()
        svgIcon.svgURI = svgUniverse.loadSVG(javaClass.getResource(image))
        val bufferedAWTImage = BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB)
        val awtGraphics = bufferedAWTImage.createGraphics()
        awtGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        awtGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        svgIcon.isScaleToFit = true
        svgIcon.antiAlias = true
        svgIcon.preferredSize = Dimension(size, size)
        svgIcon.paintIcon(null, awtGraphics, 0, 0)
        awtGraphics.dispose()
        val gc = canvas.graphicsContext2D
        gc.drawImage(SwingFXUtils.toFXImage(bufferedAWTImage, null), 0.0, 0.0)
        root.children.add(canvas)
        stage?.scene = Scene(root)
        stage?.show()
    }
}

fun main(args: Array<String>) {
    Application.launch(SVGSalamanderTest::class.java, *args)
}