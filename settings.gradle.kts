pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        maven("https://mirrors.tencent.com/nexus/repository/maven-public/")

        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven("https://mirrors.tencent.com/nexus/repository/maven-public/")

        mavenCentral()
    }
}

include(":app", ":lib")
rootProject.name="XAPK Installer"
