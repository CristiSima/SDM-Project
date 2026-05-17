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

// Internal helper function to copy a raw resource to a temporary file and return its absolute path.
jstring getFilePathFromRawResource(JNIEnv *env, jobject context, jint resourceId) {
    jclass contextClass = env->GetObjectClass(context);

    // File cacheDir = context.getCodeCacheDir();
    jmethodID getCodeCacheDir = env->GetMethodID(contextClass, "getCodeCacheDir", "()Ljava/io/File;");
    jobject cacheDir = env->CallObjectMethod(context, getCodeCacheDir);

    // File tempApk = new File(cacheDir, "temp_loaded.apk");
    jclass fileClass = env->FindClass("java/io/File");
    jmethodID fileInit = env->GetMethodID(fileClass, "<init>", "(Ljava/io/File;Ljava/lang/String;)V");
    jstring fileName = env->NewStringUTF("temp_loaded.apk");
    jobject tempApk = env->NewObject(fileClass, fileInit, cacheDir, fileName);

    // InputStream is = context.getResources().openRawResource(resourceId);
    jmethodID getResources = env->GetMethodID(contextClass, "getResources", "()Landroid/content/res/Resources;");
    jobject resources = env->CallObjectMethod(context, getResources);
    jclass resourcesClass = env->GetObjectClass(resources);
    jmethodID openRawResource = env->GetMethodID(resourcesClass, "openRawResource", "(I)Ljava/io/InputStream;");
    jobject inputStream = env->CallObjectMethod(resources, openRawResource, resourceId);

    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        return nullptr;
    }

    // FileOutputStream os = new FileOutputStream(tempApk);
    jclass fosClass = env->FindClass("java/io/FileOutputStream");
    jmethodID fosInit = env->GetMethodID(fosClass, "<init>", "(Ljava/io/File;)V");
    jobject outputStream = env->NewObject(fosClass, fosInit, tempApk);

    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        return nullptr;
    }

    // Copy loop: while ((bytesRead = is.read(buffer)) > 0) { os.write(buffer, 0, bytesRead); }
    jclass isClass = env->FindClass("java/io/InputStream");
    jmethodID read = env->GetMethodID(isClass, "read", "([B)I");
    jmethodID write = env->GetMethodID(fosClass, "write", "([BII)V");
    jbyteArray buffer = env->NewByteArray(1024);

    jint bytesRead;
    while ((bytesRead = env->CallIntMethod(inputStream, read, buffer)) > 0) {
        env->CallVoidMethod(outputStream, write, buffer, 0, bytesRead);
        if (env->ExceptionCheck()) break;
    }

    // os.flush(); os.close(); is.close();
    jmethodID flush = env->GetMethodID(fosClass, "flush", "()V");
    env->CallVoidMethod(outputStream, flush);
    jmethodID closeOS = env->GetMethodID(fosClass, "close", "()V");
    env->CallVoidMethod(outputStream, closeOS);
    jmethodID closeIS = env->GetMethodID(isClass, "close", "()V");
    env->CallVoidMethod(inputStream, closeIS);

    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        return nullptr;
    }

    // return tempApk.getAbsolutePath();
    jmethodID getAbsolutePath = env->GetMethodID(fileClass, "getAbsolutePath", "()Ljava/lang/String;");
    return (jstring)env->CallObjectMethod(tempApk, getAbsolutePath);
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

    // 2. Call the native implementation of getFilePathFromRawResource
    jstring jPath = getFilePathFromRawResource(env, context, resourceId);

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
