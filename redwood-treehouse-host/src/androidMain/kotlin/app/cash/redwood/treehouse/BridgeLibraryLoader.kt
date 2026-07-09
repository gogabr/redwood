package app.cash.redwood.treehouse

import android.util.Log

/**
 * Loads the bridge native library. Called once before any Zipline operations.
 *
 * On Android, the library is packaged into the APK via [externalNativeBuild]
 * (see [CMakeLists.txt](src/androidMain/cpp/CMakeLists.txt)).
 * It must be loaded after [libquickjs] (loaded by Zipline's QuickJs) so QuickJS
 * symbols are available for resolution.
 */
internal object BridgeLibraryLoader {
  private var loaded = false

  fun ensureLoaded() {
    if (loaded) return
    try {
      System.loadLibrary("redwood-bridge")
      loaded = true
    } catch (e: UnsatisfiedLinkError) {
      Log.w("BridgeLibraryLoader", "libredwood-bridge not found — bridge dispatch unavailable", e)
    }
  }
}
