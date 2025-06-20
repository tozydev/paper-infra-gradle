package vn.id.tozydev.paperinfra.java

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import vn.id.tozydev.paperinfra.base.PaperInfraBaseProjectPlugin
import vn.id.tozydev.paperinfra.base.PaperInfraProjectExtension

abstract class PaperInfraJavaPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            applyPlugins()
            configureMavenCentralRepository()

            val extension = registerExtension()
            applyConfigurations(extension)
        }

    private fun Project.applyPlugins() {
        apply<PaperInfraBaseProjectPlugin>()
        apply<JavaPlugin>()
    }

    private fun Project.registerExtension(): PaperInfraJavaExtension =
        extensions
            .getByType<PaperInfraProjectExtension>()
            .extensions
            .create<PaperInfraJavaExtension>("java")
            .apply {
                version.convention(DEFAULT_JAVA_VERSION)
            }

    private fun Project.applyConfigurations(extension: PaperInfraJavaExtension) {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.convention(extension.version.map { JavaLanguageVersion.of(it) })
            }
        }

        afterEvaluate {
            tasks.named<JavaCompile>("compileJava") {
                options.release.convention(extension.version)
                options.encoding = Charsets.UTF_8.name()
            }
        }
    }

    companion object {
        const val DEFAULT_JAVA_VERSION = 21
    }
}
