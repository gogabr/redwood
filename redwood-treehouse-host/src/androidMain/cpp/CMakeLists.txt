# Bridge native library for Redwood JNI dispatch (v4)
cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_C_STANDARD 99)

# Header paths: Zipline native sources (sibling repo)
set(ZIPLINE_NATIVE "${CMAKE_CURRENT_SOURCE_DIR}/../../../../../zipline/zipline/native")
include_directories(
    ${ZIPLINE_NATIVE}
    ${ZIPLINE_NATIVE}/include/share
    ${ZIPLINE_NATIVE}/include/unix
    ${ZIPLINE_NATIVE}/quickjs
)

# Generated bridge C files
set(BRIDGE_PROTO "${CMAKE_CURRENT_SOURCE_DIR}/../../../../redwood-protocol/build/generated/bridge/c")
set(BRIDGE_HOST  "${CMAKE_CURRENT_SOURCE_DIR}/../../../../redwood-protocol-host/build/generated/bridge/c")
set(BRIDGE_MERGED "${CMAKE_CURRENT_SOURCE_DIR}/../../../../redwood-protocol-host/build/generated/bridge/merged")

file(GLOB bridge_sources
    "${BRIDGE_PROTO}/app_cash_redwood_protocol_*.c"
    "${BRIDGE_HOST}/app_cash_redwood_protocol_host_*.c"
    "${BRIDGE_MERGED}/bridge_module_init.c"
)

set(BRIDGE_JNI "${CMAKE_CURRENT_SOURCE_DIR}/../../../../redwood-protocol-host/src/bridge/c/bridge_jni.c")

add_library(redwood-bridge SHARED
    ${bridge_sources}
    ${BRIDGE_JNI}
)

# Link against libquickjs via IMPORTED library.
# QUICKJS_JNI_DIR is passed from Gradle. ANDROID_ABI is set by the NDK CMake toolchain.
if(QUICKJS_JNI_DIR)
    set(QUICKJS_LIB "${QUICKJS_JNI_DIR}/${ANDROID_ABI}/libquickjs.so")
    if(EXISTS "${QUICKJS_LIB}")
        add_library(quickjs SHARED IMPORTED)
        set_target_properties(quickjs PROPERTIES IMPORTED_LOCATION "${QUICKJS_LIB}")
        target_link_libraries(redwood-bridge quickjs log)
        message(STATUS "Bridge: linked against ${QUICKJS_LIB}")
    else()
        message(WARNING "Bridge: ${QUICKJS_LIB} not found")
        target_link_libraries(redwood-bridge log)
    endif()
else()
    message(WARNING "Bridge: QUICKJS_JNI_DIR not set")
    target_link_libraries(redwood-bridge log)
endif()
