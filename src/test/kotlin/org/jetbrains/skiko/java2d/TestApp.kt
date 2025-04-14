package org.jetbrains.skiko.java2d

import org.jetbrains.skia.Canvas
import org.jetbrains.skia.Color
import org.jetbrains.skia.Paint
import org.jetbrains.skia.PaintMode
import org.jetbrains.skia.PaintStrokeCap
import org.jetbrains.skia.PaintStrokeJoin
import org.jetbrains.skia.Path
import org.jetbrains.skia.Point
import org.jetbrains.skia.Rect
import org.jetbrains.skiko.SkikoRenderDelegate
import org.jetbrains.skiko.swing.SkiaSwingLayer
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Dimension
import java.awt.event.ComponentEvent
import javax.swing.*
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

val testCases: List<Pair<String, SkikoRenderDelegate>> = listOf(
    Pair("Simple", object : SkikoRenderDelegate {
        override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
            canvas.clear(0xFF00FFFF.toInt())

            val paint = Paint().apply {
                color = 0xFFFF0000.toInt()
            }
            canvas.drawRect(Rect.makeXYWH(100f, 100f, 200f, 150f), paint)

            paint.color = 0xFF00FF00.toInt()
            canvas.drawCircle(400f, 300f, 50f, paint)
        }
    }),

    Pair("Line with skew", object : SkikoRenderDelegate {
        override fun onRender(
            canvas: Canvas,
            width: Int,
            height: Int,
            nanoTime: Long
        ) {
            val paint = Paint().apply {
                color = 0xFF9a67be.toInt()
                isAntiAlias = true
                strokeWidth = 20f

            }
            canvas.skew(1.0f, 0.0f)
            canvas.drawLine(32f, 96f, 32f, 160f, paint)
            canvas.skew(-2.0f, 0.0f)
            canvas.drawLine(288f, 96f, 288f, 160f, paint)
        }
    }),

    Pair("Points", object : SkikoRenderDelegate {
        override fun onRender(
            canvas: Canvas,
            width: Int,
            height: Int,
            nanoTime: Long
        ) {
            // https://fiddle.skia.org/c/@Canvas_drawPoints
            canvas.scale(3f, 3f)
            val paint = Paint().apply {
                isAntiAlias = true
                strokeWidth = 10f
                color = 0x80349a45.toInt()
                mode = PaintMode.STROKE
            }
            val points = arrayOf<Point>(Point(32f, 16f), Point(48f, 48f), Point(16f, 32f))
            val join = listOf<PaintStrokeJoin>(PaintStrokeJoin.ROUND, PaintStrokeJoin.MITER, PaintStrokeJoin.BEVEL)
            val path = Path().apply {
                addPoly(points, true)
            }
            var joinIndex = 0
            for (cap in arrayOf(PaintStrokeCap.ROUND, PaintStrokeCap.SQUARE, PaintStrokeCap.BUTT)) {
                paint.strokeCap = cap
                paint.strokeJoin = join[joinIndex++]
                canvas.drawPoints(points, paint)
                canvas.translate(64f, 0f)
                canvas.drawLines(points, paint)
                canvas.translate(64f, 0f)
                canvas.drawPolygon(points, paint)
                canvas.translate(64f, 0f)
                canvas.drawPath(path, paint)
                canvas.translate(-192f, 64f)
            }
        }
    }),

    Pair("ClockAwt", object : SkikoRenderDelegate {
        override fun onRender(
            canvas: Canvas,
            width: Int,
            height: Int,
            nanoTime: Long
        ) {
            val watchFill = Paint().apply {
                color = 0xFFFFFFFF.toInt()
            }
            val watchStroke = Paint().apply {
                color = Color.RED
                mode = PaintMode.STROKE
                strokeWidth = 1f
            }
            val watchStrokeAA = Paint().apply {
                color = 0xFF000000.toInt()
                mode = PaintMode.STROKE
                strokeWidth = 1f
            }
            for (x in 0 .. (width - 50) step 50) {
                for (y in 20 .. (height - 50) step 50) {
                    val stroke = if (x > width / 2) watchStrokeAA else watchStroke
                canvas.drawOval(Rect.makeXYWH(x + 5f, y + 5f, 40f, 40f), watchFill)
                canvas.drawOval(Rect.makeXYWH(x + 5f, y + 5f, 40f, 40f), stroke)
                    var angle = 0f
                    while (angle < 2f * PI) {
                        canvas.drawLine(
                            (x + 25 - 17 * sin(angle)),
                            (y + 25 + 17 * cos(angle)),
                            (x + 25 - 20 * sin(angle)),
                            (y + 25 + 20 * cos(angle)),
                            stroke
                        )
                        angle += (2.0 * PI / 12.0).toFloat()
                    }
                    val time = (nanoTime / 1E6) % 60000 +
                            (x.toFloat() / width * 5000).toLong() +
                            (y.toFloat() / width * 5000).toLong()

                    val angle1 = (time.toFloat() / 5000 * 2f * PI).toFloat()
                    canvas.drawLine(x + 25f, y + 25f,
                        x + 25f - 15f * sin(angle1),
                        y + 25f + 15 * cos(angle1),
                        stroke)

                    val angle2 = (time / 60000 * 2f * PI).toFloat()
                    canvas.drawLine(x + 25f, y + 25f,
                        x + 25f - 10f * sin(angle2),
                        y + 25f + 10f * cos(angle2),
                        stroke)
                }
            }
        }
    })
)

fun main() {
    SwingUtilities.invokeLater {
        JFrame().apply {
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            setSize(1000, 600)
            val list = JList<String>(testCases.map { it.first }.toTypedArray())
            list.selectionMode = ListSelectionModel.SINGLE_SELECTION
            list.selectedIndex = 0
            val scrollPane = JScrollPane(list).apply {
                preferredSize = Dimension(200, 0)
            }

            val contentPane = JPanel(CardLayout())

            for (case in testCases) {
                contentPane.add(createSplitPane(case.second), case.first)
            }

            val cardLayout = contentPane.getLayout() as CardLayout
            list.addListSelectionListener(ListSelectionListener { e: ListSelectionEvent? ->
                val selected = list.getSelectedValue()
                cardLayout.show(contentPane, selected)
            })

            val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, contentPane)
            splitPane.setDividerLocation(200)
            splitPane.setDividerSize(3)
            add(splitPane)

            isVisible = true
        }
    }
}

fun createSplitPane(renderDelegate: SkikoRenderDelegate): JPanel {
    val leftPanel = Profiler("Skiko backend", SkiaSwingLayer(renderDelegate)).apply {
        border = BorderFactory.createLineBorder(java.awt.Color.BLACK, 2)
    }
    val rightPanel = Profiler("Java2D backend", SkiaOverJava2dPanel(renderDelegate)).apply {
        border = BorderFactory.createLineBorder(java.awt.Color.BLACK, 2)
    }
    val splitPane = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel).apply {
        dividerLocation = 400
        resizeWeight = 0.5
        isEnabled = false
        dividerSize = 1
        addComponentListener(object : java.awt.event.ComponentAdapter() {
            override fun componentResized(e: ComponentEvent?) {
                dividerLocation = width / 2
            }
        })
    }

    return JPanel().apply {
        layout = BorderLayout()
        add(splitPane, BorderLayout.CENTER)
    }
}

