#ifndef JAVA_2D_CANVAS_H
#define JAVA_2D_CANVAS_H

#include <core/SkCanvas.h>
#include <core/SkPaint.h>
#include <core/SkPoint.h>
#include <core/SkColor.h>
#include <core/SkScalar.h>
#include <core/SkCanvasVirtualEnforcer.h>

#include <jni.h>

class Java2dCanvas : public SkCanvasVirtualEnforcer<SkCanvas> {
public:
    Java2dCanvas(JNIEnv *env, jobject canvas);

    ~Java2dCanvas() override;

private:
    static JNIEnv *getEnv();

    void setColor(JNIEnv *env, SkColor skiaColor) const;

    void setStrokeWidth(JNIEnv *env, float width) const;

    void enableAntialiasing(JNIEnv *env, bool enable) const;

    void setPaint(JNIEnv *env, const SkPaint &paint) const;

    void clear(JNIEnv *env) const;

    void drawLine(JNIEnv *env, jint x1, jint y1, jint x2, jint y2) const;

    void drawRect(JNIEnv *env, jint x, jint y, jint width, jint height) const;

    void fillRect(JNIEnv *env, jint x, jint y, jint width, jint height) const;

    void drawOval(JNIEnv *env, jint x, jint y, jint width, jint height) const;

    void fillOval(JNIEnv *env, jint x, jint y, jint width, jint height) const;

    void scale(JNIEnv *env, jfloat x, jfloat y) const;

    void translate(JNIEnv *env, jfloat x, jfloat y) const;

    void concat(JNIEnv *env, jdouble v0, jdouble v1, jdouble v2, jdouble v3, jdouble v4, jdouble v5) const;

    void drawPolyline(JNIEnv *env, jintArray xPoints, jintArray yPoints) const;

    jobject implRef;

protected:
    void onDrawPaint(const SkPaint &paint) override;

    void onDrawRect(const SkRect &rect, const SkPaint &paint) override;

    void onDrawRRect(const SkRRect &rrect, const SkPaint &paint) override;

    void onDrawDRRect(const SkRRect &outer, const SkRRect &inner, const SkPaint &paint) override;

    void onDrawOval(const SkRect &rect, const SkPaint &paint) override;

    void onDrawArc(const SkRect &rect, SkScalar startAngle, SkScalar sweepAngle, bool useCenter,
                   const SkPaint &paint) override;

    void onDrawPath(const SkPath &path, const SkPaint &paint) override;

    void onDrawRegion(const SkRegion &region, const SkPaint &paint) override;

    void onDrawTextBlob(const SkTextBlob *blob, SkScalar x, SkScalar y, const SkPaint &paint) override;

    void onDrawPatch(const SkPoint cubics[12], const SkColor colors[4], const SkPoint texCoords[4], SkBlendMode mode,
                     const SkPaint &paint) override;

    void onDrawPoints(PointMode mode, size_t count, const SkPoint pts[], const SkPaint &paint) override;

    void onDrawEdgeAAQuad(const SkRect &rect, const SkPoint clip[4], SkCanvas::QuadAAFlags aaFlags,
                          const SkColor4f &color, SkBlendMode mode) override;

    void onDrawAnnotation(const SkRect &rect, const char key[], SkData *value) override;

    void onDrawShadowRec(const SkPath &, const SkDrawShadowRec &) override;

    void onDrawDrawable(SkDrawable *drawable, const SkMatrix *matrix) override;

    void onDrawPicture(const SkPicture *picture, const SkMatrix *matrix, const SkPaint *paint) override;

    void didTranslate(SkScalar x, SkScalar y) override;

    void didScale(SkScalar x, SkScalar y) override;

    void didConcat44(const SkM44 &) override;
};

#endif //JAVA_2D_CANVAS_H
