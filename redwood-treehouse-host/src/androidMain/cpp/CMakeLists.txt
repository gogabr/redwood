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
set(BRIDGE_RUNTIME "${CMAKE_CURRENT_SOURCE_DIR}/../../../../redwood-runtime/build/generated/bridge/c")
set(BRIDGE_MERGED "${CMAKE_CURRENT_SOURCE_DIR}/../../../../redwood-protocol-host/build/generated/bridge/merged")
# Compose-live protocol-host modules also generate bridge C code.
# Redwood is at ${CMAKE_CURRENT_SOURCE_DIR}/../../../.. (4 levels up from cpp).
# Compose-live is a sibling of redwood: one more level up + compose-live.
# Only ru.wildberries.* modules are used; io.composelive.standard.* is a separate variant.
set(BRIDGE_CL_WB "${CMAKE_CURRENT_SOURCE_DIR}/../../../../../compose-live/wb/nodes/main/generated/protocol-host/build/generated/bridge/c")
set(BRIDGE_CL_COMMON "${CMAKE_CURRENT_SOURCE_DIR}/../../../../../compose-live/core/nodes/foundation/common/build/generated/bridge/c")

file(GLOB bridge_sources
    "${BRIDGE_PROTO}/app_cash_redwood_protocol_*.c"
    "${BRIDGE_HOST}/app_cash_redwood_protocol_host_*.c"
    "${BRIDGE_RUNTIME}/app_cash_redwood_*.c"
    "${BRIDGE_CL_WB}/ru_wildberries_composelive_nodes_main_protocol_host_foundation_*.c"
    "${BRIDGE_CL_COMMON}/io_composelive_nodes_foundation_common_*.c"
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
