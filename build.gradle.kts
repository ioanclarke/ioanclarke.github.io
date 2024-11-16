plugins {
    kotlin("jvm") version "2.0.20"
}

group = "ioan"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.ktor:ktor-server-core:3.0.1")
    implementation("io.ktor:ktor-server-netty:3.0.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("generate") {
    group = "application"
    description = "Runs the main method of the specified Kotlin file"
    mainClass.set("ssg.GeneratorKt")
    classpath = sourceSets["main"].runtimeClasspath
}

kotlin {
    jvmToolchain(21)
}
