import com.android.build.gradle.internal.dsl.SigningConfig

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val releaseKeyStoreFile: File?
    get() = file("release.keystore").run { if (exists()) this else null }

val TINO_KEYSTORE_PASSWORD = "Tino.Keystore.Password"
val TINO_KEYSTORE_KEY_ALIAS = "Tino.Keystore.KeyAlias"
val TINO_KEYSTORE_KEY_PASSWORD = "Tino.KeyStore.KeyPassword"

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "guru.stefma.tino"
        minSdkVersion(23)
        targetSdkVersion(29)
        versionCode = 5
        versionName = "1.2.0"
    }
    signingConfigs {
        named("debug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("release") {
            storeFile = releaseKeyStoreFile
            storePassword = findProperty(TINO_KEYSTORE_PASSWORD) as? String
            keyAlias = findProperty(TINO_KEYSTORE_KEY_ALIAS) as? String
            keyPassword = findProperty(TINO_KEYSTORE_KEY_PASSWORD) as? String
        }
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-dirty"
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = getReleaseSigningConfig(signingConfigs)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    sourceSets.all { java.srcDir("src/$name/kotlin") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.core:core-ktx:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")

    implementation("com.google.android.material:material:1.1.0")

    val lifecycleVersion = "2.2.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    kapt("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")

    implementation("de.halfbit:knot3:3.1.1")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")

    implementation("androidx.viewpager2:viewpager2:1.0.0")

    implementation(project(":authentication"))
    implementation(project(":store"))
    implementation(project(":name-generator"))
    implementation(project(":screenshot-sharer"))

    releaseImplementation("com.google.firebase:firebase-crashlytics:17.0.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.3.6")

    implementation("com.google.dagger:dagger:2.27")
    kapt("com.google.dagger:dagger-compiler:2.27")

    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
    testImplementation("com.google.truth:truth:1.0.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.3.6")
}

tasks.withType(Test::class.java) {
    useJUnitPlatform()
}

fun getReleaseSigningConfig(signingConfigs: NamedDomainObjectContainer<SigningConfig>): SigningConfig =
    if (releaseKeyStoreFile != null
        && properties.keys.contains(TINO_KEYSTORE_PASSWORD)
        && properties.keys.contains(TINO_KEYSTORE_KEY_ALIAS)
        && properties.keys.contains(TINO_KEYSTORE_KEY_PASSWORD)
    ) {
        logger.info("** Tino ** - Sign release with **Release Signing Configuration**")
        signingConfigs.getByName("release")
    } else {
        logger.info("** Tino ** - Sign release with **Debug Signing Configuration**")
        signingConfigs.getByName("debug")
    }

android.applicationVariants.all {
    if (name == "debug") {
        // Disable the `processDebugGoogleServices` tasks
        // because otherwise we can't build the `debug` buildType
        tasks.getByName("process${name.capitalize()}GoogleServices") {
            enabled = false
        }

        // Disable the `injectCrashlyticsMappingFileId` task
        // because we don't need it
        tasks.getByName("injectCrashlyticsMappingFileId${name.capitalize()}") {
            enabled = false
        }
    }
}