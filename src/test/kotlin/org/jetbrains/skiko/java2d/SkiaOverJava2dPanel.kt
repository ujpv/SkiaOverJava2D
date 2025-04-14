package org.jetbrains.skiko.java2d

import org.jetbrains.skiko.SkikoRenderDelegate
import java.awt.GraphicsEnvironment
import javax.swing.JPanel

class SkiaOverJava2dPanel(val renderDelegate: SkikoRenderDelegate) : JPanel() {
    val deviceScaleFactor = 1 / GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration.defaultTransform.scaleX
    override fun paint(g: java.awt.Graphics) {
        val g2g = g as java.awt.Graphics2D
        g2g.scale(deviceScaleFactor, deviceScaleFactor)
        val canvas = Java2dCanvas.create(g2g)
        renderDelegate.onRender(canvas, g2g.clipBounds.width, g2g.clipBounds.height, System.nanoTime())
        canvas.close()
    }
}