#include <jni.h>
#include "quickjs/quickjs.h"
#include "bridge_dispatch.h"

extern void init_all(JNIEnv *env);
extern void register_all(JSContext *ctx);

JNIEXPORT void JNICALL
Java_app_cash_zipline_QuickJs_bridgeInitAllNative(JNIEnv* env, jclass clazz, jlong jsContext) {
    // Cache JNI class/method references from the main thread before QuickJS starts.
    init_all(env);
    if (jsContext) {
        register_all((JSContext*)jsContext);
    }
}
