#include <jni.h>
#include "quickjs/quickjs.h"
#include "bridge_dispatch.h"

JNIEXPORT void JNICALL
Java_app_cash_zipline_QuickJs_bridgeInitAllNative(JNIEnv* env, jclass clazz, jlong jsContext) {
    if (jsContext) {
        bridge_init_all((JSContext*)jsContext);
    }
}
