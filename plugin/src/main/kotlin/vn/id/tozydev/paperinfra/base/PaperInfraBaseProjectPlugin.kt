package vn.id.tozydev.paperinfra.base

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

abstract class PaperInfraBaseProjectPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.extensions.create<PaperInfraProjectExtension>("paperInfra")
    }
}
