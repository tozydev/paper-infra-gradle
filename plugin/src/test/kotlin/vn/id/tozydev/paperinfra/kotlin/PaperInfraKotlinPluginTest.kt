package vn.id.tozydev.paperinfra.kotlin

import org.gradle.api.artifacts.ArtifactRepositoryContainer
import org.gradle.testfixtures.ProjectBuilder
import vn.id.tozydev.paperinfra.base.PaperInfraBaseProjectPlugin
import kotlin.test.Test
import kotlin.test.assertNotNull

class PaperInfraKotlinPluginTest {
    @Test
    fun `plugin should apply kotlin jvm plugin`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("vn.id.tozydev.paper-infra.kotlin")

        assertNotNull(project.plugins.findPlugin("org.jetbrains.kotlin.jvm"))
    }

    @Test
    fun `plugin should apply base project plugin`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("vn.id.tozydev.paper-infra.kotlin")

        assertNotNull(project.plugins.findPlugin(PaperInfraBaseProjectPlugin::class.java))
    }

    @Test
    fun `plugin should apply paper-infra java plugin`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("vn.id.tozydev.paper-infra.kotlin")

        assertNotNull(project.plugins.findPlugin("vn.id.tozydev.paper-infra.java"))
    }

    @Test
    fun `plugin should add mavenCentral`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("vn.id.tozydev.paper-infra.kotlin")

        val repositories = project.repositories
        assertNotNull(repositories.findByName(ArtifactRepositoryContainer.DEFAULT_MAVEN_CENTRAL_REPO_NAME))
    }
}
