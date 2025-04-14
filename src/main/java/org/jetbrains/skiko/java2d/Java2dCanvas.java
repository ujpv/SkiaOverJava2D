package org.jetbrains.skiko.java2d;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Java2dCanvas {
    private final Graphics2D g2d;

    static  {
        System.loadLibrary("java2d_canvas");
    }

    private Java2dCanvas(Graphics2D g2d) {
        this.g2d = g2d;
    }

    public static org.jetbrains.skia.Canvas create(Graphics2D g2d) {
        return new org.jetbrains.skia.Canvas(new Java2dCanvas(g2d).createCanvasImpl(), false, "") ;
    }

    private native long createCanvasImpl();

    private static Color convertSkColor(int skiaColor) {
        int alpha = (skiaColor >> 24) & 0xFF;
        int red = (skiaColor >> 16) & 0xFF;
        int green = (skiaColor >> 8) & 0xFF;
        int blue = skiaColor & 0xFF;

        return new Color(red, green, blue, alpha);
    }

    public void clear() {
        g2d.fillRect(0, 0, g2d.getClipBounds().width, g2d.getClipBounds().height);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        g2d.drawLine(x1, y1, x2, y2);
    }

    public void drawPolyline(int[] x, int[] y) {
        g2d.drawPolyline(x, y, x.length);
    }

    public void drawPath(org.jetbrains.skia.Path path) {
    }

    public void drawRect(int x, int y, int width, int height) {
        g2d.drawRect(x, y, width, height);
    }

    public void fillRect(int x, int y, int width, int height) {
        g2d.fillRect(x, y, width, height);
    }

    public void drawOval(int x, int y, int width, int height) {
        g2d.drawOval(x, y, width, height);
    }

    public void fillOval(int x, int y, int width, int height) {
        g2d.fillOval(x, y, width, height);
    }

    public void enableAntialiasing(boolean enable) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                enable ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
    }

    public void setColor(int skiaColor) {
        g2d.setColor(convertSkColor(skiaColor));
    }

    public void setStrokeWidth(float width) {
        g2d.setStroke(new BasicStroke(width));
    }

    public void scale(float sx, float sy) {
        g2d.scale(sx, sy);
    }

    public void translate(float tx, float ty) {
        g2d.translate(tx, ty);
    }

    public void concat(double v0, double v1, double v2, double v3,
                       double v4, double v5) {
        AffineTransform at = new AffineTransform(v0, v1, v2, v3, v4, v5);
        at.concatenate(g2d.getTransform());
        g2d.setTransform(at);
    }
}
