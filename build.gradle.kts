plugins {
    java
}

group = "thegate"
version = "0.3.0-mc26.1.2"

val paperVersion = "26.1.2.build.74-stable"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
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
