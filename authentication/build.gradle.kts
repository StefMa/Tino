plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(23)
        targetSdkVersion(29)
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    sourceSets.all { java.srcDir("src/$name/kotlin") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7")

    implementation("com.google.firebase:firebase-auth:19.2.0")
}
