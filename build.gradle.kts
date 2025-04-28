import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.2"
	id("io.spring.dependency-management") version "1.1.0"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
	id("org.flywaydb.flyway") version "9.22.3"
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	kotlin("plugin.serialization") version "1.9.23"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17


repositories {
	mavenCentral()
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}



dependencies {
	// Spring WebFlux + R2DBC
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
	implementation("org.mariadb:r2dbc-mariadb:1.1.3")
	runtimeOnly("org.mariadb:r2dbc-mariadb:1.1.3")

	// Kotlin 및 코루틴
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
	implementation("io.github.microutils:kotlin-logging:3.0.5")

	// Bean Validation
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// Elasticsearch
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

	// UUID 생성기
	implementation("com.github.f4b6a3:uuid-creator:5.3.3")

	// JDBC 드라이버 (옵션)
	implementation("org.mariadb.jdbc:mariadb-java-client")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

	// MapStruct
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
	annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

	// REST Docs
	testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
	// 테스트
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mariadb")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val asciidoctorExt: Configuration by configurations.creating

dependencies {
	"asciidoctorExt"("org.springframework.restdocs:spring-restdocs-asciidoctor")
}


kotlin {
	jvmToolchain(17)
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	outputs.dir(file("build/generated-snippets"))
}

tasks.named<org.asciidoctor.gradle.jvm.AsciidoctorTask>("asciidoctor") {
	inputs.dir(file("build/generated-snippets"))
	dependsOn(tasks.test)
	configurations("asciidoctorExt")
	sources {
		include("**/*.adoc")
	}
	baseDirFollowsSourceDir()
}

tasks.register<Copy>("copyRestDocs") {
	dependsOn(tasks.named("asciidoctor"))
	from("build/docs/asciidoc")
	into("src/main/resources/static/docs")
}