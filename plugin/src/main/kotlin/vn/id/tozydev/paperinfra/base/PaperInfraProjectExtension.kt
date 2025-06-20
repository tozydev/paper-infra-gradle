package vn.id.tozydev.paperinfra.base

import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property

interface PaperInfraProjectExtension : ExtensionAware {
    val name: Property<String>
}
