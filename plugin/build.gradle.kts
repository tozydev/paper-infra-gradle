@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
    alias(libs.plugins.plugin.publish)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.shadow.gradle.plugin)
    implementation(libs.paperweight.userdev)
    implementation(libs.run.task)
    implementation(libs.resource.factory)
    implementation(libs.javapoet)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest(libs.versions.kotlin)
        }

        @Suppress("unused")
        val functionalTest by registering(JvmTestSuite::class) {
            useKotlinTest(libs.versions.kotlin)

            dependencies {
                implementation(project())
            }

            targets {
                all {
                    testTask.configure { shouldRunAfter(test) }
                }
            }
        }
    }
}

@Suppress("unused")
gradlePlugin {
    testSourceSets.add(sourceSets["functionalTest"])

    website = "https://github.com/tozydev/paper-infra-gradle"
    vcsUrl = "https://github.com/tozydev/paper-infra-gradle.git"

    val paperInfraJava by plugins.creating {
        id = "$group.paper-infra.java"
        displayName = "Paper Infrastructure Java Plugin"
        description = "A Gradle plugin to set up the Paper Minecraft Java development environment."
        implementationClass = "vn.id.tozydev.paperinfra.java.PaperInfraJavaPlugin"
    }

    val paperInfraKotlin by plugins.creating {
        id = "$group.paper-infra.kotlin"
        displayName = "Paper Infrastructure Kotlin Plugin"
        description = "A Gradle plugin to set up the Paper Minecraft Kotlin development environment."
        implementationClass = "vn.id.tozydev.paperinfra.kotlin.PaperInfraKotlinPlugin"
    }

    val paperInfraShadow by plugins.creating {
        id = "$group.paper-infra.shadow"
        displayName = "Paper Infrastructure Shadow Plugin"
        description = "A Gradle plugin to set up the Paper Minecraft Shadow development environment."
        implementationClass = "vn.id.tozydev.paperinfra.shadow.PaperInfraShadowPlugin"
    }

    val paperInfraPaper by plugins.creating {
        id = "$group.paper-infra.paper"
        displayName = "Paper Infrastructure Paper Plugin"
        description = "A Gradle plugin to set up the Paper Minecraft Paper development environment."
        implementationClass = "vn.id.tozydev.paperinfra.paper.PaperInfraPaperPlugin"
    }
}

tasks {
    check {
        dependsOn(testing.suites.named("functionalTest"))
    }

    validatePlugins {
        enableStricterValidation = true
    }
}
