//
// Created by mao on 17-8-10.
//

#ifndef MAO_ANDROID_AUDIODECODER_H
#define MAO_ANDROID_AUDIODECODER_H

#define TAG_AudioDecoder "AudioDecoder"

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
}

class AudioDecoder {
private:
    uint8_t *mBuffer;


    AVFormatContext *pFormatCtx;
    AVCodecContext *pCodecCtxOrig;
    AVCodecContext *pCodecCtx;
    AVCodec *pCodec;
    int mAudioIndex;
    int mBytesPerSample;

    AVPacket *packet;
    AVFrame *pFrame;

public:
    AudioDecoder();

    ~AudioDecoder();

    void addBuffer(uint8_t *buffer);

    int prepare(const char *path);

    int decode();

    void release();
};


#endif //MAO_ANDROID_AUDIODECODER_H
