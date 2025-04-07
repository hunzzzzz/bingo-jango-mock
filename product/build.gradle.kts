plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
	kotlin("kapt") version "1.9.25"
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

extra["queryDslVersion"] = "5.0.0"

dependencies {
	// database
	implementation("com.mysql:mysql-connector-j")
	testImplementation("com.h2database:h2")
	// jpa
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	// kotlin
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	// queryDsl
	implementation("com.querydsl:querydsl-jpa:${property("queryDslVersion")}:jakarta")
	kapt("com.querydsl:querydsl-apt:${property("queryDslVersion")}:jakarta")
	// redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	// test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	// web
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
