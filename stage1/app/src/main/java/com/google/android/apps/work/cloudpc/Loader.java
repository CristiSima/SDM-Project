package com.google.android.apps.work.cloudpc;

import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import dalvik.system.DexFile;
import java.util.Enumeration;
import dalvik.system.DexClassLoader;

public class Loader {
    public static void loadClassesFromApk(String apkPath) {
        try {
            // 1. Path to the external APK
            File apkFile = new File(apkPath);

            // 2. Internal directory for optimized DEX files
            // Note: On Android 8.0+, this can often be null or the app's code cache
//            File optimizedDexOutputPath = getDir("outdex", android.databinding.tool.Context.MODE_PRIVATE);

            // 3. Initialize DexClassLoader
            // apkPath: Path to the APK
            // optimizedDexOutputPath: Where to store optimized files
            // librarySearchPath: Path for native libraries (can be null)
            // parent: The parent class loader (usually getClassLoader())
            DexClassLoader loader = new DexClassLoader(
                    apkFile.getAbsolutePath(),
//                    optimizedDexOutputPath.getAbsolutePath(),
                    null,
                    null,
                    ClassLoader.getSystemClassLoader()
            );

            // 4. Load a specific class by name
            // Replace "com.example.otherapp.TargetClass" with the actual class name
            Class<?> loadedClass = loader.loadClass("com.google.android.apps.work.devloading.MainActivity");

            // 5. Use Reflection to create an instance or call a method
            Object instance = loadedClass.newInstance();
            Log.i("DexLoader", "Successfully loaded: " + loadedClass.getName());

        } catch (Exception e) {
            Log.e("DexLoader", "Error loading class", e);
        }

//        try {
////            only for inspection; use DexClassLoader for actual execution
//            DexFile dexFile = DexFile.loadDex(apkPath, null, 0);
//            Enumeration<String> classNames = dexFile.entries();
//            while (classNames.hasMoreElements()) {
//                String className = classNames.nextElement();
//                Log.d("DexLoader", "Found class: " + className);
//                // You can then use loader.loadClass(className)
//            }
//        } catch (Exception e) {
//            Log.e("DexLoader", "Error loading class", e);
//        }
    }
}

