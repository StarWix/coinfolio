plugins {
    java
    idea
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.openapi.generator") version "7.4.0"
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

group = "net.starwix"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
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
    implementation("org.jetbrains:annotations:24.1.0")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.shell:spring-shell-starter:3.2.4")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.xerial:sqlite-jdbc:3.44.1.0")
    implementation("org.hibernate.orm:hibernate-community-dialects:6.4.4.Final")

    implementation("net.osslabz:coingecko-java:1.0.0")

    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.google.code.findbugs:jsr305:3.0.2")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

openApiGenerate {
    generatorName = "java"
    inputSpec = "$projectDir/swagger/blockscout/swagger.yaml"
    outputDir = "$buildDir/generated/"
    apiPackage = "net.starwix.blockscount.client.api"
    modelPackage = "net.starwix.blockscount.model.api"
    library = "resttemplate"

    skipValidateSpec = true
    skipOperationExample = true
    configOptions = mapOf(
            "openApiNullable" to "false"
    )
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("$buildDir/generated/src/main/java")
    }
}

tasks.compileJava {
    dependsOn("openApiGenerate")
}
