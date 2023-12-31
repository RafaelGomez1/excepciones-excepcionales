import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.4"
	id("io.spring.dependency-management") version "1.1.3"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
	kotlin("plugin.jpa") version "1.8.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

	// H2
	runtimeOnly("com.h2database:h2")

	// Functional Programming
	implementation("io.arrow-kt:arrow-core:1.2.0")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	testImplementation("io.kotest.extensions:kotest-assertions-arrow:1.3.0") {
		because("provides good testing for arrow")
	}

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// Mockito
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
	testImplementation("org.mockito:mockito-core:4.0.0")
	testImplementation("org.mockito:mockito-inline:4.0.0")

	// Faker
	testImplementation("io.github.serpro69:kotlin-faker:1.8.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
