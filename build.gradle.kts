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
}

dependencies {
	implementation(platform("org.springframework.ai:spring-ai-bom:1.0.0"))
	implementation("org.springframework.ai:spring-ai-starter-model-openai")
	implementation("org.springframework.ai:spring-ai-starter-mcp-client-webflux")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
