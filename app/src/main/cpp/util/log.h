#ifndef JUNKUTILS_LOG_H
#define JUNKUTILS_LOG_H

#include <android/log.h>

#define LOG_LEVEL_DEBUG 1
#define LOG_LEVEL_INFO  2
#define LOG_LEVEL_WARN  3
#define LOG_LEVEL_ERROR 4
#define LOG_LEVEL LOG_LEVEL_DEBUG

#if LOG_LEVEL <= LOG_LEVEL_DEBUG
    #define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,  __VA_ARGS__)
#else
    #define LOGD(...)
#endif

#if LOG_LEVEL <= LOG_LEVEL_INFO
    #define LOGI(...)  __android_log_print(ANDROID_LOG_INFO,  __VA_ARGS__)
#else
    #define LOGI(...)
#endif

#if LOG_LEVEL <= LOG_LEVEL_WARN
    #define LOGW(...)  __android_log_print(ANDROID_LOG_WARN,  __VA_ARGS__)
#else
    #define LOGW(...)
#endif

#if LOG_LEVEL <= LOG_LEVEL_ERROR
    #define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,  __VA_ARGS__)
#else
    #define LOGE(...)
#endif

#endif
