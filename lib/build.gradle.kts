plugins{
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
}

group = "com.wuliang.lib"
version = "1.0.1"

android {
    compileSdkVersion(28)

    defaultConfig {
        minSdkVersion(14)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        val release by getting {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildToolsVersion = "28.0.3"

}


dependencies {
//    implementation (fileTree(dir to "libs", include to ["*.jar"]))

    implementation( "androidx.appcompat:appcompat:$appCompatVersion")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    testImplementation( "junit:junit:4.12")

    implementation("org.zeroturnaround:zt-zip:1.13")
}

afterEvaluate {

    publishing {
        publications {
            // Creates a Maven publication called "release".
            create<MavenPublication>("release"){
                // Applies the component for the release build variant.
                from(components["release"])

                // You can then customize attributes of the publication as shown below.
                artifactId = "xapkinstaller"
            }
        }
    }

}