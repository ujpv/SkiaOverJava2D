#include "org_jetbrains_skiko_java2d_Java2dCanvas.h"
#include "Java2dCanvas.h"

extern "C" jlong Java_org_jetbrains_skiko_java2d_Java2dCanvas_createCanvasImpl(JNIEnv *env, jobject self) {
    return reinterpret_cast<jlong>(new Java2dCanvas(env, self));
}
