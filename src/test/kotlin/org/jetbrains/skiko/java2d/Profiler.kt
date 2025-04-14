package org.jetbrains.skiko.java2d

import java.awt.BorderLayout
import java.awt.Graphics
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class Profiler(private val name: String, child: JComponent) : JPanel() {
    private var start = 0L
    private var count = 0L
    private var totalTime = 0L
    private var lastReported = 0L

    private val infoLabel = JLabel(name)

    init {
        layout = BorderLayout()
        add(child, BorderLayout.CENTER)
        add(infoLabel, BorderLayout.NORTH)
    }

    fun beginFrame() {
        start = System.nanoTime()
    }

    fun endFrame() {
        val now = System.nanoTime()
        totalTime += now - start
        count++
        if (now - lastReported > 1_000_000_000 && count > 0) {
            lastReported = now
            infoLabel.text = "$name: %.2f ms/frame".format(totalTime.toDouble() / count / 1_000_000)
            totalTime = 0
            count = 0
        }
    }

    override fun paintChildren(g: Graphics) {
        beginFrame()
        super.paintChildren(g)
        endFrame()
        repaint()
    }
}