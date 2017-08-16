# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             # Associated headers in the same location as their source
             # file are automatically included.
             src/main/cpp/native-lib.cpp )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

find_library(z-lib  z)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )

# add yuv lib
add_library(yuv-decoder SHARED src/main/cpp/yuv-decoder.c)

set(FFMPEG_PATH ${CMAKE_CURRENT_LIST_DIR}/src/main/cpp/ffmpeg)

# add my media lib
add_library(mao-media SHARED
    src/main/cpp/util/JniDataType.cpp
    src/main/cpp/media/MediaLibraryJni.cpp
    src/main/cpp/media/AudioDecoderJni.cpp
    src/main/cpp/media/AudioDecoder.cpp
    src/main/cpp/mao-ffmpeg.cpp)

include_directories(${FFMPEG_PATH}/include)

target_link_libraries(mao-media
    ${FFMPEG_PATH}/prebuilt/libavformat.a
    ${FFMPEG_PATH}/prebuilt/libavcodec.a
    ${FFMPEG_PATH}/prebuilt/libavdevice.a
    ${FFMPEG_PATH}/prebuilt/libavfilter.a
    ${FFMPEG_PATH}/prebuilt/libavutil.a
    ${FFMPEG_PATH}/prebuilt/libpostproc.a
    ${FFMPEG_PATH}/prebuilt/libswresample.a
    ${FFMPEG_PATH}/prebuilt/libswscale.a
    ${FFMPEG_PATH}/prebuilt/libfdk-aac.a
    ${FFMPEG_PATH}/prebuilt/libmp3lame.a
    ${FFMPEG_PATH}/prebuilt/libx264.a
    ${z-lib}
    ${log-lib})