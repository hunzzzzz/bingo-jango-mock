plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "2.0.0"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

extra["jwtVersion"] = "0.12.6"

dependencies {
	// jwt
	implementation("io.jsonwebtoken:jjwt-api:${property("jwtVersion")}")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:${property("jwtVersion")}")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:${property("jwtVersion")}")
	// kotlin
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	// redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	// test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	// web
	implementation("org.springframework.boot:spring-boot-starter-web")
	// websocket
	implementation("org.springframework.boot:spring-boot-starter-websocket")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
