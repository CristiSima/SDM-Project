package com.google.android.apps.work.cloudpc;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import dalvik.system.DexFile;
import java.util.Enumeration;
import dalvik.system.DexClassLoader;

public class Loader {
    public static ClassLoader loadClassesFromApk(String apkPath) {
        DexClassLoader loader = null;
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
            loader = new DexClassLoader(
                    apkFile.getAbsolutePath(),
                    null,
                    null,
                    Loader.class.getClassLoader()
            );

        } catch (Exception e) {
            Log.e("DexLoader", "Error loading class", e);
        }
        return loader;
    }
}

