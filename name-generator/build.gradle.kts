plugins {
    kotlin("jvm")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
    testImplementation("com.google.truth:truth:1.0.1")
}

tasks.withType(Test::class.java) {
    useJUnitPlatform()
}
