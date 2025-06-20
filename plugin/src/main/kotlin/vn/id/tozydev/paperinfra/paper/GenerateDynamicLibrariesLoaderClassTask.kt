package vn.id.tozydev.paperinfra.paper

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject
import kotlin.io.path.createDirectories

@CacheableTask
abstract class GenerateDynamicLibrariesLoaderClassTask : DefaultTask() {
    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val className: Property<String>

    @get:Input
    abstract val repositories: MapProperty<String, String>

    @get:Input
    abstract val dependencies: ListProperty<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Inject
    abstract val fsOps: FileSystemOperations

    @TaskAction
    fun run() {
        val output = outputDir.get().asFile.toPath()
        fsOps.delete { delete(output) }

        output.createDirectories()

        val builder = DynamicLibrariesLoaderClassBuilder()
        builder.packageName = packageName.get()
        builder.className = className.get()
        builder.repositories = repositories.get()
        builder.dependencies = dependencies.get()

        builder.writeToPath(output)
    }
}
