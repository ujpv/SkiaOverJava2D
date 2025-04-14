cmake_minimum_required(VERSION 3.15)
project(Java2dCanvas)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)



set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR})

set(SKIA_DIR ${CMAKE_SOURCE_DIR}/skia)
include_directories(${SKIA_DIR}/include ${SKIA_DIR})

if (${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
    file(GLOB SKIA_LIBS ${SKIA_DIR}/out/**/*.a)
elseif (${CMAKE_SYSTEM_NAME} MATCHES "Windows")
    file(GLOB SKIA_LIBS ${SKIA_DIR}/out/**/*.lib)
    add_compile_definitions(_ITERATOR_DEBUG_LEVEL=0)
    set(CMAKE_MSVC_RUNTIME_LIBRARY "MultiThreaded")      # /MD
endif ()

find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})

add_library(java2d_canvas SHARED
        Java2dCanvas.cpp
        Java2dCanvas.h
        org_jetbrains_skiko_java2d_Java2dCanvas.h
        org_jetbrains_skiko_java2d_Java2dCanvas.cpp
)

if (${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
    find_library(COREFOUNDATION CoreFoundation)
    find_library(CORETEXT CoreText)
    find_library(COREGRAPHICS CoreGraphics)
    find_library(CARBON Carbon)

    target_link_libraries(java2d_canvas ${COREFOUNDATION} ${CORETEXT} ${COREGRAPHICS} ${CARBON})
endif ()

target_link_libraries(java2d_canvas ${SKIA_LIBS} ${JNI_LIBRARIES})

target_compile_definitions(java2d_canvas PUBLIC SK_RELEASE)
target_compile_options(java2d_canvas PUBLIC -std=c++17)

