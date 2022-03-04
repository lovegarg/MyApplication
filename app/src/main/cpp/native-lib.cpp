#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_test_myapplication_MainActivity_stringFromJNITimeStamp(
        JNIEnv* env,
        jobject /* this */, jobject to_string) {

    jclass cls = (*env).GetObjectClass(to_string);
    _jfieldID *timestamp = (*env).GetFieldID(cls, "lastUpdate", "Ljava/lang/String;");
    _jobject *sTimestamp = (*env).GetObjectField(to_string, timestamp);

    // we have to get string bytes into C string
    const char *c_str;
    c_str = (*env).GetStringUTFChars(static_cast<jstring>(sTimestamp), NULL);
    if(c_str == NULL) {
        return (jstring) "";
    }

    return env->NewStringUTF(c_str);
}


extern "C"
JNIEXPORT double JNICALL
Java_com_test_myapplication_MainActivity_stringFromJNIPrice(JNIEnv *env, jobject thiz,
                                                            jobject to_string) {
    jclass cls = (*env).GetObjectClass(to_string);
    _jfieldID *price = (*env).GetFieldID(cls, "price", "D");
    double dPrice = (*env).GetDoubleField(to_string, price);

    return dPrice;
}


extern "C"
JNIEXPORT jstring JNICALL
Java_com_test_myapplication_MainActivity_stringFromJNISymbol(JNIEnv *env, jobject thiz,
                                                             jobject to_string) {
    jclass cls = (*env).GetObjectClass(to_string);
    _jfieldID *symbol = (*env).GetFieldID(cls, "symbol", "Ljava/lang/String;");
    _jobject *sSymbol = (*env).GetObjectField(to_string, symbol);

    // we have to get string bytes into C string
    const char *c_str;
    c_str = (*env).GetStringUTFChars(static_cast<jstring>(sSymbol), NULL);
    if(c_str == NULL) {
        return (jstring) "";
    }

    return env->NewStringUTF( c_str);
}