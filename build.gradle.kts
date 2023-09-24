// Top-level build file where you can add configuration options common to all sub-projects/modules.


buildscript {
    repositories {
        google()
        maven("https://mirrors.tencent.com/nexus/repository/maven-public/")

        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        maven("https://mirrors.tencent.com/nexus/repository/maven-public/")

        mavenCentral()
    }
}

tasks.register<Delete>("clean"){
    delete(rootProject.buildDir)
}