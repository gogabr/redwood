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
      val oldPolicy = android.os.StrictMode.allowThreadDiskReads()
      // Load libquickjs first so its symbols are available for libredwood-bridge
      try { System.loadLibrary("quickjs") } catch (_: UnsatisfiedLinkError) {}
      System.loadLibrary("redwood-bridge")
      android.os.StrictMode.setThreadPolicy(oldPolicy)
      loaded = true
      Log.i("BridgeLibraryLoader", "libredwood-bridge loaded successfully")
    } catch (e: UnsatisfiedLinkError) {
      Log.w("BridgeLibraryLoader", "libredwood-bridge not found — bridge dispatch unavailable", e)
    }
  }
}
