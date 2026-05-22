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
            isMinifyEnabled = false
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
    // .\gradlew assemble
    var sourceFile = file("../../stage2/app/build/outputs/apk/release/app-release-unsigned.apk")
    val dest = "src/main/res/raw/app_debug.apk"

    // Safety check: print a warning if the file is missing
    doFirst {
        if (sourceFile.exists()) {
            logger.lifecycle("Copying ${sourceFile} to ${dest}")
            return@doFirst
        }
        logger.error("FAILED: Source APK not found at ${sourceFile.absolutePath}. Please build the stage2 project first.")

        sourceFile = file("../../stage2/app/build/outputs/apk/debug/app-debug.apk")

        if (sourceFile.exists()) {
            logger.lifecycle("Copying ${sourceFile} to ${dest}")
            return@doFirst
        }
        logger.error("FAILED: Source APK not found at ${sourceFile.absolutePath}. Please build the stage2 project first.")

        sourceFile = file("../../stage2/app/build/intermediates/apk/debug/app-debug.apk")

        if (sourceFile.exists()) {
            logger.lifecycle("Copying ${sourceFile} to ${dest}")
            return@doFirst
        }
        logger.error("FAILED: Source APK not found at ${sourceFile.absolutePath}. Please build the stage2 project first.")

        throw GradleException("FAILED: Source APK not found at. Please build the stage2 project first.")
    }

    var sourceDir = sourceFile.parent
    val sourceFileName = sourceFile.name

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
