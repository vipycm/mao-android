//
// Created by mao on 17-8-10.
//

#include "AudioDecoder.h"
#include "../util/log.h"


AudioDecoder::AudioDecoder() {
    pFrame = NULL;
    pCodecCtx = NULL;
    pCodecCtxOrig = NULL;
    pFormatCtx = NULL;
}

AudioDecoder::~AudioDecoder() {
    LOGE(TAG_AudioDecoder, "~AudioDecoder()");
    release();
}

void AudioDecoder::addBuffer(uint8_t *buffer) {
    mBuffer = buffer;
}

int AudioDecoder::prepare(const char *path) {
    release();
    if (path == NULL) {
        return -1;
    }
    pFormatCtx = avformat_alloc_context();
    if (avformat_open_input(&pFormatCtx, path, NULL, NULL) < 0) {
        LOGE(TAG_AudioDecoder, "Couldn't open input stream.");
        return -1;
    }
    if (avformat_find_stream_info(pFormatCtx, NULL) < 0) {
        LOGE(TAG_AudioDecoder, "Couldn't find stream information.\n");
        return -1;
    }
    mAudioIndex = -1;
    for (int i = 0; i < pFormatCtx->nb_streams; i++) {
        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_AUDIO) {
            mAudioIndex = i;
            break;
        }
    }
    if (mAudioIndex < 0) {
        return -1;
    }
    pCodecCtxOrig = pFormatCtx->streams[mAudioIndex]->codec;
    pCodec = avcodec_find_decoder(pCodecCtxOrig->codec_id);
    if (pCodec == NULL) {
        LOGE(TAG_AudioDecoder, "Couldn't find Codec.\n");
        return -1;
    }
    pCodecCtx = avcodec_alloc_context3(pCodec);
    if (avcodec_copy_context(pCodecCtx, pCodecCtxOrig) != 0) {
        LOGE(TAG_AudioDecoder, "Couldn't copy codec context");
        return -1;
    }
    if (avcodec_open2(pCodecCtx, pCodec, NULL) < 0) {
        LOGE(TAG_AudioDecoder, "Couldn't open codec.\n");
        return -1;
    }
    pFrame = av_frame_alloc();
    packet = (AVPacket *) av_malloc(sizeof(AVPacket));
    mBytesPerSample = av_get_bytes_per_sample(pCodecCtx->sample_fmt);
    return 0;
}

int AudioDecoder::decode() {
    int data_size = -1;
    if (av_read_frame(pFormatCtx, packet) >= 0) {

        if (packet->stream_index == mAudioIndex) {

            avcodec_send_packet(pCodecCtx, packet);
            int receive = avcodec_receive_frame(pCodecCtx, pFrame);
//            int len1 = avcodec_decode_audio4(pCodecCtx, pFrame, &got_frame, packet);
            if (receive == 0) {
                data_size = av_samples_get_buffer_size(NULL,
                                                       pCodecCtx->channels,
                                                       pFrame->nb_samples,
                                                       pCodecCtx->sample_fmt,
                                                       1);

                int sampleIndex, channelIndex, dataOffset, bufferOffset;
                //将音频信息写入到mBuffer
                for (sampleIndex = 0; sampleIndex < pFrame->nb_samples; sampleIndex++) {
                    dataOffset = sampleIndex * mBytesPerSample;
                    for (channelIndex = 0; channelIndex < pCodecCtx->channels; channelIndex++) {
                        bufferOffset = (sampleIndex * pCodecCtx->channels + channelIndex) * mBytesPerSample;
                        memcpy(mBuffer + bufferOffset, (pFrame->data[channelIndex]) + dataOffset, mBytesPerSample);
                    }
                }
            }
        }
        av_free_packet(packet);
    }

    return data_size;
}

void AudioDecoder::release() {
    if (pFrame != NULL) {
        av_frame_free(&pFrame);
        pFrame = NULL;
    }
    if (pCodecCtx != NULL) {
        avcodec_close(pCodecCtx);
        pCodecCtx = NULL;
    }
    if (pCodecCtxOrig != NULL) {
        avcodec_close(pCodecCtxOrig);
        pCodecCtxOrig = NULL;
    }
    if (pFormatCtx != NULL) {
        avformat_close_input(&pFormatCtx);
        pFormatCtx = NULL;
    }
}
