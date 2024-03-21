// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.4.2")
        classpath("com.google.gms:google-services:4.4.0")
        classpath ("com.vanniktech:gradle-maven-publish-plugin:0.14.2")
        classpath ("org.jetbrains.dokka:dokka-gradle-plugin:1.4.10.2")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        maven {
            url = uri("https://maven.google.com")
            url = uri("https://jitpack.io")
        }
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
