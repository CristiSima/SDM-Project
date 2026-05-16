#include <jni.h>

// Write C++ code here.
//
// Do not forget to dynamically load the C++ library into your application.
//
// For instance,
//
// In MainActivity.java:
//    static {
//       System.loadLibrary("cloudpc");
//    }
//
// Or, in MainActivity.kt:
//    companion object {
//      init {
//         System.loadLibrary("cloudpc")
//      }
//    }


extern "C"
JNIEXPORT void JNICALL
Java_com_google_android_apps_work_cloudpc_MainActivity_asd(JNIEnv *env, jclass clazz) {
    // get reference to Log class and call println method
    jclass logClass = env->FindClass("android/util/Log");
    if (logClass == nullptr) return;

    // 2. Get the priority value (Log.INFO)
    jfieldID infoField = env->GetStaticFieldID(logClass, "INFO", "I");
    jint infoPriority = env->GetStaticIntField(logClass, infoField);

    // 3. Get the Method ID for Log.println(int priority, String tag, String msg)
    // The signature (ILjava/lang/String;Ljava/lang/String;)I means:
    // (int, String, String) returning int
    jmethodID printlnMethod = env->GetStaticMethodID(logClass, "println", "(ILjava/lang/String;Ljava/lang/String;)I");

    if (printlnMethod != nullptr) {
        // 4. Create strings for the tag and message
        jstring tag = env->NewStringUTF("JNI_LOG");
        jstring msg = env->NewStringUTF("Hello from C++!");

        // 5. Call the static method
        env->CallStaticIntMethod(logClass, printlnMethod, infoPriority, tag, msg);

        // Clean up local references
        env->DeleteLocalRef(tag);
        env->DeleteLocalRef(msg);
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_google_android_apps_work_cloudpc_Manager_asd(JNIEnv *env, jclass clazz) {
    // TODO: implement asd()
    Java_com_google_android_apps_work_cloudpc_MainActivity_asd(env, clazz);
}