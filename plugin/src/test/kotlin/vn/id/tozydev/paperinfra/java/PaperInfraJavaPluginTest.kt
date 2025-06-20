package vn.id.tozydev.paperinfra.java

import org.gradle.api.artifacts.ArtifactRepositoryContainer
import org.gradle.testfixtures.ProjectBuilder
import vn.id.tozydev.paperinfra.base.PaperInfraBaseProjectPlugin
import vn.id.tozydev.paperinfra.base.PaperInfraProjectExtension
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class PaperInfraJavaPluginTest {
    @Test
    fun `plugin should apply java plugin`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("vn.id.tozydev.paper-infra.java")

        assertNotNull(project.plugins.findPlugin("java"))
    }

    @Test
    fun `plugin should apply base project plugin`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("vn.id.tozydev.paper-infra.java")

        assertNotNull(project.plugins.findPlugin(PaperInfraBaseProjectPlugin::class.java))
    }

    @Test
    fun `plugin should create extension with conventions`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("vn.id.tozydev.paper-infra.java")

        val paperInfraProjectExtension = project.extensions.findByName("paperInfra")
        assertNotNull(paperInfraProjectExtension)

        assertIs<PaperInfraProjectExtension>(paperInfraProjectExtension)

        val extension = paperInfraProjectExtension.extensions.findByName("java")
        assertNotNull(extension)

        assertIs<PaperInfraJavaExtension>(extension)
        with(extension) {
            assertNotNull(version.orNull)
        }
    }

    @Test
    fun `plugin should add mavenCentral`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("vn.id.tozydev.paper-infra.java")

        val repositories = project.repositories
        assertNotNull(repositories.findByName(ArtifactRepositoryContainer.DEFAULT_MAVEN_CENTRAL_REPO_NAME))
    }
}
