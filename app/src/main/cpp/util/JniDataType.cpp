//
// Created by mao on 17-8-15.
//

#include <cstdlib>
#include "JniDataType.h"

JniString::JniString(JNIEnv *env, jstring jniStr) : mEnv(env), mJniStr(jniStr), mConstCharPtr(NULL) {
    if (mEnv == NULL || mJniStr == NULL) {
        return;
    }
    mConstCharPtr = mEnv->GetStringUTFChars(mJniStr, NULL);
    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        mConstCharPtr = NULL;
    }
}

JniString::~JniString() {

    if (mEnv == NULL || mJniStr == NULL || mConstCharPtr == NULL) {
        return;
    }

    mEnv->ReleaseStringUTFChars(mJniStr, mConstCharPtr);
}

const char *JniString::get() const {
    return mConstCharPtr;
}

char *JniString::createCharPtr() const {
    if (mConstCharPtr == NULL) {
        return NULL;
    }
    char *charPtr = new char[strlen(mConstCharPtr) + 1];
    strcpy(charPtr, mConstCharPtr);
    return charPtr;
}
