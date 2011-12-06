#include <string.h>
#include <jni.h>


jstring
Java_com_acme_hellojni_HelloJni_stringFromJNI( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env, "Hello from Native JNI!");
}
