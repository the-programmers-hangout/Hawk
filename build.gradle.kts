import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "me.abberantfox"
version = Versions.BOT
description = "A bot to add and maintain a symbol as a prefix or suffix in staff names."

plugins {
    kotlin("jvm") version "1.4.10"
    id("com.github.johnrengelman.shadow") version "6.0.0"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("me.jakejmattson:DiscordKt:${Versions.DISCORDKT}")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    shadowJar {
        archiveFileName.set("Hawk.jar")
        manifest {
            attributes(
                    "Main-Class" to "me.aberrantfox.hawk.MainKt"
            )
        }
    }
}


object Versions {
    const val BOT = "1.2.0"
    const val DISCORDKT = "0.19.1"
}