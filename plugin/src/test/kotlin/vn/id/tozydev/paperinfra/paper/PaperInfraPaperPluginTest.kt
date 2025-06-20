package vn.id.tozydev.paperinfra.paper

import org.gradle.testfixtures.ProjectBuilder
import vn.id.tozydev.paperinfra.base.PaperInfraProjectExtension
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class PaperInfraPaperPluginTest {
    @Test
    fun `should apply java plugin`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("java")
        project.plugins.apply("vn.id.tozydev.paper-infra.paper")

        assertNotNull(project.plugins.findPlugin("java"))
        assertNotNull(project.plugins.findPlugin("vn.id.tozydev.paper-infra.java"))
    }

    @Test
    fun `should apply paperweight plugin`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("vn.id.tozydev.paper-infra.paper")

        assertNotNull(project.plugins.findPlugin("io.papermc.paperweight.userdev"))
    }

    @Test
    fun `should create paper infra paper extension`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("vn.id.tozydev.paper-infra.paper")

        val paperInfraProjectExtension = project.extensions.getByType(PaperInfraProjectExtension::class.java)

        val extension = paperInfraProjectExtension.extensions.getByName("paper")
        assertNotNull(extension)
        assertIs<PaperInfraPaperExtension>(extension)

        with(extension) {
            assertNotNull(minecraftVersion.orNull)
            assertNotNull(runServer.directory.orNull)
            assertNotNull(runServer.hotswap.orNull)
            assertNotNull(runServer.acceptEula.orNull)
        }
    }

    @Test
    fun `should apply run paper plugin`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("vn.id.tozydev.paper-infra.paper")

        assertNotNull(project.plugins.findPlugin("xyz.jpenilla.run-paper"))
    }

    @Test
    fun `should apply resource factory plugin`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("vn.id.tozydev.paper-infra.paper")

        assertNotNull(project.plugins.findPlugin("xyz.jpenilla.resource-factory"))
    }
}
