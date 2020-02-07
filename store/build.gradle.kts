plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
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

    implementation("com.google.firebase:firebase-firestore:21.4.0")

    val roomVersion = "2.2.3"
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
}
