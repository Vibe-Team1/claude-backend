// Kotlin 컴파일 태스크 import 제거 (Java 프로젝트이므로 불필요)

plugins {
	java
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
	id("com.diffplug.spotless") version "6.25.0"
}

group = "com.stockroom"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot Starters
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// Database
	runtimeOnly("org.postgresql:postgresql")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.12.3")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

	// API Documentation
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

	// Lombok
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// MapStruct
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	// Development Tools
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// Test Dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("io.rest-assured:rest-assured")
	testImplementation("com.h2database:h2") // For lightweight unit tests

	// Test Lombok & MapStruct
	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")
	testAnnotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
}

dependencyManagement {
	imports {
		mavenBom("org.testcontainers:testcontainers-bom:1.19.3")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()

	// Test 환경변수 설정
	systemProperty("spring.profiles.active", "test")

	// 테스트 리포트 설정
	testLogging {
		events("passed", "skipped", "failed")
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
		showStandardStreams = false
	}

}


// Spotless 코드 포맷팅 설정 (Google Java Style)
spotless {
	java {
		googleJavaFormat("1.17.0")
		importOrder()
		removeUnusedImports()
		trimTrailingWhitespace()
		endWithNewline()
	}

}

// 빌드 시 코드 포맷 검사
tasks.build {
	dependsOn(tasks.spotlessCheck)
}

// JAR 설정
tasks.withType<Jar> {
	enabled = true
	archiveClassifier.set("")
}

// 실행 가능한 JAR 생성
tasks.bootJar {
	enabled = true
	archiveBaseName.set("stockroom-sns")
	archiveVersion.set(version.toString())
}

// 컴파일 옵션
tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
	options.compilerArgs.add("-parameters")
}

// 프로젝트 정보 출력 태스크
tasks.register("projectInfo") {
	doLast {
		println("========================================")
		println("Project: ${project.name}")
		println("Version: ${project.version}")
		println("Java Version: ${JavaVersion.current()}")
		println("Spring Boot Version: 3.2.2")
		println("========================================")
	}
}