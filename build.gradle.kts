plugins {
    id("java")
    kotlin("jvm") version "1.8.20"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(mapOf("path" to ":analyzer")))
    implementation(project(mapOf("path" to ":storage")))
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
    implementation("commons-io:commons-io:2.13.0")
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }
}
