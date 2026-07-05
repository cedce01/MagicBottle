import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.3.1"
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:26.1.1-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    implementation("org.bstats:bstats-bukkit:3.2.1")
    compileOnly("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {

    // Disable default jars
    jar { enabled = false }
    named<ShadowJar>("shadowJar") { enabled = false }

    val commonShadow: ShadowJar.() -> Unit = {
        configurations = listOf(project.configurations.runtimeClasspath.get())

        dependencies {
            exclude { it.moduleGroup != "org.bstats" }
        }

        relocate("org.bstats", "vontus.magicbottle.bstats")

        from(sourceSets.main.get().output) {
            exclude("plugin.yml")
        }
    }



    // Spigot/Paper jar
    val pluginJar = register<ShadowJar>("pluginJar") {
        archiveBaseName.set("MagicBottle")
        archiveClassifier.set("spigot")

        from("src/main/resources/plugin.yml") {
            expand(
                mapOf(
                    "version" to project.version,
                    "mainClass" to "vontus.magicbottle.Plugin"
                )
            )
            into("")
        }

        commonShadow()
    }

    build {
        dependsOn(pluginJar)
    }
}
tasks.compileJava {
    options.compilerArgs.add("-Xlint:deprecation")
}

