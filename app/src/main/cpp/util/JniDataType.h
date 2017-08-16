//
// Created by mao on 17-8-15.
//

#ifndef MAO_ANDROID_JNIDATATYPE_H
#define MAO_ANDROID_JNIDATATYPE_H


#include <jni.h>

/**
 * 只能作为局部变量使用
 */
class JniString {
private:
    JNIEnv *mEnv;
    jstring mJniStr;
    const char *mConstCharPtr;

public:
    JniString(JNIEnv *env, jstring jniStr);

    ~JniString();

    const char *get() const;

    char *createCharPtr() const;
};


#endif //MAO_ANDROID_JNIDATATYPE_H
