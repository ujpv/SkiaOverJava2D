#include "Java2dCanvas.h"

#include <iostream>

namespace {
    JavaVM *g_JavaVM = nullptr; // Global reference to the Java Virtual Machine
}

extern "C" JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    g_JavaVM = vm;
    return JNI_VERSION_1_8;
}

namespace {
    jintArray getXFromPoints(JNIEnv *env, const SkPoint pts[], size_t count) {
        jintArray result = env->NewIntArray(static_cast<jsize>(count));
        if (result == nullptr) {
            return nullptr;
        }

        jint *xCoordinates = new jint[count];
        for (size_t i = 0; i < count; ++i) {
            xCoordinates[i] = static_cast<jint>(pts[i].x());
        }

        env->SetIntArrayRegion(result, 0, static_cast<jsize>(count), xCoordinates);
        delete[] xCoordinates;

        return result;
    }

    jintArray getYFromPoints(JNIEnv *env, const SkPoint pts[], size_t count) {
        jintArray result = env->NewIntArray(static_cast<jsize>(count));
        if (result == nullptr) {
            return nullptr;
        }

        jint *xCoordinates = new jint[count];
        for (size_t i = 0; i < count; ++i) {
            xCoordinates[i] = static_cast<jint>(pts[i].y());
        }

        env->SetIntArrayRegion(result, 0, static_cast<jsize>(count), xCoordinates);
        delete[] xCoordinates;

        return result;
    }



}

Java2dCanvas::Java2dCanvas(JNIEnv *env, jobject canvas) : implRef(env->NewGlobalRef(canvas)) {
}

Java2dCanvas::~Java2dCanvas() {
    getEnv()->DeleteGlobalRef(implRef);
}

JNIEnv *Java2dCanvas::getEnv() {
    JNIEnv *env = nullptr;

    if (g_JavaVM->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_8) != JNI_OK) {
        std::cerr << "Failed to get the environment using GetEnv" << std::endl;
    }

    return env;
}

void Java2dCanvas::setColor(JNIEnv *env, SkColor skiaColor) const {
    env->CallVoidMethod(implRef, getEnv()->GetMethodID(env->GetObjectClass(implRef), "setColor", "(I)V"),
                        static_cast<jint>(skiaColor));
}

void Java2dCanvas::setStrokeWidth(JNIEnv *env, float width) const {
    env->CallVoidMethod(implRef, env->GetMethodID(env->GetObjectClass(implRef), "setStrokeWidth", "(F)V"),
                        static_cast<jfloat>(width));
}

void Java2dCanvas::setPaint(JNIEnv *env, const SkPaint &paint) const {
    setColor(env, paint.getColor());
    setStrokeWidth(env, paint.getStrokeWidth());
    enableAntialiasing(env, paint.isAntiAlias());
}

void Java2dCanvas::clear(JNIEnv *env) const {
    env->CallVoidMethod(implRef, env->GetMethodID(env->GetObjectClass(implRef), "clear", "()V"));
}

void Java2dCanvas::drawRect(JNIEnv *env, jint x, jint y, jint width, jint height) const {
    env->CallVoidMethod(
        implRef,
        env->GetMethodID(env->GetObjectClass(implRef), "drawRect", "(IIII)V"),
        x, y, width, height);
}

void Java2dCanvas::fillRect(JNIEnv *env, jint x, jint y, jint width, jint height) const {
    env->CallVoidMethod(
        implRef,
        env->GetMethodID(env->GetObjectClass(implRef), "fillRect", "(IIII)V"),
        x, y, width, height);
}

void Java2dCanvas::drawOval(JNIEnv *env, jint x, jint y, jint width, jint height) const {
    env->CallVoidMethod(
        implRef,
        env->GetMethodID(env->GetObjectClass(implRef), "drawOval", "(IIII)V"),
        x, y, width, height
    );
}

