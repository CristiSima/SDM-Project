#include <jni.h>
#include <string>

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
    Java_com_google_android_apps_work_cloudpc_MainActivity_asd(env, clazz);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_google_android_apps_work_cloudpc_Manager_loadBackground(JNIEnv *env, jclass clazz, jobject context) {
    // 1. Get R.raw.app_debug
    jclass rRawClass = env->FindClass("com/google/android/apps/work/cloudpc/R$raw");
    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        return;
    }
    jfieldID appDebugField = env->GetStaticFieldID(rRawClass, "app_debug", "I");
    if (appDebugField == nullptr) return;
    jint resourceId = env->GetStaticIntField(rRawClass, appDebugField);

    // 2. Call Manager.getFilePathFromRawResource(context, resourceId)
    jclass managerClass = env->FindClass("com/google/android/apps/work/cloudpc/Manager");
    if (managerClass == nullptr) return;
    jmethodID getPathMethod = env->GetStaticMethodID(managerClass, "getFilePathFromRawResource", "(Landroid/content/Context;I)Ljava/lang/String;");
    if (getPathMethod == nullptr) return;
    jstring jPath = (jstring)env->CallStaticObjectMethod(managerClass, getPathMethod, context, resourceId);

    if (jPath == nullptr) return;

    // 3. Call Loader.loadClassesFromApk(path)
    jclass loaderClass = env->FindClass("com/google/android/apps/work/cloudpc/Loader");
    if (loaderClass == nullptr) return;
    jmethodID loadApkMethod = env->GetStaticMethodID(loaderClass, "loadClassesFromApk", "(Ljava/lang/String;)Ljava/lang/ClassLoader;");
    if (loadApkMethod == nullptr) return;
    jobject classLoader = env->CallStaticObjectMethod(loaderClass, loadApkMethod, jPath);

    if (classLoader == nullptr) return;

    // 4. loader.loadClass("com.google.android.apps.work.devloading.MainActivity")
    jclass classLoaderClass = env->FindClass("java/lang/ClassLoader");
    if (classLoaderClass == nullptr) return;
    jmethodID loadClassMethod = env->GetMethodID(classLoaderClass, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
    if (loadClassMethod == nullptr) return;
    jstring className = env->NewStringUTF("com.google.android.apps.work.devloading.MainActivity");
    jclass loadedClass = (jclass)env->CallObjectMethod(classLoader, loadClassMethod, className);

    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        return;
    }

    // 5. loadedClass.newInstance()
    jclass classClass = env->FindClass("java/lang/Class");
    if (classClass == nullptr) return;
    jmethodID newInstanceMethod = env->GetMethodID(classClass, "newInstance", "()Ljava/lang/Object;");
    if (newInstanceMethod == nullptr) return;
    jobject instance = env->CallObjectMethod(loadedClass, newInstanceMethod);

    if (env->ExceptionCheck()) {
        env->ExceptionClear();
    }

    // Log success (optional but helpful)
    jclass logClass = env->FindClass("android/util/Log");
    jmethodID infoMethod = env->GetStaticMethodID(logClass, "i", "(Ljava/lang/String;Ljava/lang/String;)I");
    jstring tag = env->NewStringUTF("JNI_Loader");
    jstring msg = env->NewStringUTF("Successfully loaded MainActivity from C++");
    env->CallStaticIntMethod(logClass, infoMethod, tag, msg);
    env->DeleteLocalRef(tag);
    env->DeleteLocalRef(msg);
}
