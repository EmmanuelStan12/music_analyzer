/*
* 1. kotlin("jvm"):
    This applies the Kotlin JVM plugin.
    It allows you to write Kotlin code that targets the Java Virtual Machine (JVM).
    This plugin adds support for compiling Kotlin code and running it on the JVM.
* 2. id("org.springframework.boot") version "3.3.1":
    This applies the Spring Boot plugin with the specified version.
    The Spring Boot plugin provides tasks and configurations to easily create Spring Boot applications.
    It helps in packaging the application, managing dependencies, and setting up the classpath.
* 3. id("io.spring.dependency-management") version "1.1.5":
    This applies the Spring Dependency Management plugin.
    It allows you to use a dependency management section within your build.gradle.kts file, similar to how you would in a Maven pom.xml file.
    This is useful for managing versions of dependencies across all subprojects consistently.
* 4. kotlin("plugin.spring") version "1.9.24":
    This applies the Kotlin Spring plugin.
    It makes working with Spring Framework in Kotlin easier by adding additional compiler support and features.
    This includes things like opening Kotlin classes and methods for Spring’s reflection-based APIs.
* 5. kotlin("plugin.allopen") version "1.9.24":
    This applies the Kotlin All-Open plugin.
    It configures the Kotlin compiler to automatically open classes and methods that are annotated with specific annotations.
    This is particularly useful in a Spring context where classes and methods often need to be open for proxies and other reflection-based features.
**/

plugins {
    kotlin("jvm")
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("plugin.spring") version "1.9.24"
    kotlin("plugin.allopen") version "1.9.24"
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.mysql:mysql-connector-j")
    api("org.jetbrains.kotlin:kotlin-reflect")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    api("org.springframework.boot:spring-boot-starter")
    testApi("org.springframework.boot:spring-boot-starter-test")
}
