plugins {
	id("org.springframework.boot").version("3.5.4")
	id("io.spring.dependency-management").version("latest.release")
	java
}

group = "com.example.ai"
version = "0.0.1-SNAPSHOT"

java {
	version = 21
}

repositories {
	mavenCentral()
    maven {
        url = uri("https://repo.spring.io/snapshot")
    }
    maven {
        url = uri("https://central.sonatype.com/repository/maven-snapshots")
    }
}

dependencies {
	implementation(platform("org.springframework.ai:spring-ai-bom:1.1.0-SNAPSHOT"))
	implementation("org.springframework.boot:spring-boot-docker-compose")
	implementation("org.springframework.ai:spring-ai-starter-model-openai")
	implementation("org.springframework.ai:spring-ai-starter-model-zhipuai")
	implementation("org.springframework.ai:spring-ai-tika-document-reader")
	implementation("org.springframework.ai:spring-ai-starter-vector-store-pgvector")
	//implementation("org.springframework.ai:spring-ai-advisors-vector-store")
	implementation("org.springframework.ai:spring-ai-rag")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
