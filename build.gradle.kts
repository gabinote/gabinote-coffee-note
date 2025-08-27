import org.gradle.kotlin.dsl.withType
import org.hidetake.gradle.swagger.generator.GenerateSwaggerUI

plugins {
    kotlin("jvm") version "2.2.10"
    kotlin("plugin.spring") version "2.2.10"
    kotlin("kapt") version "2.2.10"
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.epages.restdocs-api-spec") version "0.17.1"
    id("org.hidetake.swagger.generator") version "2.18.2"
}

group = "com.gabinote"
version = "0.0.1-SNAPSHOT"
description = "gabi-coffeenote"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}


dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:1.20.6")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    //core
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    // junit
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    // mockk
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    // https://mvnrepository.com/artifact/io.mockk/mockk
    testImplementation("io.mockk:mockk:1.14.2")
    // https://mvnrepository.com/artifact/com.ninja-squad/springmockk
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    // testcontainers
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mongodb")
    // rest assured
    testImplementation("io.rest-assured:rest-assured:5.5.5")
    testImplementation("io.rest-assured:kotlin-extensions:5.5.5")
    // kotest
    val kotestVersion = "5.9.1"
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    testImplementation("org.springframework:spring-jdbc")

    // web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // database
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    // https://mvnrepository.com/artifact/org.mongodb/mongodb-driver-kotlin-sync
//    implementation("org.mongodb:mongodb-driver-kotlin-sync:5.5.1")


    // caching
    implementation("org.springframework.boot:spring-boot-starter-cache")
    // https://mvnrepository.com/artifact/com.github.ben-manes.caffeine/caffeine
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.0")

    // aop
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework:spring-aspects")

    // docs
    implementation("org.webjars:swagger-ui:4.11.1")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.17.1")

    // logging
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.5")

    // dto
    // mapstruct
    implementation("org.mapstruct:mapstruct:1.6.3")
    kapt("org.mapstruct:mapstruct-processor:1.6.3")
    kaptTest("org.mapstruct:mapstruct-processor:1.6.3")
    // jackson
    implementation(platform("com.fasterxml.jackson:jackson-bom:2.15.2"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(
        "--add-opens", "java.base/java.time=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED"
    )
    systemProperty("spring.profiles.active", "test")
    testLogging {
        events("passed", "skipped", "failed")
    }
    finalizedBy("openapi3")
}


openapi3 {
    title = "API 문서"
    description = "RestDocsWithSwagger Docs"
    version = "0.0.1"
    format = "yaml"
}


tasks.withType<GenerateSwaggerUI> {
    dependsOn("openapi3")
}
