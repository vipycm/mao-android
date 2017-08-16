//
// Created by mao on 17-8-15.
//

#include <jni.h>

#ifdef __cplusplus
extern "C" {
#endif

#include <libavformat/avformat.h>

#define TAG_MEDIA_LIBRARY_JNI "MediaLibraryJni"

void avLogCallback(void *, int level, const char *fmt, va_list vl) {
    FILE *fp = fopen("/storage/emulated/0/av_log.txt", "a+");
    if (fp) {
        vfprintf(fp, fmt, vl);
        fflush(fp);
        fclose(fp);
    }
    /*
    switch (level) {
        case AV_LOG_TRACE:
        case AV_LOG_VERBOSE:
        case AV_LOG_DEBUG:
            LOGD(TAG_MEDIA_LIBRARY_JNI, fmt, vl);
            break;
        case AV_LOG_INFO:
            LOGI(TAG_MEDIA_LIBRARY_JNI, fmt, vl);
            break;
        case AV_LOG_WARNING:
            LOGW(TAG_MEDIA_LIBRARY_JNI, fmt, vl);
            break;
        case AV_LOG_ERROR:
            LOGE(TAG_MEDIA_LIBRARY_JNI, fmt, vl);
            break;
        default:
            LOGI(TAG_MEDIA_LIBRARY_JNI, fmt, vl);
            break;
    }*/
}

void initFFMpeg() {
    av_log_set_callback(avLogCallback);
    av_register_all();
    avformat_network_init();
}

JNIEXPORT void Java_com_vipycm_mao_media_MediaLibrary_nativeInit(JNIEnv *, jobject) {
    initFFMpeg();
}

#ifdef __cplusplus
}
#endif