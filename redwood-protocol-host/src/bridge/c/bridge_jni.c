#include <jni.h>
#include "quickjs/quickjs.h"
#include "bridge_dispatch.h"

extern void register_all(JSContext *ctx);

JNIEXPORT void JNICALL
Java_app_cash_zipline_QuickJs_bridgeInitAllNative(JNIEnv* env, jclass clazz, jlong jsContext) {
    if (jsContext) {
        register_all((JSContext*)jsContext);
    }
}
