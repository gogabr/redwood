cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_C_STANDARD 99)

# Header paths: Zipline native sources (sibling repo) + JDK JNI headers
set(ZIPLINE_NATIVE "${CMAKE_CURRENT_SOURCE_DIR}/../../../../../zipline/zipline/native")
include_directories(
    ${ZIPLINE_NATIVE}
    ${ZIPLINE_NATIVE}/include/share
    ${ZIPLINE_NATIVE}/include/unix
    ${ZIPLINE_NATIVE}/quickjs
)

# Generated bridge C files from redwood-protocol and redwood-protocol-host
set(BRIDGE_PROTO "${CMAKE_CURRENT_SOURCE_DIR}/../../../../redwood-protocol/build/generated/bridge/c")
set(BRIDGE_HOST  "${CMAKE_CURRENT_SOURCE_DIR}/../../../../redwood-protocol-host/build/generated/bridge/c")
set(BRIDGE_MERGED "${CMAKE_CURRENT_SOURCE_DIR}/../../../../redwood-protocol-host/build/generated/bridge/merged")

file(GLOB bridge_sources
    "${BRIDGE_PROTO}/app_cash_redwood_protocol_*.c"
    "${BRIDGE_HOST}/app_cash_redwood_protocol_host_*.c"
    "${BRIDGE_MERGED}/bridge_module_init.c"
)

# JNI wrapper for bridge_init_all()
set(BRIDGE_JNI "${CMAKE_CURRENT_SOURCE_DIR}/../../../../redwood-protocol-host/src/bridge/c/bridge_jni.c")

add_library(redwood-bridge SHARED
    ${bridge_sources}
    ${BRIDGE_JNI}
)

# QuickJS symbols are resolved at load time from libquickjs (loaded first by Zipline)
set_target_properties(redwood-bridge PROPERTIES
    LINK_FLAGS "-Wl,--allow-shlib-undefined"
)
