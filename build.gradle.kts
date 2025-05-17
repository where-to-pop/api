import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.0"
	id("org.asciidoctor.jvm.convert") version "4.0.2"
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	kotlin("plugin.serialization") version "1.9.23"
	kotlin("plugin.jpa") version "1.9.23"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21


repositories {
//	mavenLocal()
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
	maven {
		url = uri("https://repo.spring.io/milestone")
		mavenContent {
			releasesOnly()
		}
	}
	maven {
		name = "Central Portal Snapshots"
		url = uri("https://central.sonatype.com/repository/maven-snapshots/")
	}

}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}



dependencies {
	// Spring AI
//	implementation(platform("org.springframework.ai:spring-ai-bom:1.0.0-SNAPSHOT"))
	implementation(platform("org.springframework.ai:spring-ai-bom:1.0.0-M8"))
	implementation("org.springframework.ai:spring-ai-openai")
	implementation("org.springframework.ai:spring-ai-starter-model-openai")
	implementation("org.springframework.ai:spring-ai-starter-mcp-client")
	implementation("org.springframework.ai:spring-ai-starter-vector-store-weaviate")

	// Spring MVC + JPA (WebFlux, R2DBC 대체)
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.mariadb.jdbc:mariadb-java-client")

	// Kotlin 및 코루틴
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
	implementation("io.github.microutils:kotlin-logging:3.0.5")

	// security
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	
	// Bean Validation
	implementation("org.springframework.boot:spring-boot-starter-validation")


	// UUID 생성기
	implementation("com.github.f4b6a3:uuid-creator:5.3.3")

	// for crawling
	implementation("org.jsoup:jsoup:1.19.1")
	implementation("org.seleniumhq.selenium:selenium-java:4.31.0")
//	implementation("io.github.bonigarcia:webdrivermanager:5.8.0")
	implementation("org.apache.httpcomponents.client5:httpclient5:5.3.1")

	// REST Docs
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	
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
	jvmToolchain(21)
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		jvmTarget = "21"
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