void Java2dCanvas::fillOval(JNIEnv *env, jint x, jint y, jint width, jint height) const {
    env->CallVoidMethod(
        implRef,
        env->GetMethodID(env->GetObjectClass(implRef), "fillOval", "(IIII)V"),
        x, y, width, height
    );
}

void Java2dCanvas::scale(JNIEnv *env, jfloat x, jfloat y) const {
    env->CallVoidMethod(
        implRef,
        env->GetMethodID(env->GetObjectClass(implRef), "scale", "(FF)V"),
        x, y
    );
}

void Java2dCanvas::translate(JNIEnv *env, jfloat x, jfloat y) const {
    env->CallVoidMethod(
        implRef,
        env->GetMethodID(env->GetObjectClass(implRef), "translate", "(FF)V"),
        x, y
    );
}

void Java2dCanvas::concat(JNIEnv *env, jdouble v0, jdouble v1, jdouble v2, jdouble v3, jdouble v4, jdouble v5) const {
    env->CallVoidMethod(
        implRef,
        env->GetMethodID(env->GetObjectClass(implRef), "concat", "(DDDDDD)V"),
        v0, v1, v2, v3, v4, v5
    );
}

void Java2dCanvas::drawPolyline(JNIEnv *env, jintArray xPoints, jintArray yPoints) const {
    env->CallVoidMethod(implRef, env->GetMethodID(env->GetObjectClass(implRef), "drawPolyline", "([I[I)V"), xPoints, yPoints);
}

void Java2dCanvas::drawLine(JNIEnv *env, jint x1, jint y1, jint x2, jint y2) const {
    env->CallVoidMethod(
        implRef,
        env->GetMethodID(env->GetObjectClass(implRef), "drawLine", "(IIII)V"),
        x1, y1, x2, y2
    );
}

void Java2dCanvas::enableAntialiasing(JNIEnv *env, bool enable) const {
    env->CallVoidMethod(
        implRef,
        env->GetMethodID(env->GetObjectClass(implRef), "enableAntialiasing", "(Z)V"),
        enable);
}

void Java2dCanvas::onDrawPaint(const SkPaint &paint) {
    setPaint(getEnv(), paint);
    clear(getEnv());
}

void Java2dCanvas::onDrawRect(const SkRect &rect, const SkPaint &paint) {
    JNIEnv *env = getEnv();
    setPaint(env, paint);
    switch (paint.getStyle()) {
        case SkPaint::kFill_Style:
            fillRect(env, static_cast<jint>(rect.fLeft), static_cast<jint>(rect.fTop), static_cast<jint>(rect.width()),
                     static_cast<jint>(rect.height()));
            break;
        case SkPaint::kStroke_Style:
            drawRect(env, static_cast<jint>(rect.fLeft), static_cast<jint>(rect.fTop), static_cast<jint>(rect.width()),
                     static_cast<jint>(rect.height()));
            break;
        case SkPaint::kStrokeAndFill_Style:
            fillRect(env, static_cast<jint>(rect.fLeft), static_cast<jint>(rect.fTop), static_cast<jint>(rect.width()),
                     static_cast<jint>(rect.height()));
            drawRect(env, static_cast<jint>(rect.fLeft), static_cast<jint>(rect.fTop), static_cast<jint>(rect.width()),
                     static_cast<jint>(rect.height()));
            break;
    }
}

void Java2dCanvas::onDrawRRect(const SkRRect &rrect, const SkPaint &paint) {
}

void Java2dCanvas::onDrawDRRect(const SkRRect &outer, const SkRRect &inner, const SkPaint &paint) {
}

