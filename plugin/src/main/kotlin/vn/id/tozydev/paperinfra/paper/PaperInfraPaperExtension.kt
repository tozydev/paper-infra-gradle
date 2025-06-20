package vn.id.tozydev.paperinfra.paper

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml
import xyz.jpenilla.runtask.pluginsapi.DownloadPluginsSpec
import javax.inject.Inject

interface PaperInfraPaperExtension {
    val minecraftVersion: Property<String>

    @get:Nested
    val plugin: PaperPluginYaml

    fun plugin(configure: PaperPluginYaml.() -> Unit) {
        plugin.configure()
    }

    @get:Nested
    val runServer: RunServerOptions

    fun runServer(configure: RunServerOptions.() -> Unit) {
        runServer.configure()
    }
}

interface RunServerOptions {
    val acceptEula: Property<Boolean>

    val directory: DirectoryProperty

    val hotswap: Property<Boolean>

    val downloadPluginsSpec: DownloadPluginsSpec

    fun downloadPlugins(configure: DownloadPluginsSpec.() -> Unit) {
        downloadPluginsSpec.configure()
    }
}

internal abstract class PaperInfraPaperExtensionImpl
    @Inject
    constructor(
        override val runServer: RunServerOptions,
    ) : PaperInfraPaperExtension

internal abstract class RunServerOptionsImpl
    @Inject
    constructor(
        override val downloadPluginsSpec: DownloadPluginsSpec,
    ) : RunServerOptions
