plugins {
    kotlin("multiplatform") version "1.3.72"
    id("com.android.library") version "3.6.0"
    id("maven-publish")
}

group = "io.paloski"
version = "1.1.0"

repositories {
    mavenCentral()
    google()
    jcenter()
}

kotlin {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */

    jvm {
    }
    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        compilations.all {
            kotlinOptions {
                sourceMap = true
                metaInfo = true
            }
        }
    }
    android {
        publishLibraryVariants("release", "debug")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }

        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }

        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

    }
}

publishing {
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/APaloski/Kotlin-Logging")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(14)
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}