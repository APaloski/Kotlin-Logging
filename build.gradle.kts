plugins {
    id("com.android.library") version "3.6.0"
    kotlin("multiplatform") version "1.4.21"
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

    jvm("jvmCommon")
    jvm("jvmJdkLogging")
    jvm("jvmSlf4j")
    js(BOTH) {
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

    val autoServiceVersion = "1.0-rc7"

    sourceSets {
        val commonMain by getting

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting

        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val jvmCommonMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }

        val jvmCommonTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val jvmJdkLoggingMain by getting {
            dependsOn(jvmCommonMain)
        }

        val jvmJdkLoggingTest by getting {
            dependsOn(jvmCommonTest)
        }

        val jvmSlf4jMain by getting {
            dependsOn(jvmCommonMain)
            dependencies {
                implementation("org.slf4j:slf4j-api:1.7.30")
            }
        }

        val jvmSlf4jTest by getting {
            dependsOn(jvmCommonTest)
        }

        val jsMain by getting

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