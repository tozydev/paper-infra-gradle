@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
    alias(libs.plugins.plugin.publish)
    alias(libs.plugins.maven.publish)
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

    website = findProperty("pluginWebsite")?.toString()
    vcsUrl = findProperty("pluginVcsUrl")?.toString()

    val pluginTags = findProperty("pluginTags")?.toString()?.split(",")?.map { it.trim() }

    setOf("java", "kotlin", "shadow", "paper").forEach {
        plugins.create(
            "paperInfra$it",
            fun PluginDeclaration.() {
                id = "$group.paper-infra.$it"
                displayName = findProperty("plugin.$it.displayName")?.toString()
                description = findProperty("plugin.$it.description")?.toString()
                implementationClass = findProperty("plugin.$it.implementationClass")?.toString()
                tags = pluginTags
            },
        )
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

publishing {
    repositories {
        maven {
            val isSnapshot = version.toString().endsWith("-SNAPSHOT")
            if (isSnapshot) {
                url = uri("https://vela.tozydev.id.vn/snapshots")
                name = "VelaSnapshots"
            } else {
                url = uri("https://vela.tozydev.id.vn/releases")
                name = "VelaReleases"
            }
            credentials {
                username = propertyOrEnv("vela.username")
                password = propertyOrEnv("vela.password")
            }
        }
    }
}

fun Project.propertyOrEnv(name: String): String? = findProperty(name) as String? ?: System.getenv(name.uppercase().replace('.', '_'))
