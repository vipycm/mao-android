#include <jni.h>
#include <string>

extern "C"
jstring Java_com_vipycm_mao_jni_HelloJni_stringFromJNI(JNIEnv *env, jobject) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