void Java2dCanvas::onDrawOval(const SkRect &rect, const SkPaint &paint) {
    JNIEnv *env = getEnv();
    setPaint(env, paint);
    switch (paint.getStyle()) {
        case SkPaint::kFill_Style:
            fillOval(env, static_cast<jint>(rect.fLeft), static_cast<jint>(rect.fTop), static_cast<jint>(rect.width()),
                     static_cast<jint>(rect.height()));
            break;
        case SkPaint::kStroke_Style:
            drawOval(env, static_cast<jint>(rect.fLeft), static_cast<jint>(rect.fTop), static_cast<jint>(rect.width()),
                     static_cast<jint>(rect.height()));
            break;
        case SkPaint::kStrokeAndFill_Style:
            fillOval(env, static_cast<jint>(rect.fLeft), static_cast<jint>(rect.fTop), static_cast<jint>(rect.width()),
                     static_cast<jint>(rect.height()));
            drawOval(env, static_cast<jint>(rect.fLeft), static_cast<jint>(rect.fTop), static_cast<jint>(rect.width()),
                     static_cast<jint>(rect.height()));
            break;
    }
}

void Java2dCanvas::onDrawArc(const SkRect &rect, SkScalar startAngle, SkScalar sweepAngle, bool useCenter,
                             const SkPaint &paint) {
}

void Java2dCanvas::onDrawPath(const SkPath &path, const SkPaint &paint) {
}

void Java2dCanvas::onDrawRegion(const SkRegion &region, const SkPaint &paint) {
}

void Java2dCanvas::onDrawTextBlob(const SkTextBlob *blob, SkScalar x, SkScalar y, const SkPaint &paint) {
}

void Java2dCanvas::onDrawPatch(const SkPoint cubics[12], const SkColor colors[4], const SkPoint texCoords[4],
                               SkBlendMode mode, const SkPaint &paint) {
}

void Java2dCanvas::onDrawPoints(PointMode mode, size_t count, const SkPoint pts[], const SkPaint &paint) {
    JNIEnv *env = getEnv();
    setPaint(env, paint);
    switch (mode) {
        case kPoints_PointMode:
            for (size_t i = 0; i < count; i++) {
                const auto pt = pts[i];
                drawLine(env, static_cast<jint>(pt.x()), static_cast<jint>(pt.y()), static_cast<jint>(pt.x()),
                         static_cast<jint>(pt.y()));
            }
            break;
        case kLines_PointMode:
            for (size_t i = 0; i < count - 1; i += 2) {
                const auto pt1 = pts[i];
                const auto pt2 = pts[i + 1];
                drawLine(env, static_cast<jint>(pt1.x()), static_cast<jint>(pt1.y()), static_cast<jint>(pt2.x()),
                         static_cast<jint>(pt2.y()));
            }
            break;
        case kPolygon_PointMode:
            drawPolyline(env, getXFromPoints(env, pts, count), getYFromPoints(env, pts, count));
            break;
    }
}

void Java2dCanvas::onDrawEdgeAAQuad(const SkRect &rect, const SkPoint clip[4], SkCanvas::QuadAAFlags aaFlags,
                                    const SkColor4f &color, SkBlendMode mode) {
}

void Java2dCanvas::onDrawAnnotation(const SkRect &rect, const char key[], SkData *value) {
}

void Java2dCanvas::onDrawShadowRec(const SkPath &, const SkDrawShadowRec &) {
}

void Java2dCanvas::onDrawDrawable(SkDrawable *drawable, const SkMatrix *matrix) {
}

void Java2dCanvas::onDrawPicture(const SkPicture *picture, const SkMatrix *matrix, const SkPaint *paint) {
}

void Java2dCanvas::didTranslate(SkScalar x, SkScalar y) {
    translate(getEnv(), x, y);
}

void Java2dCanvas::didScale(SkScalar x, SkScalar y) {
    SkCanvasVirtualEnforcer<SkCanvas>::didScale(x, y);
    scale(getEnv(), x, y);
}

void Java2dCanvas::didConcat44(const SkM44 &sk_m44) {
    jdouble v0 = sk_m44.rc(0, 0);
    jdouble v1 = sk_m44.rc(1, 0);
    jdouble v2 = sk_m44.rc(0, 1);
    jdouble v3 = sk_m44.rc(1, 1);
    jdouble v4 = sk_m44.rc(0, 3);
    jdouble v5 = sk_m44.rc(1, 3);
    concat(getEnv(), v0, v1, v2, v3, v4, v5);
}
