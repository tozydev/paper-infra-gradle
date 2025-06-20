package vn.id.tozydev.paperinfra.shadow

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

class PaperInfraShadowPluginTest {
    @Test
    fun `should apply shadow plugin`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("java")
        project.plugins.apply("vn.id.tozydev.paper-infra.shadow")

        assertNotNull(project.plugins.findPlugin("com.gradleup.shadow"))
    }
}
