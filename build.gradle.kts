plugins {
    java
    id("com.gradleup.shadow") version "9.6.1"
}

group = "thegate"
version = "0.3.0-mc26.1.2"

val paperVersion = "26.1.2.build.74-stable"
val packetEventsVersion = "2.14.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-releases/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
    implementation("com.github.retrooper:packetevents-spigot:$packetEventsVersion") {
        // Paper already ships these Adventure modules, but not adventure-nbt.
        exclude(group = "net.kyori", module = "adventure-api")
        exclude(group = "net.kyori", module = "adventure-key")
        exclude(group = "net.kyori", module = "examination-api")
        exclude(group = "net.kyori", module = "examination-string")
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

// The plugin and its bundled helper libraries keep their original source folders.
sourceSets {
    main {
        java {
            setSrcDirs(listOf(
                "TheGate/src",
                "EasyAPIs/EasyUI_API/src",
                "EasyAPIs/EasyPackage_API/src",
            ))
        }
        resources {
            setSrcDirs(listOf("TheGate/src"))
            include("*.yml")
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 25
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.shadowJar {
    archiveClassifier = ""
    relocate("com.github.retrooper.packetevents", "thegate.libs.packetevents.api")
    relocate("io.github.retrooper.packetevents", "thegate.libs.packetevents.impl")
    relocate("net.kyori.adventure.nbt", "thegate.libs.adventure.nbt") {
        // BinaryTagHolder lives in adventure-api, which Paper provides.
        exclude("net.kyori.adventure.nbt.api.**")
    }
    mergeServiceFiles()
}

tasks.jar {
    archiveClassifier = "plain"
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}
