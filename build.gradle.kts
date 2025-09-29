import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

group = "ru.emi.taskrunner"
java.sourceCompatibility = JavaVersion.VERSION_21

dependencies {
// Spring Boot
    // metrics
    implementation(Spring.boot.actuator)
    // database
    implementation(Spring.boot.data.r2dbc)
    implementation(Spring.boot.data.jpa)
    // web API
    implementation(Spring.boot.webflux)
    annotationProcessor(Spring.boot.configurationProcessor)
// Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:_")
    implementation("org.jetbrains.kotlin:kotlin-reflect:_")
    implementation(Kotlin.stdlib.jdk8)
    implementation(KotlinX.coroutines.reactor)
    implementation(KotlinX.coroutines.slf4j)
// metrics
    implementation("io.micrometer:micrometer-registry-prometheus:_")
// for JSON format logging
    implementation("net.logstash.logback:logstash-logback-encoder:_")
// database drivers
    implementation("org.postgresql:r2dbc-postgresql:_")
}

springBoot {
    buildInfo()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.set(listOf("-Xjsr305=strict"))
        jvmTarget.set(JvmTarget.JVM_21)
    }
}