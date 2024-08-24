plugins {
    kotlin("jvm")
}

repositories {
    maven {
        name = "TarsosDSP repository"
        url = uri("https://mvn.0110.be/releases")
    }
}
dependencies {
    implementation("be.tarsos.dsp:core:2.5")
    implementation("be.tarsos.dsp:jvm:2.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.apache.commons:commons-exec:1.3")
    implementation("com.tambapps.fft4j:fft4j:2.0")
}