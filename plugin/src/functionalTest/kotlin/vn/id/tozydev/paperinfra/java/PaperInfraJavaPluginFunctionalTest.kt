package vn.id.tozydev.paperinfra.java

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.io.TempDir
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PaperInfraJavaPluginFunctionalTest {
    @field:TempDir
    lateinit var projectDir: File

    private val settingsFile by lazy { projectDir.resolve("settings.gradle.kts") }
    private val buildFile by lazy { projectDir.resolve("build.gradle.kts") }

    @Test
    fun `build should success`() {
        // Set up the test build
        settingsFile.writeText("")

        buildFile.writeText(
            """
            plugins {
                id("vn.id.tozydev.paper-infra.java")
            }
            """.trimIndent(),
        )

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withProjectDir(projectDir)
        runner.withArguments("build")
        val result = runner.build()

        // Verify the result
        val task = result.task(":build")
        assertNotNull(task)
        assertEquals(TaskOutcome.SUCCESS, task.outcome)
    }
}
