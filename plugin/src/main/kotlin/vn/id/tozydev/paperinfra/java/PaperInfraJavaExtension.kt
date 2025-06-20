package vn.id.tozydev.paperinfra.java

import org.gradle.api.provider.Property

interface PaperInfraJavaExtension {
    val version: Property<Int>
}
