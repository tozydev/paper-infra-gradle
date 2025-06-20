package vn.id.tozydev.paperinfra.shadow

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

abstract class PaperInfraShadowPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.apply<ShadowPlugin>()
    }
}
