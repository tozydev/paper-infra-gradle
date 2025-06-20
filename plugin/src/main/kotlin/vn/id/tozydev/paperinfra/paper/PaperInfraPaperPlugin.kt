package vn.id.tozydev.paperinfra.paper

import io.papermc.paperweight.userdev.PaperweightUser
import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
import io.papermc.paperweight.userdev.PaperweightUserExtension
import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactRepositoryContainer
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.attributes.Category
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.jvm.toolchain.JvmVendorSpec
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer
import org.gradle.kotlin.dsl.withType
import vn.id.tozydev.paperinfra.base.PaperInfraBaseProjectPlugin
import vn.id.tozydev.paperinfra.base.PaperInfraProjectExtension
import vn.id.tozydev.paperinfra.java.PaperInfraJavaExtension
import vn.id.tozydev.paperinfra.java.PaperInfraJavaPlugin
import xyz.jpenilla.resourcefactory.ResourceFactoryExtension
import xyz.jpenilla.resourcefactory.ResourceFactoryPlugin
import xyz.jpenilla.runpaper.RunPaperPlugin
import xyz.jpenilla.runpaper.task.RunServer
import xyz.jpenilla.runtask.pluginsapi.DownloadPluginsSpec
import xyz.jpenilla.runtask.pluginsapi.PluginApi

abstract class PaperInfraPaperPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit =
        with(target) {
            applyPlugins()
            val extension = registerExtension()
            configurePaperweight(extension)
            configureRunPaper(extension)
            configureResourceFactory(extension)
            configureDynamicLibrariesLoader(extension)
        }

    private fun Project.applyPlugins() {
        apply<PaperInfraBaseProjectPlugin>()
        apply<PaperInfraJavaPlugin>()
        apply<PaperweightUser>()
        apply<RunPaperPlugin>()
        apply<ResourceFactoryPlugin>()
    }

    private fun Project.registerExtension(): PaperInfraPaperExtension {
        val runServerOptions =
            objects.newInstance<RunServerOptionsImpl>(
                objects.newInstance(
                    DownloadPluginsSpec::class,
                    objects.polymorphicDomainObjectContainer(
                        PluginApi::class,
                    ),
                ),
            )
        return extensions
            .getByType<PaperInfraProjectExtension>()
            .extensions
            .create(PaperInfraPaperExtension::class, "paper", PaperInfraPaperExtensionImpl::class, runServerOptions)
            .apply {
                minecraftVersion.convention(DEFAULT_MINECRAFT_VERSION)
                plugin.setConventionsFromProjectMeta(this@registerExtension)
                runServer {
                    directory.convention(layout.projectDirectory.dir(".run-server"))
                    hotswap.convention(true)
                    acceptEula.convention(false)
                }
            }
    }

    private fun Project.configurePaperweight(extension: PaperInfraPaperExtension) {
        dependencies {
            configure<PaperweightUserDependenciesExtension> {
                paperDevBundle(extension.minecraftVersion.map { "$it-R0.1-SNAPSHOT" })
            }
        }

        extensions.configure<PaperweightUserExtension> {
            reobfArtifactConfiguration.convention(ReobfArtifactConfiguration.MOJANG_PRODUCTION)
        }
    }

    private fun Project.configureRunPaper(extension: PaperInfraPaperExtension) =
        afterEvaluate {
            val javaToolchains = extensions.getByType<JavaToolchainService>()
            val javaExtension =
                extensions.getByType<PaperInfraProjectExtension>().extensions.getByType<PaperInfraJavaExtension>()
            tasks {
                val runServerOptions = extension.runServer
                named<RunServer>("runServer") {
                    version.convention(extension.minecraftVersion)
                }

                withType<RunServer> {
                    runDirectory.convention(runServerOptions.directory)
                    systemProperties["com.mojang.eula.agree"] = runServerOptions.acceptEula.get()

                    downloadPlugins {
                        from(runServerOptions.downloadPluginsSpec)
                    }

                    if (runServerOptions.hotswap.get()) {
                        javaLauncher.convention(
                            javaToolchains.launcherFor {
                                languageVersion.set(javaExtension.version.map { JavaLanguageVersion.of(it) })
                                @Suppress("UnstableApiUsage")
                                vendor.set(JvmVendorSpec.JETBRAINS)
                            },
                        )
                        jvmArgs("-XX:+AllowEnhancedClassRedefinition")
                    }
                }
            }
        }

    private fun Project.configureResourceFactory(extension: PaperInfraPaperExtension) {
        extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
            extensions.configure<ResourceFactoryExtension> {
                factory(extension.plugin.resourceFactory())
            }
        }
    }

    private fun Project.configureDynamicLibrariesLoader(extension: PaperInfraPaperExtension) {
        fun collectMavenRepositories() =
            repositories
                .filterIsInstance<MavenArtifactRepository>()
                .distinctBy { it.url.toASCIIString() }

        fun ResolvedDependencyResult.containsPlatformVariant() =
            selected.variants.any {
                it.displayName.contains(Category.REGULAR_PLATFORM) || it.displayName.contains(Category.ENFORCED_PLATFORM)
            }

        fun ResolvedComponentResult.collectModuleVersions() =
            dependencies
                .asSequence()
                .filterIsInstance<ResolvedDependencyResult>()
                .filterNot {
                    if (it.containsPlatformVariant()) {
                        logger.info("Ignoring platform dependency: ${it.selected.moduleVersion}")
                        true
                    } else {
                        false
                    }
                }.mapNotNull { it.selected.moduleVersion }

        val library =
            configurations.register("library") {
                configurations[JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME].extendsFrom(this)
            }

        val task =
            tasks.register(
                "generateDynamicLibrariesLoaderClass",
                GenerateDynamicLibrariesLoaderClassTask::class.java,
            ) {
                description = "Generates a dynamic libraries PluginLoader class for Paper plugin."
                packageName.convention(DEFAULT_LOADER_PACKAGE)
                className.convention(DEFAULT_LOADER_CLASS_NAME)
            }

        extension.plugin {
            loader.convention("$DEFAULT_LOADER_PACKAGE.$DEFAULT_LOADER_CLASS_NAME")
        }

        afterEvaluate {
            task.configure {
                outputDir.convention(layout.buildDirectory.dir("generated/sources/paperInfra/java"))
                repositories.set(
                    collectMavenRepositories()
                        .associate { it.name to it.url.toASCIIString() },
                )
                repositories.put(
                    ArtifactRepositoryContainer.DEFAULT_MAVEN_CENTRAL_REPO_NAME,
                    "https://maven-central-asia.storage-download.googleapis.com/maven2/",
                )
                dependencies.set(
                    library
                        .flatMap { it.incoming.resolutionResult.rootComponent }
                        .map { result ->
                            result.collectModuleVersions().map { it.toString() }.toList()
                        },
                )
            }

            tasks.named(JavaPlugin.COMPILE_JAVA_TASK_NAME) {
                dependsOn(task)
            }

            extensions.getByType<SourceSetContainer>().named(SourceSet.MAIN_SOURCE_SET_NAME) {
                java {
                    srcDir(task.flatMap { it.outputDir })
                }
            }
        }
    }

    companion object {
        const val DEFAULT_MINECRAFT_VERSION = "1.21.6"

        private const val DEFAULT_LOADER_PACKAGE = "vn.id.tozydev.paperinfra.paper"
        private const val DEFAULT_LOADER_CLASS_NAME = "DynamicLibrariesPluginLoader"
    }
}
