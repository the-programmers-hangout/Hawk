import java.util.*

group = "me.ddivad"
version = "3.1"
description = "A bot to add and maintain a symbol as a prefix or suffix in staff names.\n"

plugins {
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("me.jakejmattson:DiscordKt:0.23.4")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")
    implementation("ch.qos.logback:logback-classic:1.4.0")
    implementation("ch.qos.logback:logback-core:1.4.0")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"

        Properties().apply {
            setProperty("name", "Hawk")
            setProperty("description", project.description)
            setProperty("version", version.toString())
            setProperty("url", "https://github.com/the-programmers-hangout/Hawk/")

            store(file("src/main/resources/bot.properties").outputStream(), null)
        }
    }

    shadowJar {
        archiveFileName.set("Hawk.jar")
        manifest {
            attributes(
                "Main-Class" to "me.ddivad.hawk.MainKt"
            )
        }
    }
}
