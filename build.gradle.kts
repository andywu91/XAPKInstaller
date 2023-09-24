// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version agpVersion apply false
    id("com.android.library") version agpVersion apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
}

tasks.register<Delete>("clean"){
    delete(rootProject.buildDir)
}