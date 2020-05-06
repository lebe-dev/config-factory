import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    id("io.gitlab.arturbosch.detekt") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "ru.tinyops.cf"
version = "1.0.2"

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/arrow-kt/arrow-kt/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("io.arrow-kt:arrow-core-data:0.10.5")

    implementation("com.typesafe:config:1.3.3")
    implementation("commons-cli:commons-cli:1.4")
    implementation("commons-lang:commons-lang:2.6")
    
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.slf4j:slf4j-api:1.7.28")

    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

detekt {
    input = project.files("src/main/kotlin")

    config = files("detekt.yml")

    reports {
        html {
            enabled = true
            destination = File("detekt.html")
        }
    }
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("config-factory")
        mergeServiceFiles()
        manifest {
            attributes["Main-Class"] = "ru.tinyops.cf.App"
        }
    }
}
