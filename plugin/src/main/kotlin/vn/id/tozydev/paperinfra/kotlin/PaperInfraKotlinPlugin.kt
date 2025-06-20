package vn.id.tozydev.paperinfra.kotlin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import vn.id.tozydev.paperinfra.base.PaperInfraBaseProjectPlugin
import vn.id.tozydev.paperinfra.java.PaperInfraJavaPlugin
import vn.id.tozydev.paperinfra.java.configureMavenCentralRepository

abstract class PaperInfraKotlinPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            configurePlugins()
            configureMavenCentralRepository()
        }

    private fun Project.configurePlugins() {
        apply<PaperInfraBaseProjectPlugin>()
        apply<PaperInfraJavaPlugin>()
        apply<KotlinPluginWrapper>()
    }
}
