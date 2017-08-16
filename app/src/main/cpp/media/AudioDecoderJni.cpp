//
// Created by mao on 17-8-15.
//

#include <jni.h>
#include <string>
#include "../util/JniDataType.h"

#ifdef __cplusplus
extern "C" {
#endif

#include "AudioDecoder.h"


JNIEXPORT long Java_com_vipycm_mao_media_AudioDecoder_nativeCreateAudioDecoder(JNIEnv *, jobject) {
    AudioDecoder *decoder = new AudioDecoder();
    return (long) decoder;
}

JNIEXPORT void Java_com_vipycm_mao_media_AudioDecoder_nativeAddBuffer(JNIEnv *env, jobject, jlong descriptor, jobject bufferObj) {
    AudioDecoder *decoder = (AudioDecoder *) descriptor;
    uint8_t *buffer = (uint8_t *) env->GetDirectBufferAddress(bufferObj);
    decoder->addBuffer(buffer);
}


JNIEXPORT int Java_com_vipycm_mao_media_AudioDecoder_nativePrepare(JNIEnv *env, jobject, jlong descriptor, jstring path) {
    JniString jniPath(env, path);
    AudioDecoder *decoder = (AudioDecoder *) descriptor;
    return decoder->prepare(jniPath.get());
}

JNIEXPORT int Java_com_vipycm_mao_media_AudioDecoder_nativeDecode(JNIEnv *, jobject, jlong descriptor) {
    AudioDecoder *decoder = (AudioDecoder *) descriptor;
    int size = decoder->decode();
    return size;
}

JNIEXPORT void Java_com_vipycm_mao_media_AudioDecoder_nativeRelease(JNIEnv *, jobject, jlong descriptor) {
    delete ((AudioDecoder *) descriptor);
}

#ifdef __cplusplus
}
#endif