//
// Created by mao on 17-4-26.
//
#ifndef MAO_ANDROID_MAO_FFMPEG_H
#define MAO_ANDROID_MAO_FFMPEG_H

#include <stdarg.h>

#ifdef __cplusplus
extern "C" {
#endif

void av_log_callback(void *ptr, int level, const char *fmt, va_list vl);

void init_ffmpeg();

#ifdef __cplusplus
}
#endif
#endif //MAO_ANDROID_MAO_FFMPEG_H
