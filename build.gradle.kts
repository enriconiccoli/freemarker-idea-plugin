plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.0"
}

group = "com.ennic"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

intellij {
    version.set("2024.1.4")
    type.set("IC") // IntelliJ Community Edition
}

tasks {
    patchPluginXml {
        changeNotes.set("Initial version with FreeMarker file type support.")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
